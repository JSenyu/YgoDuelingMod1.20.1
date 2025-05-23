package de.cas_ual_ty.ydm.clientutil;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class ScreenUtil
{
    public static void drawLineRect(GuiGraphics gui, float x, float y, float w, float h, float lineWidth, float r, float g, float b, float a)
    {
        ScreenUtil.drawRect(gui, x, y, w, lineWidth, r, g, b, a); //top
        ScreenUtil.drawRect(gui, x, y + h - lineWidth, w, lineWidth, r, g, b, a); //bot
        ScreenUtil.drawRect(gui, x, y, lineWidth, h, r, g, b, a); //left
        ScreenUtil.drawRect(gui, x + w - lineWidth, y, lineWidth, h, r, g, b, a); //right
    }

    /**
     * 在屏幕上绘制一个纯色矩形
     *
     * @param gui   当前 GuiGraphics
     * @param x     矩形左上角 X
     * @param y     矩形左上角 Y
     * @param w     矩形宽度
     * @param h     矩形高度
     * @param r     红色分量 [0,1]
     * @param g     绿色分量 [0,1]
     * @param b     蓝色分量 [0,1]
     * @param a     透明度分量 [0,1]
     */
    public static void drawRect(GuiGraphics gui, float x, float y, float w, float h,
                                float r, float g, float b, float a) {
        // 将浮点 RGBA 转换为 ARGB 整型
        int alpha = Math.round(a * 255) & 0xFF;
        int red   = Math.round(r * 255) & 0xFF;
        int green = Math.round(g * 255) & 0xFF;
        int blue  = Math.round(b * 255) & 0xFF;
        int color = (alpha << 24) | (red << 16) | (green << 8) | blue;
        // fill 方法的参数为 (x1, y1, x2, y2, ARGB)
        gui.fill((int)x, (int)y, (int)(x + w), (int)(y + h), color);
    }


    public static void drawSplitString(GuiGraphics gui, Font font, List<Component> list,
                                       float x, float y, int maxWidth, int color) {
        float yy = y;
        for (Component comp : list) {
            String text = comp.getString();
            if (text.isEmpty() && comp.getSiblings().isEmpty()) {
                yy += font.lineHeight;
            } else {
                for (FormattedCharSequence seq : font.split(comp, maxWidth)) {
                    // 用 GuiGraphics.drawString，并传入 true 开启阴影
                    gui.drawString(font, seq, x, yy, color, true);
                    yy += font.lineHeight;
                }
            }
        }
    }


    public static void renderHoverRect(GuiGraphics gui, float x, float y, float w, float h)
    {
        // from ContainerScreen#render
        
        RenderSystem.colorMask(true, true, true, false);
        ScreenUtil.drawRect(gui, x, y, w, h, 1F, 1F, 1F, 0.5F);
        RenderSystem.colorMask(true, true, true, true);
    }
    
    public static void renderDisabledRect(GuiGraphics gui, float x, float y, float w, float h)
    {
        RenderSystem.colorMask(true, true, true, false);
        ScreenUtil.drawRect(gui, x, y, w, h, 0F, 0F, 0F, 0.5F);
        RenderSystem.colorMask(true, true, true, true);
    }
    
    public static void white()
    {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
