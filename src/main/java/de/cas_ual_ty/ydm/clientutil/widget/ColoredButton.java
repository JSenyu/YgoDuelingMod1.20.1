package de.cas_ual_ty.ydm.clientutil.widget;

import de.cas_ual_ty.ydm.duel.screen.widget.IWidgetToolTip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ColoredButton extends Button implements IWidgetToolTip {
    public static final ResourceLocation RESOURCE =
            new ResourceLocation("ydm", "textures/gui/colored_button.png");
    private int offset;
    protected final ITooltip tooltip;

    public ColoredButton(int x, int y, int width, int height, Component title, OnPress onPress, ITooltip tooltip) {
        super(Button.builder(title, onPress).pos(x, y).size(width, height));
        this.tooltip = tooltip;
        this.offset = 0;
    }

    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontrenderer = minecraft.font;
//        RenderSystem.setShaderTexture(0, ColoredButton.RESOURCE);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        int i = getYImage(this, isHoveredOrFocused());
        int x = this.getX();
        int y = this.getY();
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.enableDepthTest();
        gui.blit(RESOURCE, x, y, 0, offset + i * 20, width / 2, height / 2);
        gui.blit(RESOURCE, x + width / 2, y, 200 - width / 2, offset + i * 20, width / 2, height / 2);
        gui.blit(RESOURCE, x, y + height / 2, 0, offset + (i + 1) * 20 - height / 2, width / 2, height / 2);
        gui.blit(RESOURCE, x + width / 2, y + height / 2, 200 - width / 2, offset + (i + 1) * 20 - height / 2, width / 2, height / 2);
//        renderBg(PoseStack, minecraft, mouseX, mouseY);

        int j = getFGColor();
        gui.drawCenteredString(fontrenderer, getMessage(), x + width / 2, y + (height - 8) / 2, j | Mth.ceil(alpha * 255.0F) << 24);

        // Render tooltip on hover
        if (this.isHoveredOrFocused()) {

            if(this.isMouseOver(mouseX, mouseY)){
                this.renderToolTip(gui, mouseX, mouseY);
            }
        }
    }

    /**
     * Set button color to blue variant.
     */
    public ColoredButton setBlue() {
        this.offset = 0;
        return this;
    }

    /**
     * Set button color to red variant.
     */
    public ColoredButton setRed() {
        this.offset = 60;
        return this;
    }

    @Override
    public ITooltip getToolTip() {
        return this.tooltip;
    }
}
