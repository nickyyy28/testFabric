package com.nickyyy.testfabric.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModItem extends Item {

    private ToolTipProvider provider = null;

    public ModItem(Settings settings) {
        super(settings);
    }

    public ModItem setToolTip(ToolTipProvider provider) {
        this.provider = provider;
        return this;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (this.provider != null) {
            provider.provide(stack, world, tooltip, context);
        }
    }

    public interface ToolTipProvider{
        void provide(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context);
    }
}
