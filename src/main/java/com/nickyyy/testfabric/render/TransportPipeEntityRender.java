package com.nickyyy.testfabric.render;

import com.nickyyy.testfabric.entity.TransportPipeEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static net.minecraft.util.math.RotationAxis.POSITIVE_Y;

public class TransportPipeEntityRender implements BlockEntityRenderer<TransportPipeEntity> {
    private final ItemRenderer renderer;

    public TransportPipeEntityRender(BlockEntityRendererFactory.Context context) {
        this.renderer = context.getItemRenderer();
    }

    @Override
    public void render(TransportPipeEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        double percent = entity.getWorld().getTime() % TransportPipeEntity.MAX_COOLING / 4.0;
        if (entity.display() != ItemStack.EMPTY) {
            double[] movePos = getMovePos(entity, percent);
            matrices.translate(movePos[0], movePos[1], movePos[2]);
        }
        matrices.multiply(POSITIVE_Y.rotationDegrees((entity.getWorld().getTime() + tickDelta) * 4));
        this.renderer.renderItem(entity.display(), ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 0);
        matrices.pop();
    }

    public double[] getMovePos(TransportPipeEntity entity, double percent) {
        double[] pos = new double[3];

        Direction from = entity.from;
        Direction to = entity.to;

        if (percent <= 0.5) {
            switch (from) {
                case NORTH -> {
                    pos[0] = 0.5;
                    pos[1] = 0.5;
                    pos[2] = percent;
                }
                case SOUTH -> {
                    pos[0] = 0.5;
                    pos[1] = 0.5;
                    pos[2] = 1 - percent;
                }
                case EAST -> {
                    pos[0] = 1 - percent;
                    pos[1] = 0.5;
                    pos[2] = 0.5;
                }
                case WEST -> {
                    pos[0] = percent;
                    pos[1] = 0.5;
                    pos[2] = 0.5;
                }
                case UP -> {
                    pos[0] = 0.5;
                    pos[1] = 1 - percent;
                    pos[2] = 0.5;
                }
                case DOWN -> {
                    pos[0] = 0.5;
                    pos[1] = percent;
                    pos[2] = 0.5;
                }
            }
        } else {
            switch (to) {
                case NORTH -> {
                    pos[0] = 0.5;
                    pos[1] = 0.5;
                    pos[2] = 1 - percent;
                }
                case SOUTH -> {
                    pos[0] = 0.5;
                    pos[1] = 0.5;
                    pos[2] = percent;
                }
                case EAST -> {
                    pos[0] = percent;
                    pos[1] = 0.5;
                    pos[2] = 0.5;
                }
                case WEST -> {
                    pos[0] = 1 - percent;
                    pos[1] = 0.5;
                    pos[2] = 0.5;
                }
                case UP -> {
                    pos[0] = 0.5;
                    pos[1] = percent;
                    pos[2] = 0.5;
                }
                case DOWN -> {
                    pos[0] = 0.5;
                    pos[1] = 1 - percent;
                    pos[2] = 0.5;
                }
            }
        }

        return pos;
    }

    public Vec3d getFromVec(Direction direction) {
        return switch (direction) {
            case NORTH -> new Vec3d(0.5, 0.5, 1);
            default -> new Vec3d(0, 0, 0);
        };
    }
}
