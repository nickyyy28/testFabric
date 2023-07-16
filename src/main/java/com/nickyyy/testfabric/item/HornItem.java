package com.nickyyy.testfabric.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HornItem extends Item {
    public HornItem(Settings settings) {
        super(settings);

    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        say(user, "Hello World!");                  //发送信息
        user.getItemCooldownManager().set(this, 10);    //设置冷却时间10tick
        return super.use(world, user, hand);
    }

    private void say(@NotNull PlayerEntity player, @NotNull String str) {
        player.sendMessage(Text.literal(str));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.testfabric.horn.tooltip").formatted(Formatting.BLUE));
    }
}
