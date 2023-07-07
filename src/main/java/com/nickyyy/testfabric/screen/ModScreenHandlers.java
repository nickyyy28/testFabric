package com.nickyyy.testfabric.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<TransportCombinerScreenHandler> TRANSPORT_COMBINER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier("testfabric", "transport_combiner"), TransportCombinerScreenHandler::new);
    public static final ScreenHandlerType<TransportPipeScreenHandler> TRANSPORT_PIPE_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier("testfabric", "transport_pipe"), TransportPipeScreenHandler::new);
}
