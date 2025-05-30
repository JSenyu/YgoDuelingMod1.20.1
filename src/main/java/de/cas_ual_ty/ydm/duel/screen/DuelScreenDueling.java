package de.cas_ual_ty.ydm.duel.screen;

import com.google.common.collect.ImmutableList;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.CardRenderUtil;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.widget.*;
import de.cas_ual_ty.ydm.duel.DuelContainer;
import de.cas_ual_ty.ydm.duel.DuelPhase;
import de.cas_ual_ty.ydm.duel.action.*;
import de.cas_ual_ty.ydm.duel.network.DuelMessages;
import de.cas_ual_ty.ydm.duel.playfield.*;
import de.cas_ual_ty.ydm.duel.screen.animation.*;
import de.cas_ual_ty.ydm.duel.screen.widget.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.*;

public class DuelScreenDueling<E extends DuelContainer> extends DuelContainerScreen<E> implements IDuelScreenContext{
    public static final int CARDS_WIDTH = 24;
    public static final int CARDS_HEIGHT = 32;

    protected TextWidget cardStackNameWidget;
    protected Component nameShown;
    protected ViewCardStackWidget viewCardStackWidget;
    protected Button scrollUpButton;
    protected Button scrollDownButton;

    protected ZoneWidget clickedZoneWidget;
    protected DuelCard clickedCard;

    protected List<ZoneWidget> zoneWidgets;
    protected List<InteractionWidget> interactionWidgets;
    protected boolean isAdvanced;

    protected Button coinFlipButton;
    protected Button diceRollButton;
    protected Button addCounterButton;
    protected Button removeCounterButton;
    protected Button advancedOptionsButton;

    protected Button reloadButton;
    protected Button flipViewButton;
    protected Button offerDrawButton;
    protected Button admitDefeatButton;
    protected LPTextFieldWidget lifePointsWidget;

    protected ColoredButton prevPhaseButton;
    protected ColoredButton nextPhaseButton;
    protected ColoredTextWidget phaseWidget;

    protected ZoneOwner view;
    protected AnimationsWidget animationsWidget;

    protected DuelCard cardInfo;

    // need to store these seperately
    // to make sure that we keep them
    // in case a player leaves
    protected MutableComponent player1Name;
    protected MutableComponent player2Name;

    public DuelScreenDueling(E screenContainer, Inventory inv, Component titleIn)
    {
        super(screenContainer, inv, titleIn);
        interactionWidgets = new ArrayList<>(); // Need to temporarily initialize with placeholder this to make sure no clear() call gets NPEd
        isAdvanced = false;

        viewCardStackWidget = null;
        nameShown = null;
        clickedZoneWidget = null;
        clickedCard = null;

        coinFlipButton = null;
        diceRollButton = null;
        addCounterButton = null;
        removeCounterButton = null;
        advancedOptionsButton = null;
        reloadButton = null;
        flipViewButton = null;
        offerDrawButton = null;
        admitDefeatButton = null;
        lifePointsWidget = null;
        prevPhaseButton = null;
        nextPhaseButton = null;
        phaseWidget = null;

        view = getZoneOwner();
        if(view == ZoneOwner.NONE)
        {
            view = ZoneOwner.PLAYER1;
        }
        animationsWidget = null;
        cardInfo = null;
        player1Name = null;
        player2Name = null;
    }

