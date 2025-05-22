package de.cas_ual_ty.ydm.duel.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import de.cas_ual_ty.ydm.card.CardSleevesType;
import de.cas_ual_ty.ydm.clientutil.CardRenderUtil;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.widget.ITooltip;
import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.screen.DuelScreenDueling;
import de.cas_ual_ty.ydm.duel.screen.IDuelScreenContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class ViewCardStackWidget extends Button implements IWidgetToolTip{
    public final IDuelScreenContext context;
    public DuelCard hoverCard;
    protected int cardsTextureSize;
    protected int rows;
    protected int columns;
    protected int currentRow;
    protected List<DuelCard> cards;
    protected boolean forceFaceUp;
    protected final ITooltip tooltip;

    public ViewCardStackWidget(IDuelScreenContext context, int x, int y, int width, int height, Component title, Consumer<ViewCardStackWidget> onPress, ITooltip onTooltip) {
//        super(x, y, width, height, title, (button) -> onPress.accept((ViewCardStackWidget) button), onTooltip);
        super(Button.builder(title, (button) -> onPress.accept((ViewCardStackWidget) button)).pos(x, y).size(width, height));
        this.tooltip = onTooltip;
        this.context = context;
        hoverCard = null;
        rows = 0;
        columns = 0;
        currentRow = 0;
        deactivate();
    }

    public ViewCardStackWidget setRowsAndColumns(int cardsTextureSize, int rows, int columns) {
        this.cardsTextureSize = cardsTextureSize;
        this.rows = Math.max(1, rows);
        this.columns = Math.max(1, columns);
        return this;
    }

    public void activate(List<DuelCard> cards, boolean forceFaceUp) {
        active = true;
        visible = true;
        currentRow = 0;
        this.cards = cards;
        this.forceFaceUp = forceFaceUp;
    }

    public void forceFaceUp() {
        forceFaceUp = true;
    }

    public void deactivate() {
        cards = null;
        visible = false;
        active = false;
    }

    public int getCurrentRow() {
        return currentRow;
    }

    public int getMaxRows() {
        if(cards != null && columns > 0) {
            return Math.max(0, Mth.ceil(cards.size() / (float) columns) - rows);
        } else {
            return 0;
        }
    }

    public void decreaseCurrentRow() {
        currentRow = Math.max(0, currentRow - 1);
    }

    public void increaseCurrentRow() {
        currentRow = Math.min(getMaxRows(), currentRow + 1);
    }

    public boolean getForceFaceUp() {
        return forceFaceUp;
    }

    public List<DuelCard> getCards() {
        return cards;
    }


    @Override
    public void renderWidget(@NotNull GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontrenderer = minecraft.font;
        int x = this.getX();
        int y = this.getY();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1F, 1F, 1F, alpha);

        if(!cards.isEmpty()) {
            hoverCard = renderCards(gui, mouseX, mouseY);
        } else {
            hoverCard = null;
        }

        int color = getFGColor() | Mth.ceil(alpha * 255.0F) << 24;
        gui.drawCenteredString(fontrenderer, getMessage(), x, y, color);
    }

    @Nullable
    public DuelCard renderCards(GuiGraphics gui, int mouseX, int mouseY) {
        DuelCard hoveredCard = null;
        int hoverX = 0, hoverY = 0;

        int index = currentRow * columns;
        int x, y;
        DuelCard c;

        for(int i = 0; i < rows; ++i) {
            for(int j = 0; j < columns && index < cards.size(); ++j) {
                x = this.getX() + j * cardsTextureSize;
                y = this.getY() + i * cardsTextureSize;

                c = cards.get(index++);

                if(drawCard(gui, c, x, y, cardsTextureSize, cardsTextureSize, mouseX, mouseY)) {
                    hoverX = x;
                    hoverY = y;
                    hoveredCard = c;
                }
            }
        }

        if(hoveredCard != null) {
            if(hoveredCard.getCardPosition().isFaceUp || forceFaceUp || (context.getClickedZone() != null && context.getZoneOwner() == context.getClickedZone().getOwner() && !context.getClickedZone().getType().getIsSecret())) {
                context.renderCardInfo(gui, hoveredCard);
            }
            ScreenUtil.renderHoverRect(gui, hoverX, hoverY, cardsTextureSize, cardsTextureSize);
        }

        if(!active) return null;
        else return hoveredCard;

    }

    protected boolean drawCard(GuiGraphics gui, DuelCard duelCard, int renderX, int renderY, int renderWidth, int renderHeight, int mouseX, int mouseY) {
        if(context.getClickedCard() == duelCard) {
            if(context.getOpponentClickedCard() == duelCard) {
                DuelScreenDueling.renderBothSelectedRect(gui, renderX, renderY, renderWidth, renderHeight);
            } else {
                DuelScreenDueling.renderSelectedRect(gui, renderX, renderY, renderWidth, renderHeight);
            }
        } else {
            if(context.getOpponentClickedCard() == duelCard) {
                DuelScreenDueling.renderEnemySelectedRect(gui, renderX, renderY, renderWidth, renderHeight);
            }
        }

        CardRenderUtil.renderDuelCardCentered(gui, context.getClickedZone() != null ? context.getClickedZone().getSleeves() : CardSleevesType.CARD_BACK, mouseX, mouseY, renderX, renderY, renderWidth, renderHeight, duelCard, forceFaceUp);

        return isHoveredOrFocused() && mouseX >= renderX && mouseX < renderX + renderWidth && mouseY >= renderY && mouseY < renderY + renderHeight;
    }

    @Override
    public ITooltip getToolTip() {
        return this.tooltip;
    }
}
