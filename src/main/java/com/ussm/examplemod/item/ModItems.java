package com.ussm.examplemod.item;

import com.ussm.examplemod.ExampleMod;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.ussm.examplemod.item.custom.*;
import com.ussm.examplemod.entity.ModEntities;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.minecraft.world.level.block.Blocks;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ExampleMod.MODID);
    //Enregistrer les Items ici
    //-------------------------------------GENERAL-------------------------------------------------------

    public static final DeferredItem<Item> SPAWNER_RECEPTACLE = ITEMS.register("spawner_receptacle",
            () -> new SpawnerReceptacle(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)));

    public static final DeferredItem<Item> CAPTURED_SPAWNER = ITEMS.register("captured_spawner",
            () -> new CapturedSpawnerItem(Blocks.SPAWNER, new Item.Properties()));

    public static final DeferredItem<Item> HAZMAT_FABRIC = ITEMS.register("hazmat_fabric",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> HAZMAT_MASK = ITEMS.register("hazmat_mask",
            () -> new HazmatArmorItem(ArmorItem.Type.HELMET, new Item.Properties()));
    public static final DeferredItem<Item> HAZMAT_SUIT = ITEMS.register("hazmat_suit",
            () -> new HazmatArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final DeferredItem<Item> HAZMAT_LEGGINGS = ITEMS.register("hazmat_leggings",
            () -> new HazmatArmorItem(ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final DeferredItem<Item> HAZMAT_BOOTS = ITEMS.register("hazmat_boots",
            () -> new HazmatArmorItem(ArmorItem.Type.BOOTS, new Item.Properties()));

    public static final DeferredItem<Item> BLADE_FRAGMENT = ITEMS.register("blade_fragment",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> KATANA = ITEMS.register("katana",
            () -> new KatanaItem(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> UNSTABLE_MATTER = ITEMS.register("unstable_matter",
            () -> new UnstableMatter(new Item.Properties()));

    public static final DeferredItem<Item> VOID_MATTER = ITEMS.register("void_matter",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> VOID_STABILIZER = ITEMS.register("void_stabilizer",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> VOID_BALL = ITEMS.register("void_ball",
            () -> new VoidBallItem(new Item.Properties()));

    public static final DeferredItem<Item> VOID_BOAT = ITEMS.register("void_boat",
            () -> new ModBoatItem(false, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> VOID_CHEST_BOAT = ITEMS.register("void_chest_boat",
            () -> new ModBoatItem(true, new Item.Properties().stacksTo(1)));


    public static final DeferredItem<Item> HEDGEHOG_SPAWN_EGG = ITEMS.register("hedgehog_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.HEDGEHOG, 0x6b4f3a, 0xd9c7a6,
                    new Item.Properties()));
    public static final DeferredItem<Item> MIMIC_SPAWN_EGG = ITEMS.register("mimic_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.MIMIC, 0x2e2836, 0x8e8a99,
                    new Item.Properties()));

    public static final DeferredItem<Item> SMOKE_BOSS_SPAWN_EGG = ITEMS.register("smoke_boss_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SMOKE_BOSS, 0x1B1B22, 0x6B6B7A, new Item.Properties()));

    public static final DeferredItem<Item> COCKROACH_SPAWN_EGG = ITEMS.register("cockroach_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.COCKROACH, 0x3a2a06, 0x614400, new Item.Properties()));
    public static final DeferredItem<Item> GLOOM_BIRD_SPAWN_EGG = ITEMS.register("gloom_bird_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.GLOOM_BIRD, 0xd3d3d3, 0xe08a00, new Item.Properties()));
    public static final DeferredItem<Item> BLOOM_BUTTERFLY_SPAWN_EGG = ITEMS.register("bloom_butterfly_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.BLOOM_BUTTERFLY, 0xeef4fa, 0xd01010, new Item.Properties()));
    public static final DeferredItem<Item> FLOATING_LIGHT_SPAWN_EGG = ITEMS.register("floating_light_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.FLOATING_LIGHT, 0xf5ef10, 0x2a2a2a, new Item.Properties()));
    public static final DeferredItem<Item> CARNIVOROUS_PLANT_SPAWN_EGG = ITEMS.register("carnivorous_plant_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.CARNIVOROUS_PLANT, 0x1fae1f, 0xff5a10, new Item.Properties()));
    //>-------------------------------------------------------
    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}