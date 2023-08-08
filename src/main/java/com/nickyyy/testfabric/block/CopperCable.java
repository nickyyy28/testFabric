package com.nickyyy.testfabric.block;

import com.nickyyy.testfabric.entity.CopperCableEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class CopperCable extends BaseCable{
    protected CopperCable(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CopperCableEntity(pos, state);
    }
}
