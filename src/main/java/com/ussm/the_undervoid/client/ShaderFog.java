package com.ussm.the_undervoid.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.ussm.the_undervoid.Config;
import com.ussm.the_undervoid.UnderVoidMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4fStack;

import java.lang.reflect.Method;

// avec des shaders (iris) le brouillard du jeu passe a la trappe, chaque pack fait le sien.
// du coup on dessine des bulles transparentes autour de la camera, ca cache ce qui est loin quel que soit le pack
@EventBusSubscriber(modid = UnderVoidMod.MODID, value = Dist.CLIENT)
public class ShaderFog {
    private static final int SHELLS = 12;
    private static final int STACKS = 10;
    private static final int SLICES = 20;

    private static final RenderType FOG = RenderType.create("the_undervoid_shader_fog", DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS, 1 << 16, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .createCompositeState(false));

    // sphere de rayon 1, calculee une fois
    private static final float[][] SPHERE = new float[(STACKS + 1) * (SLICES + 1)][];

    static {
        for (int i = 0; i <= STACKS; i++) {
            float pitch = Mth.PI * i / STACKS - Mth.HALF_PI;
            for (int j = 0; j <= SLICES; j++) {
                float yaw = Mth.TWO_PI * j / SLICES;
                SPHERE[i * (SLICES + 1) + j] = new float[]{Mth.cos(pitch) * Mth.cos(yaw), Mth.sin(pitch), Mth.cos(pitch) * Mth.sin(yaw)};
            }
        }
    }

    private static ClientLevel rainLevel;
    private static boolean irisChecked;
    private static Object iris;
    private static Method packInUse, shadowPass;

    // AFTER_LEVEL arrive apres le "final" d'iris : on dessine par dessus l'image du pack avec le shader vanilla,
    // le depth buffer est encore celui du monde donc les bulles passent juste devant ce qui est loin
    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;
        if (!UndervoidFog.isActive() || !Config.SHADER_FOG.get() || !shadersOn()) return;

        float end = UndervoidFog.fogEnd();
        float start = Math.max(UndervoidFog.fogStart(), end * Config.SHADER_FOG_START.get().floatValue());
        float[] fog = RenderSystem.getShaderFogColor();
        // un peu plus saturé, avec les shaders le gris violet du gloom fait juste gris
        float sat = Config.SHADER_FOG_SATURATION.get().floatValue();
        float lum = 0.299f * fog[0] + 0.587f * fog[1] + 0.114f * fog[2];
        float[] color = {Mth.clamp(lum + (fog[0] - lum) * sat, 0f, 1f), Mth.clamp(lum + (fog[1] - lum) * sat, 0f, 1f), Mth.clamp(lum + (fog[2] - lum) * sat, 0f, 1f)};

        Matrix4fStack modelView = RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        modelView.mul(event.getModelViewMatrix());
        RenderSystem.applyModelViewMatrix();

        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer consumer = buffers.getBuffer(FOG);
        // la bulle k cache k/SHELLS de ce qu'il y a derriere elle, la derniere est opaque
        for (int k = 1; k <= SHELLS; k++) {
            float radius = start + (end - start) * k / SHELLS;
            float alpha = 1f / (SHELLS - k + 1);
            for (int i = 0; i < STACKS; i++) {
                for (int j = 0; j < SLICES; j++) {
                    vertex(consumer, SPHERE[i * (SLICES + 1) + j], radius, color, alpha);
                    vertex(consumer, SPHERE[(i + 1) * (SLICES + 1) + j], radius, color, alpha);
                    vertex(consumer, SPHERE[(i + 1) * (SLICES + 1) + j + 1], radius, color, alpha);
                    vertex(consumer, SPHERE[i * (SLICES + 1) + j + 1], radius, color, alpha);
                }
            }
        }
        buffers.endBatch(FOG);

        modelView.popMatrix();
        RenderSystem.applyModelViewMatrix();
    }

    private static void vertex(VertexConsumer consumer, float[] p, float radius, float[] color, float alpha) {
        consumer.addVertex(p[0] * radius, p[1] * radius, p[2] * radius).setColor(color[0], color[1], color[2], alpha);
    }

    // la dimension est en fixed_time 12000 et les packs la prennent pour l'overworld : ils mettent un soleil couchant,
    // lumiere orange qui vient d'un coté. si le client croit qu'il pleut ils passent en temps couvert (lumiere grise, diffuse)
    // et comme nos biomes ont pas de precipitation il tombe rien et y'a pas de son
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        ClientLevel level = Minecraft.getInstance().level;
        boolean wanted = level != null && UndervoidFog.isActive() && Config.SHADER_FOG.get() && packInUse();
        if (wanted) {
            level.setRainLevel(Config.SHADER_OVERCAST.get().floatValue());
            rainLevel = level;
        } else if (rainLevel != null) {
            // seulement si on est toujours dans le meme monde, sinon c'est le serveur qui gere la pluie
            if (rainLevel == level) level.setRainLevel(0f);
            rainLevel = null;
        }
    }

    public static boolean isRainForced() {
        return rainLevel != null;
    }

    public static float forcedRain() {
        return rainLevel != null ? Config.SHADER_OVERCAST.get().floatValue() : 0f;
    }

    private static boolean shadersOn() {
        if (!packInUse()) return false;
        try {
            return !(boolean) shadowPass.invoke(iris);
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }

    // iris est pas une dependance, on passe par la reflexion
    private static boolean packInUse() {
        if (!irisChecked) {
            irisChecked = true;
            try {
                Class<?> api = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
                iris = api.getMethod("getInstance").invoke(null);
                packInUse = api.getMethod("isShaderPackInUse");
                shadowPass = api.getMethod("isRenderingShadowPass");
            } catch (ReflectiveOperationException | LinkageError e) {
                iris = null;
            }
        }
        if (iris == null) return false;
        try {
            return (boolean) packInUse.invoke(iris);
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }
}
