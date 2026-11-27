package com.ussm.the_undervoid.attachment;

import com.mojang.serialization.Codec;
import com.ussm.the_undervoid.UnderVoidMod;
import com.ussm.the_undervoid.item.custom.KatanaDash;
import com.ussm.the_undervoid.sanity.Sanity;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, UnderVoidMod.MODID);

    public static final Supplier<AttachmentType<Float>> SANITY = ATTACHMENT_TYPES.register("sanity",
            () -> AttachmentType.builder(() -> Sanity.MAX).serialize(Codec.FLOAT).sync((holder, to) -> holder == to, ByteBufCodecs.FLOAT).build());

    public record SanityIllusion(float start, float target, int dose, boolean active) {
        private static final StreamCodec<ByteBuf, SanityIllusion> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, SanityIllusion::start,
                ByteBufCodecs.FLOAT, SanityIllusion::target,
                ByteBufCodecs.VAR_INT, SanityIllusion::dose,
                ByteBufCodecs.BOOL, SanityIllusion::active,
                SanityIllusion::new);
    }

    public static final Supplier<AttachmentType<SanityIllusion>> SANITY_ILLUSION =
            ATTACHMENT_TYPES.register("sanity_illusion",
                    () -> AttachmentType.builder(() -> new SanityIllusion(0, 0, 0, false))
                            .sync((holder, to) -> holder == to, SanityIllusion.STREAM_CODEC).build());
    public static final Supplier<AttachmentType<Integer>> VOID_TICKS = ATTACHMENT_TYPES.register("void_ticks",
            () -> AttachmentType.builder(() -> 0).build());

    // pommes dorees mangees pour le vide (combien + quand la derniere)
    public static final Supplier<AttachmentType<Integer>> VOID_APPLES = ATTACHMENT_TYPES.register("void_apples",
            () -> AttachmentType.builder(() -> 0).build());
    public static final Supplier<AttachmentType<Long>> VOID_APPLE_TIME = ATTACHMENT_TYPES.register("void_apple_time",
            () -> AttachmentType.builder(() -> 0L).build());
    public static final Supplier<AttachmentType<Long>> VOID_APPLE_START = ATTACHMENT_TYPES.register("void_apple_start",
            () -> AttachmentType.builder(() -> 0L).build());

    public static final Supplier<AttachmentType<KatanaDash>> KATANA_DASH = ATTACHMENT_TYPES.register(
            "katana_dash",
            () -> AttachmentType.<KatanaDash>builder(() -> null).build()
    );

    public static final Supplier<AttachmentType<Long>> KATANA_PARRY = ATTACHMENT_TYPES.register(
            "katana_parry",
            () -> AttachmentType.builder(() -> 0L).build()
    );

    public static final Supplier<AttachmentType<Long>> KATANA_UP_DASH = ATTACHMENT_TYPES.register(
            "katana_up_dash",
            () -> AttachmentType.builder(() -> 0L).build()
    );

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
