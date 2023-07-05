package com.nickyyy.testfabric.render;

import com.nickyyy.testfabric.entity.DisplayBlockEntity;
import com.nickyyy.testfabric.item.ModItems;
import com.nickyyy.testfabric.util.ModLog;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static net.minecraft.util.math.RotationAxis.POSITIVE_Y;
import static net.minecraft.util.math.RotationAxis.of;

public class DisplayBlockEntityRender implements BlockEntityRenderer<DisplayBlockEntity> {

    private final ItemRenderer renderer;

    public DisplayBlockEntityRender(BlockEntityRendererFactory.Context context) {
        this.renderer = context.getItemRenderer();
    }

    @Override
    public void render(DisplayBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        double offset = Math.sin((entity.getWorld().getTime() + tickDelta) / 8.0) / 8.0;

        matrices.translate(0.5, 0.5 + offset, 0.5);
        matrices.multiply(POSITIVE_Y.rotationDegrees((entity.getWorld().getTime() + tickDelta) * 4));
//        if (entity.itemToDisplay.isEmpty()) {
//            ModLog.LOGGER.info("Empty ItemStack");
//        } else {
//            ModLog.LOGGER.info("Item: " + entity.itemToDisplay.getName());
//        }
        this.renderer.renderItem(entity.itemToDisplay, ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 0);
        matrices.pop();
    }
}
