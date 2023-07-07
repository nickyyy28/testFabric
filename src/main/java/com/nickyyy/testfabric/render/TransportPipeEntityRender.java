package com.nickyyy.testfabric.render;

import com.nickyyy.testfabric.entity.TransportPipeEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;

import static net.minecraft.util.math.RotationAxis.POSITIVE_Y;

public class TransportPipeEntityRender implements BlockEntityRenderer<TransportPipeEntity> {
    private final ItemRenderer renderer;

    public TransportPipeEntityRender(BlockEntityRendererFactory.Context context) {
        this.renderer = context.getItemRenderer();
    }

    @Override
    public void render(TransportPipeEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(POSITIVE_Y.rotationDegrees((entity.getWorld().getTime() + tickDelta) * 4));
        this.renderer.renderItem(entity.display(), ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 0);
        matrices.pop();
    }
}
