package com.nickyyy.testfabric.client;

import com.nickyyy.testfabric.entity.ModEntities;
import com.nickyyy.testfabric.render.DisplayBlockEntityRender;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class TestFabricClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ModEntities.DISPLAY_BLOCK_ENTITY, DisplayBlockEntityRender::new);
    }
}
