package com.ussm.examplemod.attachment;

import com.mojang.serialization.Codec;
import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.item.custom.KatanaDash;
import com.ussm.examplemod.sanity.Sanity;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ExampleMod.MODID);

    public static final Supplier<AttachmentType<Float>> SANITY = ATTACHMENT_TYPES.register("sanity",
            () -> AttachmentType.builder(() -> Sanity.MAX).serialize(Codec.FLOAT).sync((holder, to) -> holder == to, ByteBufCodecs.FLOAT).build());

    public static final Supplier<AttachmentType<Integer>> VOID_TICKS = ATTACHMENT_TYPES.register("void_ticks",
            () -> AttachmentType.builder(() -> 0).build());

    // pommes dorees mangees pour le vide (combien + quand la derniere)
    public static final Supplier<AttachmentType<Integer>> VOID_APPLES = ATTACHMENT_TYPES.register("void_apples",
            () -> AttachmentType.builder(() -> 0).build());
    public static final Supplier<AttachmentType<Long>> VOID_APPLE_TIME = ATTACHMENT_TYPES.register("void_apple_time",
            () -> AttachmentType.builder(() -> 0L).build());

    public static final Supplier<AttachmentType<KatanaDash>> KATANA_DASH = ATTACHMENT_TYPES.register(
            "katana_dash",
            () -> AttachmentType.<KatanaDash>builder(() -> null).build()
    );

    public static final Supplier<AttachmentType<Long>> KATANA_PARRY = ATTACHMENT_TYPES.register(
            "katana_parry",
            () -> AttachmentType.builder(() -> 0L).build()
    );

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
