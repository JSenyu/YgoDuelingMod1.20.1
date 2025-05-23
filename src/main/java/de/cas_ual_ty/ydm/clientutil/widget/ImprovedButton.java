package de.cas_ual_ty.ydm.clientutil.widget;

import de.cas_ual_ty.ydm.duel.screen.widget.IWidgetToolTip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

/**
 * ImprovedButton adapted for Minecraft 1.20.1+
 */
public class ImprovedButton extends Button implements IWidgetToolTip {
    protected final ITooltip tooltip;
    /**
     * Basic constructor. Tooltip should be handled externally if needed.
     */
    public ImprovedButton(int x, int y, int width, int height,
                          Component title, OnPress onPress) {
        super(Button.builder(title, onPress)
                .pos(x, y)
                .size(width, height));
        this.tooltip = null;
    }

    public ImprovedButton(int x, int y, int width, int height,
                          Component title, OnPress onPress, ITooltip tooltip) {
        super(Button.builder(title, onPress)
                .pos(x, y)
                .size(width, height));
        this.tooltip = tooltip;
    }

    @Override
    protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontrenderer = minecraft.font;
//        RenderSystem.setShaderTexture(0, AbstractWidget.WIDGETS_LOCATION);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        int i = getYImage(this, isHoveredOrFocused());
        int x = this.getX();
        int y = this.getY();
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.enableDepthTest();
        gui.blit(WIDGETS_LOCATION, x, y, 0, 46 + i * 20, width / 2, height / 2);
        gui.blit(WIDGETS_LOCATION, x + width / 2, y, 200 - width / 2, 46 + i * 20, width / 2, height / 2);
        gui.blit(WIDGETS_LOCATION, x, y + height / 2, 0, 46 + (i + 1) * 20 - height / 2, width / 2, height / 2);
        gui.blit(WIDGETS_LOCATION, x + width / 2, y + height / 2, 200 - width / 2, 46 + (i + 1) * 20 - height / 2, width / 2, height / 2);
//        renderBg(ms, minecraft, mouseX, mouseY);

        int j = getFGColor();
        gui.drawCenteredString(fontrenderer, getMessage(), x + width / 2, y + (height - 8) / 2, j | Mth.ceil(alpha * 255.0F) << 24);

        if(isHoveredOrFocused() && this.tooltip != null) {

            if(this.isMouseOver(mouseX, mouseY)){
                this.renderToolTip(gui, mouseX, mouseY);
            }
        }
    }

    @Override
    public ITooltip getToolTip() {
        return this.tooltip;
    }
}
