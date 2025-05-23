package de.cas_ual_ty.ydm.duel.screen;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.SwitchableContainerScreen;
import de.cas_ual_ty.ydm.deckbox.DeckBoxScreen;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.duel.*;
import de.cas_ual_ty.ydm.duel.action.Action;
import de.cas_ual_ty.ydm.duel.network.DuelMessageHeader;
import de.cas_ual_ty.ydm.duel.network.DuelMessages;
import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import de.cas_ual_ty.ydm.duel.screen.widget.DisplayChatWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public abstract class DuelContainerScreen<E extends DuelContainer> extends SwitchableContainerScreen<E>
{
    public static final ResourceLocation DUEL_FOREGROUND_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_foreground.png");
    public static final ResourceLocation DUEL_BACKGROUND_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_background.png");

    public static final ResourceLocation DECK_BACKGROUND_GUI_TEXTURE = DeckBoxScreen.DECK_BOX_GUI_TEXTURE;

    protected DuelScreenConstructor<E>[] screensForEachState;

    protected Button chatUpButton;
    protected Button chatDownButton;
    protected DisplayChatWidget chatWidget;
    protected EditBox textFieldWidget;

    protected Button duelChatButton;
    protected Button worldChatButton;
    protected boolean duelChat;

    protected List<Component> worldChatMessages;

    protected Inventory playerInv;

    @SuppressWarnings("unchecked")
    public DuelContainerScreen(E screenContainer, Inventory inv, Component titleIn)
    {
        super(screenContainer, inv, titleIn);
        imageWidth = 234;
        imageHeight = 250;

        worldChatMessages = new ArrayList<>(32);
        textFieldWidget = null;
        duelChat = true;

        //default
        screensForEachState = new DuelScreenConstructor[DuelState.VALUES.length];
        screensForEachState[DuelState.IDLE.getIndex()] = DuelScreenIdle::new;
        screensForEachState[DuelState.PREPARING.getIndex()] = DuelScreenPreparing::new;
        screensForEachState[DuelState.END.getIndex()] = DuelScreenPreparing::new;
        screensForEachState[DuelState.DUELING.getIndex()] = DuelScreenDueling::new;
        screensForEachState[DuelState.SIDING.getIndex()] = DuelScreenDueling::new;

        playerInv = inv;
    }

    public DuelContainerScreen<E> setScreenForState(DuelState state, DuelScreenConstructor<E> screen)
    {
        screensForEachState[state.getIndex()] = screen;
        return this;
    }

    protected DuelContainerScreen<E> createNewScreenForState(DuelState state)
    {
        return screensForEachState[state.getIndex()].construct(menu, playerInv, title);
    }

    public final void duelStateChanged()
    {
        switchScreen(createNewScreenForState(getState()));
    }

    public final void reInit() {
        this.init(minecraft, width, height);
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTicks, int mouseX, int mouseY)
    {
        ScreenUtil.renderDisabledRect(gui, 0, 0, width, height);


        // 绑定白色着色器，确保之后的 blit 不会被染色
        ScreenUtil.white();

        // 用 GuiGraphics.blit 画背景
        gui.blit(
                DuelContainerScreen.DUEL_BACKGROUND_GUI_TEXTURE,
                leftPos, topPos,
                0, 0,
                imageWidth, imageHeight
        );
    }

    @Override
    public void switchScreen(AbstractContainerScreen<E> s)
    {
        super.switchScreen(s);

        if(s instanceof DuelContainerScreen)
        {
            DuelContainerScreen<E> screen = (DuelContainerScreen<E>) s;
            screen.screensForEachState = screensForEachState;
            screen.worldChatMessages = worldChatMessages;
        }
    }

    @Override
    protected void onGuiClose()
    {
        super.onGuiClose();
        getDuelManager().reset();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(textFieldWidget != null && textFieldWidget.isFocused() && !textFieldWidget.isMouseOver(mouseX, mouseY))
        {
//            textFieldWidget.setFocus(false);
            textFieldWidget.setFocused(false);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(textFieldWidget != null && textFieldWidget.isFocused())
        {
            if(keyCode == GLFW.GLFW_KEY_ENTER)
            {
                sendChat();
                return true;
            }
            else
            {
                return textFieldWidget.keyPressed(keyCode, scanCode, modifiers);
            }
        }
        else
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }


    public void renderTooltip(GuiGraphics gui, List<Component> tooltips, int mouseX, int mouseY) {

        gui.pose().pushPose();
        gui.pose().translate(0, 0, 10D);

        gui.renderComponentTooltip(font, tooltips, mouseX, mouseY);

        gui.pose().popPose();
    }

    public void renderDisabledTooltip(GuiGraphics gui, List<Component> tooltips, int mouseX, int mouseY) {
        tooltips.add(Component.literal("DISABLED").withStyle((s) -> s.applyFormat(ChatFormatting.ITALIC).applyFormat(ChatFormatting.RED)));
        tooltips.add(Component.literal("COMING SOON").withStyle((s) -> s.applyFormat(ChatFormatting.ITALIC).applyFormat(ChatFormatting.RED)));
        this.renderTooltip(gui, tooltips, mouseX, mouseY);

    }


    public void renderDisabledTooltip(GuiGraphics gui, @Nullable Component text, int mouseX, int mouseY)
    {
        List<Component> tooltips = new LinkedList<>();

        if(text != null)
        {
            tooltips.add(text);
        }

        renderDisabledTooltip(gui, tooltips, mouseX, mouseY);
    }

    protected void initDefaultChat(int width, int height)
    {
        final int margin = 4;
        final int buttonHeight = 20;

        int x = leftPos + imageWidth + margin;
        int y = topPos + margin;

        int maxWidth = Math.min(160, (this.width - imageWidth) / 2 - 2 * margin);
        int maxHeight = imageHeight;

        int chatWidth = maxWidth;
        int chatHeight = (maxHeight - 4 * (buttonHeight + margin) - 2 * margin);

        initChat(width, height, x, y, maxWidth, maxHeight, chatWidth, chatHeight, margin, buttonHeight);
    }

    protected void initChat(int screenWidth,
                            int screenHeight,
                            int x, int y,
                            int w, int h,
                            int chatWidth, int chatHeight,
                            int margin,
                            int buttonHeight) {
        final int offset = buttonHeight + margin;
        // 计算左右按钮宽度
        int halfW = w / 2;
        int extraOff = halfW % 2;

        // Duel Chat / World Chat 切换按钮
        duelChatButton = Button.builder(
                        Component.translatable("container." + YDM.MOD_ID + ".duel.duel_chat"),
                        btn -> switchChat()
                )
                .pos(x, y)
                .size(halfW, buttonHeight)
                .build();
        addRenderableWidget(duelChatButton);

        worldChatButton = Button.builder(
                        Component.translatable("container." + YDM.MOD_ID + ".duel.world_chat"),
                        btn -> switchChat()
                )
                .pos(x + halfW - extraOff, y)
                .size(halfW + extraOff, buttonHeight)
                .build();
        addRenderableWidget(worldChatButton);

        y += offset;

        // 上翻页按钮
        chatUpButton = Button.builder(
                        Component.translatable("container." + YDM.MOD_ID + ".duel.up_arrow"),
                        this::chatScrollButtonClicked
                )
                .pos(x, y)
                .size(w, buttonHeight)
                .build();
        addRenderableWidget(chatUpButton);

        y += offset;

        // 聊天显示区域（自定义 Widget）
        chatWidget = new DisplayChatWidget(
                x,
                y - (chatHeight % font.lineHeight) / 2,
                chatWidth,
                chatHeight,
                Component.empty()
        );
        addRenderableWidget(chatWidget);

        y += chatHeight + margin;

        // 下翻页按钮
        chatDownButton = Button.builder(
                        Component.translatable("container." + YDM.MOD_ID + ".duel.down_arrow"),
                        this::chatScrollButtonClicked
                )
                .pos(x, y)
                .size(w, buttonHeight)
                .build();
        addRenderableWidget(chatDownButton);

        y += offset;

        // 输入框
        textFieldWidget = new EditBox(
                font,
                x + 1,
                y + 1,
                w - 2,
                buttonHeight - 2,
                Component.empty()
        );
        textFieldWidget.setMaxLength(64);
        addRenderableWidget(textFieldWidget);

        // 其它初始化
        appendToInitChat(screenWidth, screenHeight, extraOff, y, w, halfW, chatWidth, chatHeight, margin);

        // 默认切换聊天模式
        duelChat = !duelChat;
        switchChat();

        // 禁用翻页按钮初始状态
        chatUpButton.active = false;
        chatDownButton.active = false;

        makeChatVisible();
    }
    protected void appendToInitChat(int width, int height, int x, int y, int w, int h, int chatWidth, int chatHeight, int margin)
    {

    }

    protected void changeChatFlags(boolean flag)
    {
        chatUpButton.visible = flag;
        chatDownButton.visible = flag;
        chatWidget.visible = flag;
        textFieldWidget.visible = flag;
        duelChatButton.visible = flag;
        worldChatButton.visible = flag;
    }

    public void makeChatVisible()
    {
        changeChatFlags(true);
    }

    public void makeChatInvisible()
    {
        changeChatFlags(false);
    }

    protected void sendChat()
    {
        String text = textFieldWidget.getValue().trim();

        if(!text.isEmpty())
        {
            if(duelChat)
            {
                YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.SendMessageToServer(getHeader(), Component.literal(text)));
            }
            else
            {
                minecraft.player.connection.sendChat(text);
            }
        }

        textFieldWidget.setValue("");
    }

    protected void switchChat()
    {
        if(chatWidget.visible)
        {
            Button toEnable;
            Button toDisable;

            if(duelChat)
            {
                toEnable = duelChatButton;
                toDisable = worldChatButton;
                chatWidget.setTextSupplier(getLevelMessagesSupplier());
            }
            else
            {
                toEnable = worldChatButton;
                toDisable = duelChatButton;
                chatWidget.setTextSupplier(getDuelMessagesSupplier());
            }

            toEnable.active = true;
            toDisable.active = false;
            duelChat = !duelChat;
        }
    }

    protected Supplier<List<Component>> getDuelMessagesSupplier()
    {
        return () -> //TODO
        {
            List<Component> list = new ArrayList<>(getDuelManager().getMessages().size());

            for(DuelChatMessage msg : getDuelManager().getMessages())
            {
                list.add(msg.generateStyledMessage(getPlayerRole(), ChatFormatting.BLUE, ChatFormatting.RED, ChatFormatting.WHITE));
            }

            return list;
        };
    }

    protected Supplier<List<Component>> getLevelMessagesSupplier()
    {
        return () -> ClientProxy.chatMessages;
    }

    protected void chatScrollButtonClicked(Button button)
    {
        //TODO
    }

    protected void chatScrollButtonHovered(Button  button, GuiGraphics gui, int mouseX, int mouseY)
    {
        //TODO
        renderDisabledTooltip(gui, (Component) null, mouseX, mouseY);
    }

    public void populateDeckSources(List<DeckSource> deckSources)
    {
    }

    public void receiveDeck(int index, DeckHolder deck)
    {
    }

    public void deckAccepted(PlayerRole role)
    {
    }

    public void handleAction(Action action)
    {
        action.initClient(getDuelManager().getPlayField());
        action.doAction();
    }

    public DuelManager getDuelManager()
    {
        return menu.getDuelManager();
    }

    public PlayField getPlayField()
    {
        return getDuelManager().getPlayField();
    }

    public DuelMessageHeader getHeader()
    {
        return getDuelManager().headerFactory.get();
    }

    public DuelState getState()
    {
        return getDuelManager().getDuelState();
    }

    public PlayerRole getPlayerRole()
    {
        return getDuelManager().getRoleFor(ClientProxy.getPlayer());
    }

    public ZoneOwner getZoneOwner()
    {
        PlayerRole role = getPlayerRole();

        if(ZoneOwner.PLAYER1.player == role)
        {
            return ZoneOwner.PLAYER1;
        }
        else if(ZoneOwner.PLAYER2.player == role)
        {
            return ZoneOwner.PLAYER2;
        }
        else
        {
            return ZoneOwner.NONE;
        }
    }

    public interface DuelScreenConstructor<E extends DuelContainer>
    {
        DuelContainerScreen<E> construct(E container, Inventory inv, Component title);
    }
}
