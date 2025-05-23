package de.cas_ual_ty.ydm.clientutil.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import de.cas_ual_ty.ydm.duel.screen.widget.IWidgetToolTip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SmallTextWidget extends AbstractWidget implements IWidgetToolTip {
    private final Supplier<Component> msgGetter;
    private final ITooltip tooltip;

    public SmallTextWidget(int x, int y, int width, int height,
                           Supplier<Component> msgGetter,
                           ITooltip tooltip) {
        super(x, y, width, height, Component.empty());
        this.msgGetter = msgGetter;
        this.tooltip = tooltip;
        this.active = false;
    }

    public SmallTextWidget(int x, int y, int width, int height,
                           Supplier<Component> msgGetter) {
        this(x, y, width, height, msgGetter, null);
    }

    @Override
    protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontrenderer = minecraft.font;
//        RenderSystem.setShaderTexture(0, AbstractWidget.WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        int i = getYImage(this, isHoveredOrFocused());
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.enableDepthTest();

        int x = this.getX();
        int y = this.getY();

        gui.blit(AbstractWidget.WIDGETS_LOCATION, x, y, 0, 46 + i * 20, width / 2, height / 2);
        gui.blit(AbstractWidget.WIDGETS_LOCATION, x + width / 2, y, 200 - width / 2, 46 + i * 20, width / 2, height / 2);
        gui.blit(AbstractWidget.WIDGETS_LOCATION, x, y + height / 2, 0, 46 + (i + 1) * 20 - height / 2, width / 2, height / 2);
        gui.blit(AbstractWidget.WIDGETS_LOCATION, x + width / 2, y + height / 2, 200 - width / 2, 46 + (i + 1) * 20 - height / 2, width / 2, height / 2);

        x = this.getX() + width / 2;
        y = this.getY() + height / 2;

        // Render half-size text
        gui.pose().pushPose();
        gui.pose().translate(0, 0, 0);
        gui.pose().scale(0.5F, 0.5F, 1F);

        int color = this.getFGColor() | (int)(this.alpha * 255f) << 24;
        gui.drawCenteredString(
                fontrenderer,
                this.msgGetter.get(),
                x * 2,
                y * 2 - fontrenderer.lineHeight / 2,
                color);

        gui.pose().popPose();

        // Tooltip on hover
        if (this.isHoveredOrFocused() && this.tooltip != null) {

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
    protected void updateWidgetNarration(@NotNull NarrationElementOutput builder) {
        // no extra narration
    }

    @Override
    public ITooltip getToolTip() {
        return this.tooltip;
    }
}
