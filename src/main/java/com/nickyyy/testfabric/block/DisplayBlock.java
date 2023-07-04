package com.nickyyy.testfabric.block;

import com.nickyyy.testfabric.entity.DisplayBlockEntity;
import com.nickyyy.testfabric.entity.ModEntities;
import com.nickyyy.testfabric.util.ModLog;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DisplayBlock extends BlockWithEntity implements BlockEntityProvider {
    protected DisplayBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DisplayBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient()) {
            Inventory inventory = (Inventory) world.getBlockEntity(pos);

            if (inventory == null) return ActionResult.FAIL;

            if (!player.getStackInHand(hand).isEmpty()) {
                if (inventory.getStack(0).isEmpty()) {
                    ItemStack itemStack = player.getStackInHand(hand).copy();
                    itemStack.setCount(1);
                    inventory.setStack(0, itemStack);
                    player.getStackInHand(hand).setCount(player.getStackInHand(hand).getCount() - 1);
                } else {
                    ModLog.LOGGER.warn("The first slot holds "
                            + inventory.getStack(0));
                }
            } else {
                if (!inventory.getStack(0).isEmpty()) {
                    player.getInventory().offerOrDrop(inventory.getStack(0));
                    inventory.removeStack(0);
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModEntities.DISPLAY_BLOCK_ENTITY, DisplayBlockEntity::tick);
    }
}
