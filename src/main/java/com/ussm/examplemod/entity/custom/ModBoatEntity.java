package com.ussm.examplemod.entity.custom;

import com.ussm.examplemod.block.ModBlocks;
import com.ussm.examplemod.entity.ModEntities;
import com.ussm.examplemod.item.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

// version kaupenjoe sans le enum Type, on a qu'un seul bois de toute facon
public class ModBoatEntity extends Boat {
    public ModBoatEntity(EntityType<? extends Boat> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ModBoatEntity(Level level, double pX, double pY, double pZ) {
        this(ModEntities.VOID_BOAT.get(), level);
        this.setPos(pX, pY, pZ);
        this.xo = pX;
        this.yo = pY;
        this.zo = pZ;
    }

    @Override
    public Item getDropItem() {
        return ModItems.VOID_BOAT.get();
    }

    // quand il se casse en tombant de haut le boat vanilla lache des planches de chene (variant OAK)
    // du coup on les remplace par les notres
    @Override
    public ItemEntity spawnAtLocation(ItemLike pItem, int pOffsetY) {
        //System.out.println("drop " + pItem);
        if (pItem == Blocks.OAK_PLANKS) {
            pItem = ModBlocks.VOID_PLANKS.get();
        }
        return super.spawnAtLocation(pItem, pOffsetY);
    }
}
