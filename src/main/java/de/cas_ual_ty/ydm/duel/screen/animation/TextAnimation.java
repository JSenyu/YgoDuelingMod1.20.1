package de.cas_ual_ty.ydm.duel.screen.animation;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;


public class TextAnimation extends Animation
{
    public Component message;
    public float centerPosX;
    public float centerPosY;
    
    public TextAnimation(Component message, float centerPosX, float centerPosY)
    {
        super(ClientProxy.announcementAnimationLength);
        
        this.message = message;
        this.centerPosX = centerPosX;
        this.centerPosY = centerPosY;
    }
    
    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks)
    {
        Font f = ClientProxy.getMinecraft().font;
        
        double relativeTickTime = (tickTime + partialTicks) / maxTickTime;
        
        // [0, 1/2pi]
        double cosTime1 = 0.5D * Math.PI * relativeTickTime;
        // [0, 1]
        float alpha = (float) (Math.cos(cosTime1));

        gui.pose().pushPose();

        gui.pose().translate(centerPosX, centerPosY - (float) f.lineHeight / 2, 0);
        
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1F, 1F, 1F, alpha);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        
        int color = 16777215 | Mth.ceil(alpha * 255.0F) << 24; //See TextWidget
        gui.drawCenteredString(f, message, 0, 0, color);
//        gui.drawCenteredString(gui, f, message, 0, 0, j | Mth.ceil(alpha * 255.0F) << 24);
        RenderSystem.disableBlend();
        ScreenUtil.white();

        gui.pose().popPose();
    }
}
