package com.nickyyy.testfabric.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class PipeFilterScreen extends HandledScreen<PipeFilterScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("testfabric", "textures/gui/container/pipe_filter.png");
    public PipeFilterScreen(PipeFilterScreenHandler handler, PlayerInventory inventory, Text title) {
//        super(handler, inventory, getPositionText(handler).orElse(title));
        super(handler, inventory, Text.translatable("block.testfabric.pipe_filter"));
    }

    //此方法将尝试从 ScreenHandler 获取位置，因为 ScreenRendering 仅发生在客户端上，
    //我们在此处获取具有正确 BlockPos 的 ScreenHandler 实例！
    private static Optional<Text> getPositionText(ScreenHandler handler) {
        if (handler instanceof PipeFilterScreenHandler) {
            BlockPos pos = ((PipeFilterScreenHandler) handler).getPos();
            return pos != null ? Optional.of(Text.of("(" + pos.toShortString() + ")")) : Optional.empty();
        } else {
            return Optional.empty();
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
//        context.drawText()
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }
}
