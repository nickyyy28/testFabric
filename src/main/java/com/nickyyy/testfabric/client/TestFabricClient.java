package com.nickyyy.testfabric.client;

import com.nickyyy.testfabric.entity.ModEntities;
import com.nickyyy.testfabric.render.DisplayBlockEntityRender;
import com.nickyyy.testfabric.render.TransportPipeEntityRender;
import com.nickyyy.testfabric.screen.ModScreenHandlers;
import com.nickyyy.testfabric.screen.TransportCombinerScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

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
    }
}
