package de.cas_ual_ty.ydm.duel.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.widget.ITooltip;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import de.cas_ual_ty.ydm.duel.screen.IDuelScreenContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

public class StackZoneWidget extends ZoneWidget
{
    // this does not render counters
    
    public StackZoneWidget(Zone zone, IDuelScreenContext context, Consumer<ZoneWidget> onPress, ITooltip tooltip)
    {
        super(zone, context, onPress, tooltip);
    }
    
    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontrenderer = minecraft.font;
        int x = this.getX();
        int y = this.getY();
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1F, 1F, 1F, alpha);
        
        renderZoneSelectRect(gui, zone, x, y, width, height);
        
        hoverCard = renderCards(gui, mouseX, mouseY);
        
        RenderSystem.setShaderColor(1F, 1F, 1F, alpha);
        
        if(zone.getCardsAmount() > 0)
        {
            // see font renderer, top static Vector3f
            // white is translated in front by that
            gui.pose().pushPose();
            gui.pose().translate(0, 0, 0.03F);
            gui.drawCenteredString(fontrenderer, Component.literal(String.valueOf(zone.getCardsAmount())),
                    x + width / 2, y + height / 2 - fontrenderer.lineHeight / 2,
                    16777215 | Mth.ceil(alpha * 255.0F) << 24);
            gui.pose().popPose();
        }
        
        if(active)
        {
            if(isHoveredOrFocused())
            {
                if(zone.getCardsAmount() == 0)
                {
                    ScreenUtil.renderHoverRect(gui, x, y, width, height);
                }

                if(this.isMouseOver(mouseX, mouseY)){
                    this.renderToolTip(gui, mouseX, mouseY);
                }
            }
        }
        else
        {
            ScreenUtil.renderDisabledRect(gui, x, y, width, height);
        }
    }
    
    @Override
    public boolean openAdvancedZoneView()
    {
        return !zone.getType().getIsSecret() && zone.getCardsAmount() > 0;
    }
}
