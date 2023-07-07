package com.nickyyy.testfabric.client;

import com.nickyyy.testfabric.entity.ModEntities;
import com.nickyyy.testfabric.entity.TransportPipeEntity;
import com.nickyyy.testfabric.render.DisplayBlockEntityRender;
import com.nickyyy.testfabric.render.TransportPipeEntityRender;
import com.nickyyy.testfabric.screen.ModScreenHandlers;
import com.nickyyy.testfabric.screen.TransportCombinerScreen;
import com.nickyyy.testfabric.util.ModLog;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.impl.screenhandler.client.ClientNetworking;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

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

        ClientPlayNetworking.registerGlobalReceiver(TransportPipeEntity.TRANSPORT_ENTITY_PACKET_ID, ((client, handler, buf, responseSender) -> {
            ItemStack stack = buf.readItemStack();
            BlockPos pos = buf.readBlockPos();
            ModLog.LOGGER.info("client receive msg");

            TransportPipeEntity entity = (TransportPipeEntity) handler.getWorld().getBlockEntity(pos);
            entity.setDisplay(stack);
        }));
    }
}
