package com.ussm.examplemod.entity.custom;

import com.ussm.examplemod.block.ModBlocks;
import com.ussm.examplemod.entity.ModEntities;
import com.ussm.examplemod.item.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class ModChestBoatEntity extends ChestBoat {
    public ModChestBoatEntity(EntityType<? extends Boat> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ModChestBoatEntity(Level pLevel, double pX, double pY, double pZ) {
        this(ModEntities.VOID_CHEST_BOAT.get(), pLevel);
        this.setPos(pX, pY, pZ);
        this.xo = pX;
        this.yo = pY;
        this.zo = pZ;
    }

    @Override
    public Item getDropItem() {
        return ModItems.VOID_CHEST_BOAT.get();
    }

    @Override
    public ItemEntity spawnAtLocation(ItemLike pItem, int pOffsetY) {
        if (pItem == Blocks.OAK_PLANKS) {
            pItem = ModBlocks.VOID_PLANKS.get();
        }
        return super.spawnAtLocation(pItem, pOffsetY);
    }
}
