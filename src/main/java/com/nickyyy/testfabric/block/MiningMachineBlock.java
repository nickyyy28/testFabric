package com.nickyyy.testfabric.block;

import com.nickyyy.testfabric.entity.MiningMachineBlockEntity;
import com.nickyyy.testfabric.entity.ModEntities;
import com.nickyyy.testfabric.util.ModLog;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MiningMachineBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final int MAX_ENERGY = 500;
    public static final int MIN_ENERGY = 0;

    public static final int ENERGY_INC_STEP = 50;

    public static final BooleanProperty FULLED = BooleanProperty.of("fulled");

    public MiningMachineBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FULLED, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
//        if (world.isClient()) {
//            ModLog.LOGGER.warn("World is Client");
//            return ActionResult.FAIL;
//        }
        Inventory blockEntity = (Inventory) world.getBlockEntity(pos);
        if (blockEntity == null) {
            ModLog.LOGGER.warn("Block Entity is Null");
            return ActionResult.FAIL;
        }

        if (!player.getStackInHand(hand).isEmpty()) {
            if (blockEntity.getStack(0).isEmpty()) {
                blockEntity.setStack(0, player.getStackInHand(hand).copy());
                player.getStackInHand(hand).setCount(0);
            } else if (blockEntity.getStack(1).isEmpty()) {
                blockEntity.setStack(1, player.getStackInHand(hand).copy());
                player.getStackInHand(hand).setCount(0);
            } else {
                ModLog.LOGGER.warn("The first slot holds "
                        + blockEntity.getStack(0) + " and the second slot holds " + blockEntity.getStack(1));
            }
        } else {
            if (!blockEntity.getStack(1).isEmpty()) {
                player.getInventory().offerOrDrop(blockEntity.getStack(1));
                blockEntity.removeStack(1);
            } else if (!blockEntity.getStack(0).isEmpty()) {
                player.getInventory().offerOrDrop(blockEntity.getStack(0));
                blockEntity.removeStack(0);
            }
        }
        return ActionResult.SUCCESS;
    }
//
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FULLED);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MiningMachineBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModEntities.MINING_MACHINE_BLOCK_ENTITY, MiningMachineBlockEntity::tick);
    }
}