    @Override
    protected void init()
    {
        super.init();

        ViewCardStackWidget previousViewStack = viewCardStackWidget;

        if(animationsWidget != null)
        {
            animationsWidget.forceFinish();
        }

        initDefaultChat(width, height);

        int x, y;

        final int zoneSize = 32;
        final int halfSize = zoneSize / 2;
        final int quarterSize = zoneSize / 4;
        final int zonesMargin = 2;

        //middle
        x = (width - zoneSize) / 2;
        y = (height - zoneSize) / 2;

        addRenderableWidget(reloadButton = new TextureButton(x, y, quarterSize, quarterSize, Component.translatable("container." + YDM.MOD_ID + ".duel.reload"), this::middleButtonClicked, this::middleButtonHovered)
                .setTexture(new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png"), 64, 0, 16, 16));
        addRenderableWidget(flipViewButton = new TextureButton(x + quarterSize, y, quarterSize, quarterSize, Component.translatable("container." + YDM.MOD_ID + ".duel.flip_view"), this::middleButtonClicked, this::middleButtonHovered)
                .setTexture(new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png"), 80, 0, 16, 16));
        addRenderableWidget(offerDrawButton = new TextureButton(x + 2 * quarterSize, y, quarterSize, quarterSize, Component.translatable("container." + YDM.MOD_ID + ".duel.offer_draw"), this::middleButtonClicked, this::middleButtonHovered)
                .setTexture(new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png"), 96, 0, 16, 16));
        addRenderableWidget(admitDefeatButton = new TextureButton(x + 3 * quarterSize, y, quarterSize, quarterSize, Component.translatable("container." + YDM.MOD_ID + ".duel.admit_defeat"), this::middleButtonClicked, this::middleButtonHovered)
                .setTexture(new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png"), 112, 0, 16, 16));

        // lp text field for players, "Spectator" text for spectators
        if(getZoneOwner() != ZoneOwner.NONE)
        {
            addRenderableWidget(lifePointsWidget = new LPTextFieldWidget(font, x, y + 3 * quarterSize, zoneSize, quarterSize, this::lpTextFieldWidget));
        }
        else
        {
            addRenderableWidget(new SmallTextWidget(x, y + 3 * quarterSize, zoneSize, quarterSize, () -> Component.translatable("container." + YDM.MOD_ID + ".duel.spectating")));
        }

        if(getZoneOwner() == ZoneOwner.NONE)
        {
            admitDefeatButton.active = false;
            offerDrawButton.active = false;
        }

        addRenderableWidget(new LifePointsWidget(x, y + quarterSize, zoneSize, quarterSize,
                () -> getPlayField().getLifePoints(getView().opponent()), getPlayField().playFieldType.startingLifePoints, this::lpTooltipViewOpponent));
        addRenderableWidget(new LifePointsWidget(x, y + 2 * quarterSize, zoneSize, quarterSize,
                () -> getPlayField().getLifePoints(getView()), getPlayField().playFieldType.startingLifePoints, this::lpTooltipView));

        //left
        x = (width - zoneSize) / 2 - (zoneSize + zonesMargin) * 2;

        addRenderableWidget(coinFlipButton = new TextureButton(x, y, halfSize, halfSize, Component.translatable("container." + YDM.MOD_ID + ".duel.coin_flip"), this::leftButtonClicked, this::leftButtonHovered)
                .setTexture(new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png"), 32, 0, 16, 16));
        addRenderableWidget(diceRollButton = new TextureButton(x + halfSize, y, halfSize, halfSize, Component.translatable("container." + YDM.MOD_ID + ".duel.dice_roll"), this::leftButtonClicked, this::leftButtonHovered)
                .setTexture(new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png"), 48, 0, 16, 16));
        addRenderableWidget(addCounterButton = new TextureButton(x, y + halfSize, halfSize, quarterSize, Component.translatable("container." + YDM.MOD_ID + ".duel.add_counter"), this::leftButtonClicked, this::leftButtonHovered)
                .setTexture(new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png"), 128, 0, 16, 8));
        addRenderableWidget(removeCounterButton = new TextureButton(x, y + halfSize + quarterSize, halfSize, quarterSize, Component.translatable("container." + YDM.MOD_ID + ".duel.remove_counter"), this::leftButtonClicked, this::leftButtonHovered)
                .setTexture(new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png"), 128, 8, 16, 8));
        addRenderableWidget(advancedOptionsButton = new TextureButton(x + halfSize, y + halfSize, halfSize, halfSize, Component.translatable("container." + YDM.MOD_ID + ".duel.advanced_options"), this::leftButtonClicked, this::leftButtonHovered)
                .setTexture(new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png"), 144, 0, 16, 16));

        if(getZoneOwner() == ZoneOwner.NONE)
        {
            coinFlipButton.active = false;
            diceRollButton.active = false;
            addCounterButton.active = false;
            removeCounterButton.active = false;
            advancedOptionsButton.active = false;
        }

        // right
        x = (width - zoneSize) / 2 + (zoneSize + zonesMargin) * 2;

        addRenderableWidget(phaseWidget = new ColoredTextWidget(x, y, zoneSize, halfSize, this::getPhaseShort, this::phaseWidgetHovered));
        phaseWidget.active = false;
        addRenderableWidget(prevPhaseButton = new ColoredButton(x, y + halfSize, halfSize, halfSize, Component.translatable("container." + YDM.MOD_ID + ".duel.left_arrow"), this::rightButtonClicked, this::rightButtonHovered));
        addRenderableWidget(nextPhaseButton = new ColoredButton(x + halfSize, y + halfSize, halfSize, halfSize, Component.translatable("container." + YDM.MOD_ID + ".duel.right_arrow"), this::rightButtonClicked, this::rightButtonHovered));

        if(getZoneOwner() == ZoneOwner.NONE)
        {
            prevPhaseButton.active = false;
            nextPhaseButton.active = false;
        }

        zoneWidgets = new ArrayList<>(getDuelManager().getPlayField().getZones().size());
        interactionWidgets.clear();

        ZoneWidget widget;

        for(Zone zone : getDuelManager().getPlayField().getZones())
        {
            addRenderableWidget(widget = createZoneWidgetForZone(zone));

            if(getView() == ZoneOwner.PLAYER2)
            {
                widget.setPositionRelativeFlipped(zone.x, zone.y, width, height);
            }
            else
            {
                widget.setPositionRelative(zone.x, zone.y, width, height);
            }

            zoneWidgets.add(widget);
        }

        zoneWidgets.sort(Comparator.comparingInt(z -> z.zone.index));

        if(animationsWidget != null)
        {
            animationsWidget.onInit();
        }
        addRenderableWidget(animationsWidget = new AnimationsWidget(0, 0, 0, 0));

        // in case we init again, buttons is cleared, thus all interaction widgets are removed
        // just act like we click on the last widget again
        if(clickedZoneWidget != null)
        {
            for(ZoneWidget match : zoneWidgets)
            {
                if(match.zone == clickedZoneWidget.zone)
                {
                    setClickedZoneWidgetAndCard(match, clickedCard);
                    break;
                }
            }

            clickedZoneWidget.hoverCard = clickedCard;
            zoneClicked(clickedZoneWidget);
        }

        if(previousViewStack != null && previousViewStack.getCards() != null)
        {
            viewCards(previousViewStack.getCards(), nameShown, previousViewStack.getForceFaceUp());
        }

        update();
    }

    private void update() {
        updateScrollButtonStatus();
        updateLeftButtonStatus();
        updateRightButtonStatus();
    }

    @Override
    protected void initChat(int width, int height, int x, int y, int w, int h, int chatWidth, int chatHeight, int margin, int buttonHeight)
    {
        super.initChat(width, height, x, y, w, h, chatWidth, chatHeight, margin, buttonHeight);

        // 4* -> 3*
        // because we dont have a text box at the bottom
        // so more space for cards
        chatHeight = (h - 3 * (buttonHeight + margin) - 2 * margin);

        final int cardsSize = 32;
        final int offset = buttonHeight + margin;

        int widgetWidth = Math.max(cardsSize, (chatWidth / cardsSize) * cardsSize);
        int widgetHeight = Math.max(cardsSize, (chatHeight / cardsSize) * cardsSize);

        addRenderableWidget(cardStackNameWidget = new TextWidget(x, y, w, buttonHeight, this::getShownZoneName));
        y += offset;

        addRenderableWidget(scrollUpButton = new YdmDefButton(x, y, w, buttonHeight,
                Component.translatable("container." + YDM.MOD_ID + ".duel.up_arrow"),
                this::scrollButtonClicked, this::scrollButtonHovered));
        y += offset;

        int columns = chatWidth / cardsSize;
        int rows = chatHeight / cardsSize;
        addRenderableWidget(viewCardStackWidget = new ViewCardStackWidget(this, x + (w - widgetWidth) / 2, y + (chatHeight - widgetHeight) / 2,
                chatWidth, chatHeight, Component.empty(),
                this::viewCardStackClicked, this::viewCardStackTooltip)
                .setRowsAndColumns(cardsSize, rows, columns));
        y += chatHeight + margin;

        addRenderableWidget(scrollDownButton = new YdmDefButton(x, y, w, buttonHeight,
                Component.translatable("container." + YDM.MOD_ID + ".duel.down_arrow"),
                this::scrollButtonClicked, this::scrollButtonHovered));
        y += offset;
    }

    @Override
    protected void containerTick()
    {
        animationsWidget.tick();
        super.containerTick();
    }

    protected ZoneWidget createZoneWidgetForZone(Zone zone)
    {
        if(zone.getType() == ZoneTypes.MONSTER.get() ||
                zone.getType() == ZoneTypes.EXTRA_MONSTER_RIGHT.get() ||
                zone.getType() == ZoneTypes.EXTRA_MONSTER_LEFT.get())
        {
            return new MonsterZoneWidget(zone, this,
                    this::zoneClicked, this::zoneTooltip);
        }
        else if(zone.getType() == ZoneTypes.HAND.get())
        {
            return new HandZoneWidget(zone, this,
                    this::zoneClicked, this::zoneTooltip);
        }
        else if(zone.getType() == ZoneTypes.EXTRA_DECK.get() ||
                zone.getType() == ZoneTypes.GRAVEYARD.get() ||
                zone.getType() == ZoneTypes.BANISHED.get() ||
                zone.getType() == ZoneTypes.EXTRA.get())
        {
            return new NonSecretStackZoneWidget(zone, this,
                    this::zoneClicked, this::zoneTooltip);
        }
        else if(zone.getType() == ZoneTypes.DECK.get())
        {
            return new StackZoneWidget(zone, this,
                    this::zoneClicked, this::zoneTooltip);
        }
        else
        {
            return new ZoneWidget(zone, this,
                    this::zoneClicked, this::zoneTooltip);
        }
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(gui, partialTicks, mouseX, mouseY);

        ScreenUtil.white();
//        RenderSystem.setShaderTexture(0, DuelContainerScreen.DUEL_BACKGROUND_GUI_TEXTURE);
        gui.blit(DUEL_BACKGROUND_GUI_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
//        RenderSystem.setShaderTexture(0, DuelContainerScreen.DUEL_FOREGROUND_GUI_TEXTURE);
        gui.blit(DUEL_FOREGROUND_GUI_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if(cardInfo != null)
        {
            CardRenderUtil.renderCardInfo(gui, cardInfo.getCardHolder(), cardInfo.getIsToken(), (width - imageWidth) / 2);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int x, int y)
    {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        forceFinishAnimations(mouseX, mouseY);

        if(lifePointsWidget != null && lifePointsWidget.isFocused() && !lifePointsWidget.isMouseOver(mouseX, mouseY))
        {
            lifePointsWidget.setFocused(false);
        }

        if(button == GLFW.GLFW_MOUSE_BUTTON_2)
        {
            resetToNormalZoneWidgets();
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void handleAction(Action action)
    {
        action.initClient(getDuelManager().getPlayField());
        getDuelManager().actions.add(action);

        // all actions must return an animation
        // otherwise, their order might be disrupted
        // eg Action1 still in animation, then Action2 (without animation) gets done before Action1 finishes
        // so, by default a dummy animation is returned, doing nothing, lasting 1 tick, just doing the Action
        Animation animation = getAnimationForAction(action);
        playAnimation(animation);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(lifePointsWidget != null && lifePointsWidget.isFocused())
        {
            if(keyCode == GLFW.GLFW_KEY_ENTER)
            {
                parseAndSendLPChange();
                return true;
            }
            else
            {
                return lifePointsWidget.keyPressed(keyCode, scanCode, modifiers);
            }
        }
        else
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    public void flip() {
        view = view.opponent();
        
        /*//re-init anyways, this is not needed
        for(ZoneWidget w : this.zoneWidgets)
        {
            w.flip(this.width, this.height);
        }
        */

        reInit();
//        getMenu().requestFullUpdate();
    }

    public void reload()
    {
        getMenu().requestFullUpdate();
    }

    public void resetToNormalZoneWidgets()
    {
        removeClickedZone();
        removeInteractionWidgets();

        for(ZoneWidget w : zoneWidgets)
        {
            w.active = true;
        }

        makeChatVisible();
        isAdvanced = false;
    }

    protected void viewZone(ZoneWidget w, boolean forceFaceUp)
    {
        MutableComponent t = Component.literal("").append(w.getMessage());

        if(w.zone.getCardsAmount() > 0)
        {
            t.append(" (" + w.zone.getCardsAmount() + ")");
        }

        viewCards(w.zone.getCardsList(), t, forceFaceUp);
    }

    protected void viewCards(List<DuelCard> cards, Component name, boolean forceFaceUp)
    {
        viewCardStackWidget.activate(cards, forceFaceUp);
        nameShown = name;

        updateScrollButtonStatus();
        makeChatInvisible();
    }

    protected void updateScrollButtonStatus()
    {
        scrollUpButton.active = false;
        scrollUpButton.visible = false;
        scrollDownButton.active = false;
        scrollDownButton.visible = false;
        cardStackNameWidget.visible = false;

        if(viewCardStackWidget.active)
        {
            scrollUpButton.visible = true;
            scrollDownButton.visible = true;
            cardStackNameWidget.visible = true;

            if(viewCardStackWidget.getCurrentRow() > 0)
            {
                scrollUpButton.active = true;
            }

            if(viewCardStackWidget.getCurrentRow() < viewCardStackWidget.getMaxRows())
            {
                scrollDownButton.active = true;
            }
        }
    }

    protected void updateLeftButtonStatus()
    {
        if(clickedZoneWidget != null &&
                clickedZoneWidget.zone.getType().getCanHaveCounters() &&
                clickedZoneWidget.zone.getCardsAmount() > 0 &&
                clickedZoneWidget.zone.getOwner() == getZoneOwner())
        {
            addCounterButton.active = true;
            removeCounterButton.active = true;
        }
        else
        {
            addCounterButton.active = false;
            removeCounterButton.active = false;
        }

        advancedOptionsButton.active = clickedZoneWidget != null;
    }

    protected void updateRightButtonStatus()
    {
        boolean isTurn;

        if(getZoneOwner() == ZoneOwner.NONE)
        {
            isTurn = getPlayField().isPlayerTurn(ZoneOwner.PLAYER1);
        }
        else
        {
            isTurn = getPlayField().isPlayerTurn(getZoneOwner());
        }

        if(isTurn)
        {
            phaseWidget.setBlue();
            prevPhaseButton.setBlue();
            nextPhaseButton.setBlue();
        }
        else
        {
            phaseWidget.setRed();
            prevPhaseButton.setRed();
            nextPhaseButton.setRed();
        }

        isTurn = getZoneOwner() != ZoneOwner.NONE && getPlayField().isPlayerTurn(getZoneOwner());

        if(isTurn)
        {
            prevPhaseButton.active = !getPlayField().getPhase().isFirst();
            nextPhaseButton.active = true;
            // next phase button is always active
            // if last phase: we end turn
        }
        else
        {
            prevPhaseButton.active = false;
            nextPhaseButton.active = false;
        }
    }

    protected ZoneWidget getZoneWidget(Zone zone)
    {
        return zoneWidgets.get(zone.index);
    }

    protected void playAnimation(Animation a)
    {
        if(a != null)
        {
            animationsWidget.addAnimation(a);
        }
    }

    @Nullable
    public Animation getAnimationForAction(Action action0) {
        if(action0 instanceof MoveAction action) {

            CardPosition sourcePosition = action.sourceCardPosition;

            if(!sourcePosition.isFaceUp && action.sourceZone.getOwner() == getZoneOwner() && action.sourceZone.type.getShowFaceDownCardsToOwner())
            {
                sourcePosition = sourcePosition.flip();
            }

            CardPosition destinationPosition = action.destinationCardPosition;

            if(!destinationPosition.isFaceUp && action.destinationZone.getOwner() == getZoneOwner() && action.destinationZone.type.getShowFaceDownCardsToOwner())
            {
                destinationPosition = destinationPosition.flip();
            }

            Animation moveAnimation = new MoveAnimation(
                    getView(),
                    action.card,
                    getZoneWidget(action.sourceZone),
                    getZoneWidget(action.destinationZone),
                    sourcePosition,
                    destinationPosition)
                    .setOnStart(action::removeCardFromZone)
                    .setOnEnd(() ->
                    {
                        action.addCard();
                        action.finish();
                        repopulateInteractions();
                    });

            if(action.actionType == ActionTypes.SPECIAL_SUMMON.get())
            {
                ZoneWidget w = getZoneWidget(action.destinationZone);

                int size = Math.max(w.getWidth(), w.getHeight());
                Animation ringAnimation = new SpecialSummonAnimation(w.getAnimationDestX(), w.getAnimationDestY(), size, size + size / 2);

                Queue<Animation> queue = new LinkedList<>();
                queue.add(moveAnimation);
                queue.add(ringAnimation);

                return new QueueAnimation(queue);
            }
            else
            {
                return moveAnimation;
            }
        } else if(action0 instanceof ChangePositionAction action) {

            if(action.card == action.sourceZone.getTopCardSafely())
            {
                ZoneOwner owner = action.sourceZone.getOwner();

                return new MoveAnimation(
                        getView(),
                        action.card,
                        getZoneWidget(action.sourceZone),
                        getZoneWidget(action.sourceZone),
                        action.sourceCardPosition,
                        action.destinationCardPosition)
                        .setOnStart(() ->
                        {
                            action.sourceZone.removeCardKeepCounters(action.sourceCardIndex);
                        })
                        .setOnEnd(() ->
                        {
                            action.sourceZone.addCard(owner, action.card, action.sourceCardIndex);
                            action.sourceZone.getCard(action.sourceCardIndex).setPosition(action.destinationCardPosition);
                            repopulateInteractions();
                        });
            }
        }
        else if(action0 instanceof ListAction action) {

            if(!action.actions.isEmpty())
            {
                List<Animation> animations = new ArrayList<>(action.actions.size());

                Animation animation;
                for(Action a : action.actions)
                {
                    animation = getAnimationForAction(a);

                    if(animation != null)
                    {
                        animations.add(animation);
                    }
                }

                ParallelListAnimation listAnimation = new ParallelListAnimation(animations);

                if(action.actionType == ActionTypes.SPECIAL_SUMMON_OVERLAY.get()) {
                    Queue<Animation> queue = new LinkedList<>();
                    queue.add(listAnimation);

                    MoveTopAction moveAction = (MoveTopAction) action.actions.get(action.actions.size() - 1);

                    ZoneWidget w = getZoneWidget(moveAction.destinationZone);

                    int size = Math.max(w.getWidth(), w.getHeight());
                    queue.add(new SpecialSummonOverlayAnimation(w.getAnimationDestX(), w.getAnimationDestY(), size, size + size / 2));

                    return new QueueAnimation(queue);
                } else {
                    return listAnimation;
                }
            }
        }
        else if(action0 instanceof AttackAction action) {

            return new AttackAnimation(getView(), getZoneWidget(action.sourceZone), getZoneWidget(action.attackedZone));
        }
        else if(action0 instanceof CreateTokenAction action) {

            ZoneWidget w = getZoneWidget(action.destinationZone);

            int size = Math.max(w.getWidth(), w.getHeight());
            return new SpecialSummonTokenAnimation(w.getAnimationDestX(), w.getAnimationDestY(), size, size + size / 2)
                    .setOnStart(() ->
                    {
                        action.doAction();
                        repopulateInteractions();
                    });
        } else if(action0 instanceof RemoveTokenAction action) {

            ZoneWidget w = getZoneWidget(action.destinationZone);

            int size = Math.max(w.getWidth(), w.getHeight());
            return new RemoveTokenAnimation(w.getAnimationDestX(), w.getAnimationDestY(), size, size + size / 2)
                    .setOnEnd(() ->
                    {
                        action.doAction();
                        repopulateInteractions();
                    });
        } else if(action0 instanceof IAnnouncedAction action) {

            if(action.announceOnField())
            {
                ZoneWidget w = getZoneWidget(action.getFieldAnnouncementZone());

                return new TextAnimation(action0.getActionType().getLocal(), w.getAnimationDestX(), w.getAnimationDestY())
                        .setOnStart(() -> handleAnnouncedAction(action0));
            }
        }
        else if(action0.actionType == ActionTypes.CHANGE_PHASE.get() || action0.actionType == ActionTypes.END_TURN.get())
        {
            Animation a = getDefaultAnimation(action0);

            a.setOnEnd(this::updateRightButtonStatus);

            return a;
        }

        return getDefaultAnimation(action0);
    }

    protected Animation getDefaultAnimation(Action action)
    {
        return new DummyAnimation().setOnStart(action::doAction);
    }

    protected void handleAnnouncedAction(Action action) {
        if(action instanceof ViewZoneAction a) {
            if(getZoneOwner() == a.sourceZone.getOwner()) {
                viewZone(a.sourceZone);
            }
        }
        else if(action instanceof ShowZoneAction a)
        {
            if(getZoneOwner() != a.sourceZone.getOwner())
            {
                viewZone(a.sourceZone);
            }
        }
        else if(action instanceof ShowCardAction a)
        {
            if(getZoneOwner() != a.sourceZone.getOwner())
            {
                viewCards(a.sourceZone, ImmutableList.of(a.card));
            }
        }
        else if(action instanceof ShuffleAction a)
        {

            // if we have a zone selected/viewed and it is shuffled, we gotta deselect it / stop viewing it
            if(clickedZoneWidget != null &&
                    clickedZoneWidget.zone == a.sourceZone)
            {
                resetToNormalZoneWidgets();
            }

            a.doAction();
        }
    }

    protected void forceFinishAnimations(double mouseX, double mouseY)
    {
        animationsWidget.forceFinish();
    }

    protected void viewZone(Zone zone) {
        for(ZoneWidget w : zoneWidgets) {
            if(w.zone == zone) {
                resetToNormalZoneWidgets();

                w.hoverCard = null;

                zoneClicked(w);
                clickedCard = null;
                viewZone(w, true);
                return;
            }
        }
    }

    protected void viewCards(Zone zone, List<DuelCard> cards) {
        for(ZoneWidget w : zoneWidgets) {
            if(w.zone == zone) {
                resetToNormalZoneWidgets();

                w.hoverCard = null;

                zoneClicked(w);
                viewCards(cards, w.getMessage(), true);
                return;
            }
        }
    }

    protected void zoneClicked(ZoneWidget widget) {
        if(!widget.active) {
            return;
        }

        ZoneOwner owner = getZoneOwner();

        if(owner != ZoneOwner.NONE) {
            setClickedZoneWidgetAndCard(widget, widget.hoverCard);
            findAndPopulateInteractions(widget, false);
        }

        if(widget.openAdvancedZoneView()) {
            viewZone(widget, owner == widget.zone.getOwner() && widget.zone.type.getShowFaceDownCardsToOwner());
        }

        updateLeftButtonStatus();
    }

    protected void findAndPopulateInteractions(ZoneWidget widget, boolean isAdvanced) {
        ZoneOwner owner = getZoneOwner();

        removeInteractionWidgets();

        interactionWidgets = new ArrayList<>();

        for(ZoneWidget w : zoneWidgets)
        {
            w.addInteractionWidgets(owner, clickedZoneWidget.zone, clickedCard, getDuelManager(), interactionWidgets, this::interactionClicked, this::interactionTooltip, isAdvanced);
            w.active = false;
        }

        renderables.addAll(interactionWidgets); //FIXME this was "buttons"
        ((List<GuiEventListener>) children()).addAll(interactionWidgets); //FIXME this was "children"
    }

    protected void interactionClicked(InteractionWidget widget)
    {
        Action action = widget.getInteraction().action;

        ZoneType interactorType = widget.getInteraction().interactor.getType();

        if(interactorType.getKeepFocusedAfterInteraction() &&
                (!interactorType.getIsSecret() || viewCardStackWidget.active))
        {
            clickedCard = null;
            repopulateInteractions();
        }
        else if(shouldRepopulateInteractions(widget))
        {
            repopulateInteractions();
        }
        else
        {
            resetToNormalZoneWidgets();
        }

        requestDuelAction(action);
    }

    protected boolean shouldRepopulateInteractions(InteractionWidget clickedWidget)
    {
        return clickedWidget.getInteraction().action.getActionType() == ActionTypes.CREATE_TOKEN.get();
    }

    protected void repopulateInteractions()
    {
        if(clickedZoneWidget != null)
        {
            findAndPopulateInteractions(clickedZoneWidget, isAdvanced);
        }
        else
        {
            resetToNormalZoneWidgets();
        }
    }

    protected void requestDuelAction(Action action)
    {
        YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.RequestDuelAction(getDuelManager().headerFactory.get(), action));
    }

    protected Component getShownZoneName()
    {
        return nameShown == null ? Component.empty() : nameShown;
    }

    protected void viewCardStackClicked(ViewCardStackWidget widget)
    {
        ZoneWidget w = clickedZoneWidget;
        boolean forceFaceUp = widget.getForceFaceUp();

        if(w != null)
        {
            w.active = true;
            w.hoverCard = widget.hoverCard;
            zoneClicked(w);

            if(forceFaceUp)
            {
                widget.forceFaceUp();
            }
        }
    }

    protected void parseAndSendLPChange()
    {
        if(getZoneOwner().isPlayer())
        {
            String text = lifePointsWidget.getValue();
            lifePointsWidget.setValue("");

            if(text.length() > 1)
            {
                if(text.startsWith("+"))
                {
                    text = text.substring(1);
                }

                int lp = Integer.valueOf(text);
                requestDuelAction(new ChangeLPAction(ActionTypes.CHANGE_LP.get(), lp, getZoneOwner()));
            }
        }
    }

    protected void scrollButtonClicked(Button button) {
        if(viewCardStackWidget.active) {
            if(button == scrollUpButton) {
                viewCardStackWidget.decreaseCurrentRow();
            } else if(button == scrollDownButton) {
                viewCardStackWidget.increaseCurrentRow();
            }
        }

        updateScrollButtonStatus();
    }

    protected void middleButtonClicked(AbstractWidget w)
    {
        if(w == reloadButton) {
            reload();
        } else if(w == flipViewButton) {
            flip();
        } else if(w == admitDefeatButton) {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.SendAdmitDefeat(getHeader()));
        } else if(w == offerDrawButton) {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.SendOfferDraw(getHeader()));
        }
    }

    protected void leftButtonClicked(Button button)
    {
        if(button == coinFlipButton)
        {
            requestDuelAction(new CoinFlipAction(ActionTypes.COIN_FLIP.get()));
        }
        else if(button == diceRollButton)
        {
            requestDuelAction(new DiceRollAction(ActionTypes.DICE_ROLL.get()));
        }
        else if(getClickedZone() != null && clickedZoneWidget.zone.getOwner() == getZoneOwner() && button == addCounterButton)
        {
            requestDuelAction(new ChangeCountersAction(ActionTypes.CHANGE_COUNTERS.get(), getClickedZone().index, +1));
        }
        else if(getClickedZone() != null && clickedZoneWidget.zone.getOwner() == getZoneOwner() && button == removeCounterButton)
        {
            requestDuelAction(new ChangeCountersAction(ActionTypes.CHANGE_COUNTERS.get(), getClickedZone().index, -1));
        }
        else if(button == advancedOptionsButton)
        {
            isAdvanced = !isAdvanced;
            repopulateInteractions();
        }
    }

    protected void rightButtonClicked(AbstractWidget w)
    {
        DuelPhase phase = getPlayField().getPhase();

        if(w == prevPhaseButton)
        {
            if(!phase.isFirst())
            {
                DuelPhase prevPhase = DuelPhase.getFromIndex((byte) (phase.getIndex() - 1));
                requestDuelAction(new ChangePhaseAction(ActionTypes.CHANGE_PHASE.get(), prevPhase));
            }
        }
        else if(w == nextPhaseButton)
        {
            if(phase.isLast())
            {
                requestDuelAction(new EndTurnAction(ActionTypes.END_TURN.get()));
            }
            else
            {
                DuelPhase nextPhase = DuelPhase.getFromIndex((byte) (phase.getIndex() + 1));
                requestDuelAction(new ChangePhaseAction(ActionTypes.CHANGE_PHASE.get(), nextPhase));
            }
        }
        update();
    }

    protected void zoneTooltip(AbstractWidget w0, GuiGraphics gui, int mouseX, int mouseY) {
        List<Component> tooltip = new LinkedList<>();

        ZoneWidget w = (ZoneWidget) w0;

        MutableComponent t = Component.literal("").append(w.getMessage());

        if(w.zone.getCardsAmount() > 0) {
            t.append(" (" + w.zone.getCardsAmount() + ")");
        }

        tooltip.add(t);

        if(w.zone.getType().getCanHaveCounters() && w.zone.getCounters() > 0) {
            tooltip.add(Component.translatable("container." + YDM.MOD_ID + ".duel.counters").append(": " + w.zone.getCounters()));
        }

        renderTooltip(gui, tooltip, mouseX, mouseY);
    }

    protected void interactionTooltip(AbstractWidget w, GuiGraphics gui, int mouseX, int mouseY)
    {
        renderTooltip(gui, w.getMessage(), mouseX, mouseY);
    }

    protected void viewCardStackTooltip(AbstractWidget w, GuiGraphics gui, int mouseX, int mouseY)
    {
    }

    protected void lpTooltip(ZoneOwner owner, @Nullable MutableComponent playerName, AbstractWidget w, GuiGraphics gui, int mouseX, int mouseY)
    {
        List<Component> list = new LinkedList<>();

        list.add(Component.literal(String.valueOf(getPlayField().getLifePoints(owner))));

        if(playerName != null) {
            list.add(playerName);
        } else {
            list.add(getUnknownPlayerName());
        }

        renderTooltip(gui, list, mouseX, mouseY);
    }

    protected void lpTooltipView(AbstractWidget w, GuiGraphics gui, int mouseX, int mouseY)
    {
        lpTooltip(getView(), getViewName(), w, gui, mouseX, mouseY);
    }

    protected void lpTooltipViewOpponent(AbstractWidget w, GuiGraphics gui, int mouseX, int mouseY)
    {
        lpTooltip(getView().opponent(), getViewOpponentName(), w, gui, mouseX, mouseY);
    }

    protected void lpTextFieldWidget(AbstractWidget w, GuiGraphics gui, int mouseX, int mouseY)
    {
        List<Component> list = new LinkedList<>();

        list.add(Component.translatable("container." + YDM.MOD_ID + ".duel.change_lp_tooltip1"));
        list.add(Component.translatable("container." + YDM.MOD_ID + ".duel.change_lp_tooltip2"));
        list.add(Component.translatable("container." + YDM.MOD_ID + ".duel.change_lp_tooltip3"));

        renderTooltip(gui, list, mouseX, mouseY);
    }

    protected void phaseWidgetHovered(AbstractWidget w, GuiGraphics gui, int mouseX, int mouseY)
    {
        renderTooltip(gui, getCurrentPhaseTooltip(), mouseX, mouseY);
    }

    protected void scrollButtonHovered(AbstractWidget w, GuiGraphics gui, int mouseX, int mouseY) {
    }

    protected void middleButtonHovered(AbstractWidget w, GuiGraphics gui, int mouseX, int mouseY)
    {
        if(w == reloadButton)
        {
            renderTooltip(gui, Component.translatable("container." + YDM.MOD_ID + ".duel.reload"), mouseX, mouseY);
        }
        else if(w == flipViewButton)
        {
            renderTooltip(gui, Component.translatable("container." + YDM.MOD_ID + ".duel.flip_view"), mouseX, mouseY);
        }
        else if(w == offerDrawButton)
        {
            renderTooltip(gui, Component.translatable("container." + YDM.MOD_ID + ".duel.offer_draw"), mouseX, mouseY);
        }
        else if(w == admitDefeatButton)
        {
            renderTooltip(gui, Component.translatable("container." + YDM.MOD_ID + ".duel.admit_defeat"), mouseX, mouseY);
        }
    }

    private void renderTooltip(GuiGraphics gui, Component translatable, int mouseX, int mouseY) {
        super.renderTooltip(gui, List.of(translatable), mouseX, mouseY);
    }

    protected void leftButtonHovered(AbstractWidget w, GuiGraphics gui, int mouseX, int mouseY)
    {
        if(w == coinFlipButton)
        {
            renderTooltip(gui, Component.translatable("container." + YDM.MOD_ID + ".duel.coin_flip"), mouseX, mouseY);
        }
        else if(w == diceRollButton)
        {
            renderTooltip(gui, Component.translatable("container." + YDM.MOD_ID + ".duel.dice_roll"), mouseX, mouseY);
        }
        else if(w == addCounterButton)
        {
            renderTooltip(gui, Component.translatable("container." + YDM.MOD_ID + ".duel.add_counter"), mouseX, mouseY);
        }
        else if(w == removeCounterButton)
        {
            renderTooltip(gui, Component.translatable("container." + YDM.MOD_ID + ".duel.remove_counter"), mouseX, mouseY);
        }
        else if(w == advancedOptionsButton)
        {
            if(!isAdvanced)
            {
                renderTooltip(gui, Component.translatable("container." + YDM.MOD_ID + ".duel.advanced_options"), mouseX, mouseY);
            }
            else
            {
                renderTooltip(gui, Component.translatable("container." + YDM.MOD_ID + ".duel.basic_options"), mouseX, mouseY);
            }
        }
    }

    protected void rightButtonHovered(AbstractWidget w, GuiGraphics gui, int mouseX, int mouseY)
    {
        DuelPhase phase = getPlayField().getPhase();

        if(w == prevPhaseButton)
        {
            if(!phase.isFirst())
            {
                DuelPhase prevPhase = DuelPhase.getFromIndex((byte) (phase.getIndex() - 1));
                renderTooltip(gui, (getPhaseTooltip(prevPhase).append(" ").append(Component.translatable("container." + YDM.MOD_ID + ".duel.left_arrow"))), mouseX, mouseY);
            }
        }
        else if(w == nextPhaseButton)
        {
            if(phase.isLast())
            {
                renderTooltip(gui, Component.translatable("action." + YDM.MOD_ID + ".end_turn"), mouseX, mouseY);
            }
            else
            {
                DuelPhase nextPhase = DuelPhase.getFromIndex((byte) (phase.getIndex() + 1));
                renderTooltip(gui, Component.translatable("container." + YDM.MOD_ID + ".duel.right_arrow").append(" ").append(getPhaseTooltip(nextPhase)), mouseX, mouseY);
            }
        }
    }

    public MutableComponent getPhaseShort()
    {
        return Component.translatable("container." + YDM.MOD_ID + ".duel." + getPlayField().getPhase().local + ".short");
    }

    public MutableComponent getCurrentPhaseTooltip()
    {
        return getPhaseTooltip(getPlayField().getPhase());
    }

    public MutableComponent getPhaseTooltip(DuelPhase phase)
    {
        return Component.translatable("container." + YDM.MOD_ID + ".duel." + phase.local);
    }

    protected void removeInteractionWidgets()
    {
        renderables.removeIf((w) -> w instanceof InteractionWidget);
        children().removeIf((w) -> w instanceof InteractionWidget);
    }

    protected void removeClickedZone()
    {
        setClickedZoneWidgetAndCard(null, null);
        viewCardStackWidget.deactivate();
        nameShown = null;
        updateScrollButtonStatus();
        updateLeftButtonStatus();
    }

    protected void setClickedZoneWidgetAndCard(ZoneWidget zone, DuelCard card)
    {
        clickedZoneWidget = zone;
        clickedCard = card;

        if(getZoneOwner().isPlayer())
        {
            getPlayField().setClickedForPlayer(getZoneOwner(), zone != null ? zone.zone : null, card);
            requestDuelAction(new SelectAction(ActionTypes.SELECT.get(), getClickedZone(), getClickedCard(), getZoneOwner()));
        }
    }

    protected MutableComponent getUnknownPlayerName()
    {
        return Component.translatable("container." + YDM.MOD_ID + ".duel.unknown_player")
                .withStyle((style) -> style.applyFormat(ChatFormatting.ITALIC))
                .withStyle((style) -> style.applyFormat(ChatFormatting.RED));
    }

    protected MutableComponent getViewName()
    {
        return getView() == ZoneOwner.PLAYER1 ? getPlayer1Name() : getPlayer2Name();
    }

    protected MutableComponent getViewOpponentName()
    {
        return getView() == ZoneOwner.PLAYER1 ? getPlayer2Name() : getPlayer1Name();
    }

    protected MutableComponent getPlayer1Name()
    {
        if(getDuelManager().player1 != null)
        {
            return (MutableComponent) getDuelManager().player1.getName();
        }
        else
        {
            if(!fetchPlayer1Name() && player1Name == null)
            {
                // we have never fetched the name and the player isnt here
                return null;
            }
            else
            {
                return player1Name.withStyle((style) -> style.applyFormat(ChatFormatting.RED));
            }
        }
    }

    protected MutableComponent getPlayer2Name()
    {
        if(getDuelManager().player2 != null)
        {
            return (MutableComponent) getDuelManager().player2.getName();
        }
        else
        {
            if(!fetchPlayer2Name() && player2Name == null)
            {
                // we have never fetched the name and the player isnt here
                return null;
            }
            else
            {
                return player2Name.withStyle((style) -> style.applyFormat(ChatFormatting.RED));
            }
        }
    }

    // return true if player 1 is still in the same dimension
    protected boolean fetchPlayer1Name()
    {
        // TODO sync UUIDs to client, instead of setting roles only for uuid-fetchable players

        if(getDuelManager().player1Id == null)
        {
            return false;
        }

        Player p = minecraft.level.getPlayerByUUID(getDuelManager().player1Id);
        if(p != null)
        {
            player1Name = (MutableComponent) p.getName();
            return true;
        }
        else
        {
            return false;
        }
    }

    // return true if player 2 is still in the same dimension
    protected boolean fetchPlayer2Name()
    {
        if(getDuelManager().player2Id == null)
        {
            return false;
        }

        Player p = minecraft.level.getPlayerByUUID(getDuelManager().player2Id);
        if(p != null)
        {
            player2Name = (MutableComponent) p.getName();
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public ZoneOwner getView()
    {
        return view;
    }

    @Override
    public void renderCardInfo(GuiGraphics gui, DuelCard card)
    {
        cardInfo = card;
    }

    public static void renderSelectedRect(GuiGraphics gui, float x, float y, float w, float h)
    {
        ScreenUtil.drawLineRect(gui, x - 1, y - 1, w + 2, h + 2, 2, 0, 0, 1F, 1F);
    }

    public static void renderEnemySelectedRect(GuiGraphics gui, float x, float y, float w, float h)
    {
        ScreenUtil.drawLineRect(gui, x - 1, y - 1, w + 2, h + 2, 2, 1F, 0, 0, 1F);
    }

    public static void renderBothSelectedRect(GuiGraphics gui, float x, float y, float w, float h)
    {
        ScreenUtil.drawLineRect(gui, x - 1, y - 1, w + 2, h + 2, 2, 1F, 0, 1F, 1F);
    }
}
