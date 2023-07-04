package com.nickyyy.testfabric.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

public class RedStoneTransformEngineBlock extends Block {
    public RedStoneTransformEngineBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        //当实体降落在方块上时调用
        super.onLandedUpon(world, state, pos, entity, fallDistance);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        //当玩家在方块从世界中移除之前打破方块时调用,爆炸不会触发此操作
        super.onBreak(world, pos, state, player);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        //方块被放置时调用
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        //当实体踏上此块时调用
        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        //被爆炸摧毁时
        super.onDestroyedByExplosion(world, pos, explosion);
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        //在玩家打破方块并且该方块从世界中移除后调用。爆炸不会触发此操作。
        super.onBroken(world, pos, state);
    }

    @Override
    public void onEntityLand(BlockView world, Entity entity) {
        //实体降落在方块上后调用。
        super.onEntityLand(world, entity);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        player.sendMessage(Text.of("SPEED: " + player.speed + " Head YAW: " + player.headYaw));
        return super.onUse(state, world, pos, player, hand, hit);
    }
}
