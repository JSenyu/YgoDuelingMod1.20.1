package de.cas_ual_ty.ydm.clientutil.widget;

import de.cas_ual_ty.ydm.duel.screen.widget.IWidgetToolTip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.function.Supplier;

public class ColoredTextWidget extends AbstractWidget implements IWidgetToolTip {
    public static final ResourceLocation RESOURCE =
            new ResourceLocation("ydm", "textures/gui/colored_button.png");

    private final Supplier<Component> msgGetter;
    private final ITooltip tooltip;       // 使用新版 Tooltip
    private int offset;

    // 构造器：带提示
    public ColoredTextWidget(int x, int y, int width, int height,
                             Supplier<Component> msgGetter,
                             ITooltip tooltip) {
        super(x, y, width, height, Component.empty());
        this.msgGetter  = msgGetter;
        this.tooltip    = tooltip;
        this.offset     = 0;
        this.active     = false;
    }

    // 构造器：无提示
    public ColoredTextWidget(int x, int y, int width, int height,
                             Supplier<Component> msgGetter) {
        this(x, y, width, height, msgGetter, null);
    }

    @Override
    protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontrenderer = minecraft.font;
        int x = this.getX();
        int y = this.getY();
//        RenderSystem.setShaderTexture(0, ColoredTextWidget.RESOURCE);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        int i = getYImage(this, isHoveredOrFocused());
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.enableDepthTest();
        gui.blit(RESOURCE, x, y, 0, offset + i * 20, width / 2, height / 2);
        gui.blit(RESOURCE, x + width / 2, y, 200 - width / 2, offset + i * 20, width / 2, height / 2);
        gui.blit(RESOURCE, x, y + height / 2, 0, offset + (i + 1) * 20 - height / 2, width / 2, height / 2);
        gui.blit(RESOURCE, x + width / 2, y + height / 2, 200 - width / 2, offset + (i + 1) * 20 - height / 2, width / 2, height / 2);
//        renderBg(ms, minecraft, mouseX, mouseY);
//        super.renderWidget(gui, mouseX, mouseY, partialTicks);

        int j = getFGColor();
        gui.drawCenteredString(fontrenderer, getMessage(), x + width / 2, y + (height - 8) / 2, j | Mth.ceil(alpha * 255.0F) << 24);

        // 如果 hover 且有提示，就渲染提示
        if (isHoveredOrFocused() && tooltip != null) {

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
    public Component getMessage() {
        return msgGetter.get();
    }

    /** 切换到蓝色版 */
    public ColoredTextWidget setBlue() {
        this.offset = 0;
        return this;
    }

    /** 切换到红色版 */
    public ColoredTextWidget setRed() {
        this.offset = 60;
        return this;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
        // 无额外叙述
    }

    @Override
    public ITooltip getToolTip() {
        return this.tooltip;
    }
}
