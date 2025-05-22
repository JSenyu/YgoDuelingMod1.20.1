package de.cas_ual_ty.ydm.clientutil.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import de.cas_ual_ty.ydm.duel.screen.widget.IWidgetToolTip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.function.Supplier;

public class TextWidget extends AbstractWidget implements IWidgetToolTip {
    private final Supplier<Component> msgGetter;
    private final ITooltip tooltip;

    public TextWidget(int x, int y, int width, int height,
                      Supplier<Component> msgGetter, ITooltip tooltip) {
        super(x, y, width, height, Component.empty());
        this.msgGetter = msgGetter;
        this.tooltip = tooltip;
        this.active = false;
    }

    public TextWidget(int x, int y, int width, int height,
                      Supplier<Component> msgGetter) {
        this(x, y, width, height, msgGetter, null);
    }

    @Override
    protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontrenderer = minecraft.font;
        int x = this.getX();
        int y = this.getY();
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        int i = getYImage(this, isHoveredOrFocused());
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.enableDepthTest();
        gui.blit(WIDGETS_LOCATION, x, y, 0, 46 + i * 20, width / 2, height / 2);
        gui.blit(WIDGETS_LOCATION, x + width / 2, y, 200 - width / 2, 46 + i * 20, width / 2, height / 2);
        gui.blit(WIDGETS_LOCATION, x, y + height / 2, 0, 46 + (i + 1) * 20 - height / 2, width / 2, height / 2);
        gui.blit(WIDGETS_LOCATION, x + width / 2, y + height / 2, 200 - width / 2, 46 + (i + 1) * 20 - height / 2, width / 2, height / 2);

        // 2) 绘制居中文本
        int color = getFGColor() | (Mth.ceil(this.alpha * 255f) << 24);
        gui.drawCenteredString(
                fontrenderer,
                msgGetter.get(),
                x + this.getWidth() / 2,
                y + (this.getHeight() - fontrenderer.lineHeight) / 2,
                color
        );

        // 3) 悬停时显示 tooltip
        if (this.isHoveredOrFocused() && tooltip != null) {

            if(this.isMouseOver(mouseX, mouseY)){
                this.renderToolTip(gui, mouseX, mouseY);
            }
        }
    }


    @Override
    public int getFGColor() {
        return 0xFFFFFF;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
        // 无需额外叙述
    }

    @Override
    public ITooltip getToolTip() {
        return this.tooltip;
    }
}
