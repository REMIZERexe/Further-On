package com.remizerexe.further_on.content.blast_furnace;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BlastFurnaceHearthScreen extends AbstractContainerScreen<BlastFurnaceHearthMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("further_on", "textures/gui/blast_furnace_hearth.png");

    private static final int GUI_WIDTH  = 220;
    private static final int GUI_HEIGHT = 240;

    public BlastFurnaceHearthScreen(BlastFurnaceHearthMenu menu, Inventory playerInv,
                                    Component title) {
        super(menu, playerInv, title);
        this.imageWidth  = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width  - imageWidth)  / 2;
        int y = (height - imageHeight) / 2;

        // Fond principal
        graphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFF1C1C1C);

        // ── Slot slag (tout à gauche, centré verticalement) ───────────────
        graphics.fill(x + 8, y + 26, x + 26, y + 44, 0xFF333333);
        drawBorder(graphics, x + 8, y + 26, 18, 18, 0xFF666666);

        // ── Tank input coal/iron (2ème quart) ─────────────────────────────
        int inputTankX = x + 34;
        int inputTankY = y + 16;
        int inputTankW = 50;
        int inputTankH = 80;

        graphics.fill(inputTankX, inputTankY,
                inputTankX + inputTankW, inputTankY + inputTankH, 0xFF2A2A2A);

        int maxLayers    = menu.getMaxLayers();
        int filledLayers = menu.getAccumulatedLayers();
        if (maxLayers > 0 && filledLayers > 0) {
            int fillH = (int)((float) filledLayers / maxLayers * inputTankH);
            graphics.fill(inputTankX, inputTankY + inputTankH - fillH,
                    inputTankX + inputTankW, inputTankY + inputTankH,
                    0xFFCC6600);
        }
        drawBorder(graphics, inputTankX, inputTankY, inputTankW, inputTankH, 0xFF888888);

        // ── Tank molten steel (grand, droite) ─────────────────────────────
        int steelTankX = x + 94;
        int steelTankY = y + 16;
        int steelTankW = 118;
        int steelTankH = 80;

        graphics.fill(steelTankX, steelTankY,
                steelTankX + steelTankW, steelTankY + steelTankH, 0xFF1A1A1A);

        int steelAmount   = menu.getSteelAmount();
        int steelCapacity = menu.getSteelCapacity();
        if (steelCapacity > 0 && steelAmount > 0) {
            int fillH = (int)((float) steelAmount / steelCapacity * steelTankH);
            graphics.fill(steelTankX, steelTankY + steelTankH - fillH,
                    steelTankX + steelTankW, steelTankY + steelTankH,
                    0xFFE8A030);
        }
        drawBorder(graphics, steelTankX, steelTankY, steelTankW, steelTankH, 0xFF888888);

        // Séparateur inventaire joueur
        graphics.fill(x + 7, y + 125, x + imageWidth - 7, y + 126, 0xFF444444);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 6, 0xFFFFFF, false);

        graphics.drawString(font,
                "RPM: " + menu.getCurrentRPM() + "  " +
                        menu.getAccumulatedLayers() + "/" + menu.getMaxLayers(),
                34, 100, 0xAAAAAA, false);

        graphics.drawString(font,
                menu.getSteelAmount() + " mb",
                94, 113, 0xE8A030, false);
    }
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private void drawBorder(GuiGraphics graphics, int x, int y, int w, int h, int color) {
        graphics.fill(x,         y,         x + w,     y + 1,     color); // top
        graphics.fill(x,         y + h - 1, x + w,     y + h,     color); // bottom
        graphics.fill(x,         y,         x + 1,     y + h,     color); // left
        graphics.fill(x + w - 1, y,         x + w,     y + h,     color); // right
    }
}