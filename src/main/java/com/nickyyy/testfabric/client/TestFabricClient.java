package com.nickyyy.testfabric.client;

import com.nickyyy.testfabric.entity.ModEntities;
import com.nickyyy.testfabric.entity.TransportPipeBlockEntity;
import com.nickyyy.testfabric.living_entity.ModLivingEntities;
import com.nickyyy.testfabric.living_entity_renderer.CubeEntityRenderer;
import com.nickyyy.testfabric.render.DisplayBlockEntityRender;
import com.nickyyy.testfabric.render.TransportPipeEntityRender;
import com.nickyyy.testfabric.screen.ModScreenHandlers;
import com.nickyyy.testfabric.screen.PipeFilterScreen;
import com.nickyyy.testfabric.screen.TransportCombinerScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class TestFabricClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ModEntities.DISPLAY_BLOCK_ENTITY, DisplayBlockEntityRender::new);
        BlockEntityRendererFactories.register(ModEntities.TRANSPORT_PIPE_ENTITY, TransportPipeEntityRender::new);

        ScreenRegistry.register(ModScreenHandlers.TRANSPORT_COMBINER_SCREEN_HANDLER, TransportCombinerScreen::new);
        ScreenRegistry.register(ModScreenHandlers.PIPE_FILTER_SCREEN_HANDLER, PipeFilterScreen::new);

        EntityRendererRegistry.register(ModLivingEntities.CUB_ENTITY, CubeEntityRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(TransportPipeBlockEntity.TRANSPORT_ENTITY_PACKET_ID, ((client, handler, buf, responseSender) -> {
            ItemStack stack = buf.readItemStack();
            BlockPos pos = buf.readBlockPos();
            Direction from = TransportPipeBlockEntity.PacketReadDirection(buf);
            Direction to = TransportPipeBlockEntity.PacketReadDirection(buf);

            TransportPipeBlockEntity entity = (TransportPipeBlockEntity) handler.getWorld().getBlockEntity(pos);
            if (entity == null) return;
            entity.setDisplay(stack);
            entity.from = from;
            entity.to = to;
        }));
    }
}
