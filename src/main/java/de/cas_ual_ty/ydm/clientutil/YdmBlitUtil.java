package de.cas_ual_ty.ydm.clientutil;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class YdmBlitUtil {
    private YdmBlitUtil() {} // 工具类不允许实例化

    /**
     * 拉伸绘制整张贴图到指定区域。
     */
    public static void fullBlit(GuiGraphics gui,
                                ResourceLocation texture,
                                int x, int y,
                                int width, int height) {
        gui.blit(texture,
                x, y,
                0, 0,
                width, height,
                width, height);
    }

    /**
     * 绘制贴图的指定子区域（u,v 起点 + 子区域尺寸），并拉伸到屏幕区域。
     */
    public static void blit(GuiGraphics gui,
                            ResourceLocation texture,
                            int x, int y,
                            int blitWidth, int blitHeight,
                            int u, int v,
                            int srcWidth, int srcHeight,
                            int texWidth, int texHeight) {
        gui.blit(texture,
                x, y,
                blitWidth, blitHeight,
                u, v,
                srcWidth, srcHeight,
                texWidth, texHeight

        );

//        YdmBlitUtil.blit(
//                gui,
//                icon.sourceFile,
//                // 在屏幕上渲染的坐标
//                (int) (this.getX() + (this.width - iconW) / 2f),
//                (int) (this.getY() + (this.height - iconH) / 2f),
//
//                // 在屏幕上渲染的大小
//                iconW, iconH,
//
//                // 图片uv坐标
//                icon.iconX, icon.iconY,
//                icon.iconWidth, icon.iconHeight,
//
//                // 图片大小
//                icon.fileSize, icon.fileSize
//        );
    }

    /**
     * 顺时针旋转 90° 绘制。
     */
    public static void blit90(GuiGraphics gui,
                              ResourceLocation texture,
                              float x, float y,
                              float blitW, float blitH,
                              int u, int v,
                              int texW, int texH) {
        // 计算 UV 四角，顺时针 90°
        // blit 参数：目标 x,y,w,h；源 U,V；贴图尺寸
        gui.pose().pushPose();
        gui.pose().translate(x, y + blitH, 0);
        gui.pose().mulPoseMatrix( /* 旋转矩阵，绕 Z 轴逆时针 90° */
                new org.joml.Matrix4f().rotateZ((float)-Math.PI / 2)
        );
        gui.blit(texture,
                0, 0,
                (float) u, (float) v,
                (int) blitH, (int) blitW,
                texW, texH);
        gui.pose().popPose();
    }

    /**
     * 绘制 180° 旋转。
     */
    public static void blit180(GuiGraphics gui,
                               ResourceLocation texture,
                               float x, float y,
                               float blitW, float blitH,
                               int u, int v,
                               int texW, int texH) {
        gui.pose().pushPose();
        gui.pose().translate(x + blitW, y + blitH, 0);
        gui.pose().mulPoseMatrix(new org.joml.Matrix4f().rotateZ((float)Math.PI));
        gui.blit(texture,
                0, 0,
                (float) u, (float) v,
                (int) blitW, (int) blitH,
                texW, texH);
        gui.pose().popPose();
    }

    /**
     * 绘制 270° 顺时针（或 90° 逆时针）。
     */
    public static void blit270(GuiGraphics gui,
                               ResourceLocation texture,
                               float x, float y,
                               float blitW, float blitH,
                               int u, int v,
                               int texW, int texH) {
        gui.pose().pushPose();
        gui.pose().translate(x + blitW, y, 0);
        gui.pose().mulPoseMatrix(new org.joml.Matrix4f().rotateZ((float)Math.PI / 2));
        gui.blit(texture,
                0, 0,
                (float) u, (float) v,
                (int) blitH, (int) blitW,
                texW, texH);
        gui.pose().popPose();
    }

    /**
     * 高级遮罩绘制：先绘制蒙版（maskDrawer），再根据 inverted 选择混合模式绘制实际内容（textureDrawer）。
     */
    public static void advancedMaskedBlit(GuiGraphics gui,
                                          Runnable maskDrawer,
                                          Runnable textureDrawer,
                                          boolean inverted) {
        // 白色填充，准备写入 alpha 到目标
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(SourceFactor.ZERO, DestFactor.ONE,
                SourceFactor.SRC_ALPHA, DestFactor.ZERO);
        maskDrawer.run();
        if(!inverted) {
            RenderSystem.blendFuncSeparate(
                    SourceFactor.ONE_MINUS_DST_ALPHA, DestFactor.DST_COLOR,
                    SourceFactor.DST_ALPHA,           DestFactor.ONE_MINUS_DST_ALPHA
            );
        } else {
            RenderSystem.blendFuncSeparate(
                    SourceFactor.DST_ALPHA,         DestFactor.DST_COLOR,
                    SourceFactor.ONE_MINUS_DST_ALPHA, DestFactor.DST_ALPHA
            );
        }
        textureDrawer.run();
        RenderSystem.disableBlend();
    }

    public interface FullBlitMethod {
        void fullBlit(GuiGraphics gui, ResourceLocation resourceLocation, int x, int y, int width, int height);
    }
}
