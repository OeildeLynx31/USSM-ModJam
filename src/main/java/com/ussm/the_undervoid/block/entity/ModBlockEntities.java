package com.ussm.the_undervoid.block.entity;

import com.ussm.the_undervoid.UnderVoidMod;
import com.ussm.the_undervoid.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, UnderVoidMod.MODID);

    public static final Supplier<BlockEntityType<SanityTotemBlockEntity>> SANITY_TOTEM =
            BLOCK_ENTITIES.register("sanity_totem", () ->
                    BlockEntityType.Builder.of(SanityTotemBlockEntity::new, ModBlocks.SANITY_TOTEM.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
