package de.cas_ual_ty.ydm.duel.screen.widget;

import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.widget.ITooltip;
import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import de.cas_ual_ty.ydm.duel.screen.DuelScreenDueling;
import de.cas_ual_ty.ydm.duel.screen.IDuelScreenContext;
import net.minecraft.client.gui.GuiGraphics;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class HandZoneWidget extends ZoneWidget
{
    public HandZoneWidget(Zone zone, IDuelScreenContext context, Consumer<ZoneWidget> onPress, ITooltip onTooltip)
    {
        super(zone, context,  onPress, onTooltip);
    }
    
    @Override
    @Nullable
    public DuelCard renderCards(GuiGraphics gui, int mouseX, int mouseY) {

        if(zone.getCardsAmount() <= 0)
        {
            return super.renderCards(gui, mouseX, mouseY);
        }
        int x = this.getX();
        int y = this.getY();
        
        final int cardsWidth = DuelScreenDueling.CARDS_WIDTH * height / DuelScreenDueling.CARDS_HEIGHT;
        final int cardsHeight = height;
        final int offset = (cardsHeight - cardsWidth);
        final int cardsTextureSize = cardsHeight;
        
        DuelCard hoveredCard = null;
        float hoverX = x;
        float hoverY = y;
        float hoverWidth = cardsWidth;
        float hoverHeight = cardsHeight;
        
        boolean isOwner = zone.getOwner() == context.getZoneOwner();
        boolean isOpponentView = zone.getOwner() != context.getView();
        
        final int renderX = x;
        final int renderY = y;
        final int renderWidth = cardsTextureSize;
        final int renderHeight = cardsTextureSize;
        
        DuelCard c = null;
        hoverWidth = cardsWidth;
        
        int totalW = zone.getCardsAmount() * cardsWidth;
        
        if(totalW <= width || zone.getCardsAmount() == 1)
        {
            int newHoverX = renderX + (width - totalW) / 2;
            int newRenderX = newHoverX - (cardsTextureSize - cardsWidth) / 2; // Cards are 24x32, but the textures are still 32x32, so we must account for that
            
            for(int i = 0; i < zone.getCardsAmount(); ++i)
            {
                if(!isOpponentView)
                {
                    c = zone.getCardUnsafe(i);
                }
                else
                {
                    c = zone.getCardUnsafe(zone.getCardsAmount() - i - 1);
                }
                
                if(drawCard(gui, c, newRenderX, renderY, renderWidth, renderHeight, mouseX, mouseY, newHoverX, hoverY, hoverWidth, hoverHeight))
                {
                    hoveredCard = c;
                    hoverX = newHoverX;
                }
                
                newRenderX += cardsWidth;
                newHoverX += cardsWidth;
            }
        }
        else
        {
            float hoverXBase = x;
            
            float newRenderX;
            float newHoverX;
            
            float margin = cardsWidth - (zone.getCardsAmount() * cardsWidth - width) / (float) (zone.getCardsAmount() - 1);
            
            boolean renderLeftToRight = false;
            boolean renderFrontToBack = isOpponentView;
            
            if(!renderLeftToRight)
            {
                margin *= -1F;
                hoverXBase += width - cardsWidth;
            }
            
            for(int i = 0; i < zone.getCardsAmount(); ++i)
            {
                if(renderFrontToBack)
                {
                    c = zone.getCardUnsafe(i);
                }
                else
                {
                    c = zone.getCardUnsafe(zone.getCardsAmount() - i - 1);
                }
                
                newHoverX = hoverXBase + i * margin;
                newRenderX = newHoverX - (float) offset / 2;
                
                if(drawCard(gui, c, (int) newRenderX, renderY, renderWidth, renderHeight, mouseX, mouseY, newHoverX, hoverY, hoverWidth, hoverHeight))
                {
                    hoveredCard = c;
                    hoverX = newHoverX;
                }
            }
        }
        
        if(hoveredCard != null)
        {
            if(hoveredCard.getCardPosition().isFaceUp || (isOwner && !zone.getType().getIsSecret()))
            {
                context.renderCardInfo(gui, hoveredCard);
            }
            
            if(active)
            {
                ScreenUtil.renderHoverRect(gui, hoverX, hoverY, hoverWidth, hoverHeight);
            }
        }
        
        if(!active)
        {
            return null;
        }
        else
        {
            return hoveredCard;
        }
    }
    
    @Override
    public boolean openAdvancedZoneView()
    {
        return !zone.getType().getIsSecret() && zone.getCardsAmount() > 12;
    }
}
