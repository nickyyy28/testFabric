package com.nickyyy.testfabric.screen;

import com.nickyyy.testfabric.util.ModLog;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class PipeFilterScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private BlockPos blockPos;

    public PipeFilterScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new SimpleInventory(15));
        blockPos = buf.readBlockPos();
    }

    public PipeFilterScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ModScreenHandlers.PIPE_FILTER_SCREEN_HANDLER, syncId);

        this.inventory = inventory;

        // 这会将槽位放置在 3×3 网格的正确位置中。这些槽位在客户端和服务器中都存在！
        // 但是这不会渲染槽位的背景，这是 Screens job
        int m;
        int l;
        //Our inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 3; ++l) {
                this.addSlot(new Slot(inventory, l + m * 3, 17 + l * 18, 17 + m * 18));
            }
        }

        //过滤器
        for(m = 0; m < 2; ++m) {
            for (l = 0 ; l < 3; ++l) {
                this.addSlot(new Slot(inventory, 9 + l + m * 3, 103 + l * 18, 26 + m * 18));
            }
        }

        // 玩家物品栏
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        // 玩家快捷栏
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        ModLog.LOGGER.info("move slot:" + invSlot);
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size() - 6, false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public BlockPos getPos() {
        return blockPos;
    }

}
