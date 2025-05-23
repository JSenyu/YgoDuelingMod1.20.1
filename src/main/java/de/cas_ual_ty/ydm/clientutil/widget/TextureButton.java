package de.cas_ual_ty.ydm.clientutil.widget;

import de.cas_ual_ty.ydm.duel.screen.widget.IWidgetToolTip;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TextureButton extends Button implements IWidgetToolTip {
    private ResourceLocation textureLocation;
    private int texX, texY, texW, texH;
    protected ITooltip tooltip;

    public TextureButton(int x, int y, int width, int height,
                         Component title,
                         OnPress onPress,
                         ITooltip onTooltip) {
        super(Button.builder(title, onPress)
                .pos(x, y)
                .size(width, height));
        this.tooltip = onTooltip;
    }

    public TextureButton(int x, int y, int width, int height,
                         Component title,
                         OnPress onPress) {
        super(Button.builder(title, onPress)
                .pos(x, y)
                .size(width, height));
    }

    public TextureButton setTexture(ResourceLocation textureLocation,
                                    int texX, int texY,
                                    int texW, int texH) {
        this.textureLocation = textureLocation;
        this.texX = texX;
        this.texY = texY;
        this.texW = texW;
        this.texH = texH;
        return this;
    }


    @Override
    protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {

//        Minecraft minecraft = Minecraft.getInstance();
//        Font fontrenderer = minecraft.font;
        int x = this.getX();
        int y = this.getY();
//        RenderSystem.setShaderTexture(0, AbstractWidget.WIDGETS_LOCATION);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        int i = getYImage(this, isHoveredOrFocused());
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.enableDepthTest();
        gui.blit(AbstractWidget.WIDGETS_LOCATION, x, y, 0, 46 + i * 20, width / 2, height / 2);
        gui.blit(AbstractWidget.WIDGETS_LOCATION, x + width / 2, y, 200 - width / 2, 46 + i * 20, width / 2, height / 2);
        gui.blit(AbstractWidget.WIDGETS_LOCATION, x, y + height / 2, 0, 46 + (i + 1) * 20 - height / 2, width / 2, height / 2);
        gui.blit(AbstractWidget.WIDGETS_LOCATION, x + width / 2, y + height / 2, 200 - width / 2, 46 + (i + 1) * 20 - height / 2, width / 2, height / 2);
//        renderBg(gui, minecraft, mouseX, mouseY);
//        super.renderWidget(gui, mouseX, mouseY, partialTicks);

        if(textureLocation != null) {
            gui.blit(this.textureLocation, x, y, width, height, texX, texY, texW, texH, 256, 256);
//            RenderSystem.setShaderTexture(0, textureLocation);
//            YdmBlitUtil.blit(gui, x, y, width, height, texX, texY, texW, texH, 256, 256);
        }

        if(this.isHoveredOrFocused()) {

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
