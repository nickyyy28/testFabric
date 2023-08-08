package com.nickyyy.testfabric.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CopperCableEntity extends BaseCableEntity{

    public static final float RESISTANCE = 10.0f;

    public CopperCableEntity(BlockPos pos, BlockState state) {
        super(ModEntities.COPPER_CABLE_ENTITY, pos, state);
    }

    @Override
    public float getResistance() {
        return RESISTANCE;
    }

    public static void server_tick(World world, BlockPos pos, BlockState state, CopperCableEntity entity) {

    }
}
