package com.nickyyy.testfabric.render;

import com.nickyyy.testfabric.entity.TransportPipeEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class TransportPipeEntityRender implements BlockEntityRenderer<TransportPipeEntity> {
    private final ItemRenderer renderer;

    public TransportPipeEntityRender(BlockEntityRendererFactory.Context context) {
        this.renderer = context.getItemRenderer();
    }

    @Override
    public void render(TransportPipeEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

    }
}
