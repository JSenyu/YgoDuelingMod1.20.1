package de.cas_ual_ty.ydm.clientutil.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class SmallTextButton extends Button {
    public SmallTextButton(int x, int y, int width, int height,
                           Component title, OnPress onPress, Tooltip onTooltip) {
        super(Button.builder(title, onPress)
                .pos(x, y)
                .size(width, height)
                .tooltip(onTooltip));
    }

    public SmallTextButton(int x, int y, int width, int height,
                           Component title, OnPress onPress) {
        super(Button.builder(title, onPress)
                .pos(x, y)
                .size(width, height));
    }

    @Override
    protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        // 绑定默认控件纹理并设置渲染状态
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1f, 1f, 1f, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        // 计算 hover/focused 状态：2 表示 hover，1 表示正常
        int state = this.isHoveredOrFocused() ? 2 : 1;

        int halfW = this.getWidth() / 2;
        int halfH = this.getHeight() / 2;
        int x = this.getX(), y = this.getY();

        // 绘制左半和右半纹理
        gui.blit(WIDGETS_LOCATION, x, y, 0, 46 + state * 20, halfW, halfH);
        gui.blit(WIDGETS_LOCATION, x + halfW, y, 200 - halfW, 46 + state * 20, halfW, halfH);
        gui.blit(WIDGETS_LOCATION, x, y + halfH, 0, 46 + (state + 1) * 20 - halfH, halfW, halfH);
        gui.blit(WIDGETS_LOCATION, x + halfW, y + halfH, 200 - halfW, 46 + (state + 1) * 20 - halfH, halfW, halfH);

        // 渲染半尺寸文字
        gui.pose().pushPose();
        gui.pose().translate(0, 0, 0);
        gui.pose().scale(0.5F, 0.5F, 1F);

        int color = this.getFGColor() | (Mth.ceil(this.alpha * 255.0F) << 24);
        // 文字绘制时 x,y 坐标需要 *2，因为画布坐标被缩放了一半
        gui.drawCenteredString(font,
                this.getMessage(),
                (x + this.getWidth() / 2) * 2,
                (y + (this.getHeight() - 8) / 2) * 2,
                color);

        gui.pose().popPose();

        // 鼠标悬停时，Screen 层负责调用 renderComponentTooltip
    }
}
