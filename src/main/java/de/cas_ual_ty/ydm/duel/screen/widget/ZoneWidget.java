package de.cas_ual_ty.ydm.duel.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import de.cas_ual_ty.ydm.clientutil.CardRenderUtil;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.widget.ITooltip;
import de.cas_ual_ty.ydm.duel.DuelManager;
import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import de.cas_ual_ty.ydm.duel.playfield.ZoneInteraction;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import de.cas_ual_ty.ydm.duel.screen.DuelScreenDueling;
import de.cas_ual_ty.ydm.duel.screen.IDuelScreenContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jline.reader.Widget;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class ZoneWidget extends Button implements IWidgetToolTip
{
    public final Zone zone;
    public final IDuelScreenContext context;
    public boolean isFlipped;
    public DuelCard hoverCard;
    protected final ITooltip tooltip;

    public ZoneWidget(Zone zone, IDuelScreenContext ctx,
                      Consumer<ZoneWidget> onPress,
                      ITooltip onTooltip) {
        super(Button.builder(zone.getType().getLocal(), btn -> onPress.accept((ZoneWidget)btn))
                .pos(zone.x, zone.y)
                .size(zone.width, zone.height));
        this.tooltip = onTooltip;
        this.zone = zone;
        this.context = ctx;
        shift();
    }

    protected void shift()
    {
//        x -= width / 2;
//        y -= height / 2;
        this.setX(this.getX() - (this.getWidth() / 2));
        this.setY(this.getY() - (this.getHeight() / 2));
    }
    
    protected void unshift()
    {
//        x += width / 2;
//        y += height / 2;
        this.setX(this.getX() + (this.getWidth() / 2));
        this.setY(this.getY() + (this.getHeight() / 2));
    }
    
//    public ZoneWidget flip(int guiWidth, int guiHeight)
//    {
//        guiWidth /= 2;
//        guiHeight /= 2;
//
//        unshift();
//
//        x -= guiWidth;
//        y -= guiHeight;
//
//        x = -x;
//        y = -y;
//
//        x += guiWidth;
//        y += guiHeight;
//
//        shift();
//
//        isFlipped = !isFlipped;
//
//        return this;
//    }
    
    public ZoneWidget setPositionRelative(int x, int y, int guiWidth, int guiHeight)
    {
        this.setX(x + guiWidth / 2);
        this.setY(y + guiHeight / 2);
        
        shift();
        
        isFlipped = false;
        
        return this;
    }
    
    public ZoneWidget setPositionRelativeFlipped(int x, int y, int guiWidth, int guiHeight)
    {
        this.setX(guiWidth / 2 - x);
        this.setY(guiHeight / 2 - y);
        
        shift();
        
        isFlipped = true;
        
        return this;
    }



    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Font Font = minecraft.font;
        int x = this.getX();
        int y = this.getY();

//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.enableDepthTest();
//        RenderSystem.setShaderColor(1F, 1F, 1F, alpha);

        renderZoneSelectRect(gui, zone, x, y, width, height);

        hoverCard = renderCards(gui, mouseX, mouseY);

//        RenderSystem.setShaderColor(1F, 1F, 1F, alpha);

        if(zone.type.getCanHaveCounters() && zone.getCounters() > 0)
        {
            // see font renderer, top static Vector3f
            // white is translated in front by that
            gui.pose().pushPose();
            gui.pose().translate(0, 0, 0.03F);
            gui.drawCenteredString(Font, Component.literal("(" + zone.getCounters() + ")"),
                    x + width / 2, y + height / 2 - Font.lineHeight / 2,
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
    
    public void renderZoneSelectRect(GuiGraphics gui, Zone zone, float x, float y, float width, float height)
    {
        if(context.getClickedZone() == zone && context.getClickedCard() == null)
        {
            if(context.getOpponentClickedZone() == zone && context.getOpponentClickedCard() == null)
            {
                DuelScreenDueling.renderBothSelectedRect(gui, x, y, width, height);
            }
            else
            {
                DuelScreenDueling.renderSelectedRect(gui, x, y, width, height);
            }
        }
        else
        {
            if(context.getOpponentClickedZone() == zone && context.getOpponentClickedCard() == null)
            {
                DuelScreenDueling.renderEnemySelectedRect(gui, x, y, width, height);
            }
            else
            {
                //
            }
        }
    }
    
    public void renderCardSelectRect(GuiGraphics gui, DuelCard card, float x, float y, float width, float height)
    {
        if(context.getClickedCard() == card)
        {
            if(context.getOpponentClickedCard() == card)
            {
                DuelScreenDueling.renderBothSelectedRect(gui, x, y, width, height);
            }
            else
            {
                DuelScreenDueling.renderSelectedRect(gui, x, y, width, height);
            }
        }
        else
        {
            if(context.getOpponentClickedCard() == card)
            {
                DuelScreenDueling.renderEnemySelectedRect(gui, x, y, width, height);
            }
            else
            {
                //
            }
        }
    }
    
    @Nullable
    public DuelCard renderCards(GuiGraphics gui, int mouseX, int mouseY)
    {
        if(zone.getCardsAmount() <= 0)
        {
            return null;
        }
        int x = this.getX();
        int y = this.getY();
        
        boolean isOwner = zone.getOwner() == context.getZoneOwner();
        DuelCard c = zone.getTopCard();
        
        if(c != null)
        {
            if(drawCard(gui, c, x, y, width, height, mouseX, mouseY, x, y, width, height))
            {
                if(c.getCardPosition().isFaceUp || (isOwner && !zone.getType().getIsSecret())) {
                    context.renderCardInfo(gui, c);
                }
                
                if(active) {
                    ScreenUtil.renderHoverRect(gui, x, y, width, height);
                    return c;
                }
            }
        }
        
        if(context.getClickedZone() == zone) {
            DuelScreenDueling.renderSelectedRect(gui, x, y, width, height);
        }
        
        return null;
    }
    
    protected boolean drawCard(GuiGraphics gui, DuelCard duelCard, int renderX, int renderY, int renderWidth, int renderHeight, int mouseX, int mouseY, int cardsWidth, int cardsHeight) {
        int offset = cardsHeight - cardsWidth;
        
        int hoverX = renderX;
        int hoverY = renderY;
        int hoverWidth;
        int hoverHeight;
        
        if(duelCard.getCardPosition().isStraight)
        {
            hoverX += offset;
            hoverWidth = cardsWidth;
            hoverHeight = cardsHeight;
        }
        else
        {
            hoverY += offset;
            hoverWidth = cardsHeight;
            hoverHeight = cardsWidth;
        }
        
        return drawCard(gui, duelCard, renderX, renderY, renderWidth, renderHeight, mouseX, mouseY, hoverX, hoverY, hoverWidth, hoverHeight);
    }
    
    protected boolean drawCard(GuiGraphics gui, DuelCard duelCard, int renderX, int renderY, int renderWidth, int renderHeight, int mouseX, int mouseY, float hoverX, float hoverY, float hoverWidth, float hoverHeight)
    {
        boolean isOwner = zone.getOwner() == context.getZoneOwner();
        boolean faceUp = zone.getType().getShowFaceDownCardsToOwner() && isOwner;
        boolean isOpponentView = zone.getOwner() != context.getView();
        
        renderCardSelectRect(gui, duelCard, hoverX, hoverY, hoverWidth, hoverHeight);
        
        if(!isOpponentView) {
            CardRenderUtil.renderDuelCardCentered(gui, zone.getSleeves(), mouseX, mouseY, renderX, renderY, renderWidth, renderHeight, duelCard, faceUp);
        }
        else {
            CardRenderUtil.renderDuelCardReversedCentered(gui, zone.getSleeves(), mouseX, mouseY, renderX, renderY, renderWidth, renderHeight, duelCard, faceUp);
        }

        return isHoveredOrFocused() && mouseX >= hoverX && mouseX < hoverX + hoverWidth && mouseY >= hoverY && mouseY < hoverY + hoverHeight;
    }
    
    public void addInteractionWidgets(ZoneOwner player, Zone interactor, DuelCard interactorCard, DuelManager m, List<InteractionWidget> list, Consumer<InteractionWidget> onPress, ITooltip onTooltip, boolean isAdvanced)
    {
        List<ZoneInteraction> interactions;
        int x = this.getX();
        int y = this.getY();
        
        if(!isAdvanced)
        {
            interactions = m.getActionsFor(player, interactor, interactorCard, zone);
        }
        else
        {
            interactions = m.getAdvancedActionsFor(player, interactor, interactorCard, zone);
        }
        
        if(interactions.isEmpty()) return;

        
        if(interactions.size() == 1)
        {
            list.add(new InteractionWidget(interactions.get(0), context, x, y, width, height, onPress, onTooltip));
        }
        else if(interactions.size() == 2)
        {
            if(width <= height)
            {
                // Split them horizontally (1 action on top, 1 on bottom)
                list.add(new InteractionWidget(interactions.get(0), context, x, y, width, height / 2, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(1), context, x, y + height / 2, width, height / 2, onPress, onTooltip));
            }
            else
            {
                // Split them vertically (1 left, 1 right)
                list.add(new InteractionWidget(interactions.get(0), context, x, y, width / 2, height, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(1), context, x + width / 2, y, width / 2, height, onPress, onTooltip));
            }
        }
        else if(interactions.size() == 3)
        {
            if(width == height)
            {
                // 1 on top half, 1 bottom left, 1 bottom right
                list.add(new InteractionWidget(interactions.get(0), context, x, y, width, height / 2, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(1), context, x, y + height / 2, width / 2, height / 2, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(2), context, x + width / 2, y + height / 2, width / 2, height / 2, onPress, onTooltip));
            }
            else if(width < height)
            {
                // Horizontally split
                list.add(new InteractionWidget(interactions.get(0), context, x, y, width, height / 3, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(1), context, x, y + height / 3, width, height / 3, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(2), context, x, y + height * 2 / 3, width, height / 3, onPress, onTooltip));
            }
            else //if(this.width > this.height)
            {
                // Vertically split
                list.add(new InteractionWidget(interactions.get(0), context, x, y, width / 3, height, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(1), context, x + width / 3, y, width / 3, height, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(2), context, x + width * 2 / 3, y, width / 3, height, onPress, onTooltip));
            }
        }
        else if(interactions.size() == 4 && width == height)
        {
            // 1 on top left, 1 top right, 1 bottom left, 1 bottom right
            list.add(new InteractionWidget(interactions.get(0), context, x, y, width / 2, height / 2, onPress, onTooltip));
            list.add(new InteractionWidget(interactions.get(1), context, x + width / 2, y, width / 2, height / 2, onPress, onTooltip));
            list.add(new InteractionWidget(interactions.get(2), context, x, y + height / 2, width / 2, height / 2, onPress, onTooltip));
            list.add(new InteractionWidget(interactions.get(3), context, x + width / 2, y + height / 2, width / 2, height / 2, onPress, onTooltip));
        }
        else
        {
            if(width < height)
            {
                // Horizontally split
                for(int i = 0; i < interactions.size(); ++i)
                {
                    list.add(new InteractionWidget(interactions.get(i), context, x, y + height * i / interactions.size(), width, height / interactions.size(), onPress, onTooltip));
                }
            }
            else //if(this.width > this.height)
            {
                // Vertically split
                for(int i = 0; i < interactions.size(); ++i)
                {
                    list.add(new InteractionWidget(interactions.get(i), context, x + width * i / interactions.size(), y, width / interactions.size(), height, onPress, onTooltip));
                }
            }
        }
    }
    
    public int getAnimationSourceX()
    {
        return this.getX() + width / 2;
    }
    
    public int getAnimationSourceY()
    {
        return this.getY() + height / 2;
    }
    
    public int getAnimationDestX()
    {
        return this.getX() + width / 2;
    }
    
    public int getAnimationDestY()
    {
        return this.getY() + height / 2;
    }
    
    public Component getTranslation()
    {
        return Component.translatable(zone.getType().getRegistryName().getNamespace() + ".zone." + zone.getType().getRegistryName().getPath());
    }
    
    public boolean openAdvancedZoneView()
    {
        return false;
    }

    @Override
    public ITooltip getToolTip() {
        return this.tooltip;
    }
}