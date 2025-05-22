package de.cas_ual_ty.ydm.cardsupply;

import com.mojang.blaze3d.systems.RenderSystem;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.cardbinder.CardButton;
import de.cas_ual_ty.ydm.clientutil.CardRenderUtil;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.widget.ImprovedButton;
import de.cas_ual_ty.ydm.rarity.Rarities;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CardSupplyScreen extends AbstractContainerScreen<CardSupplyContainer> {
    private static final ResourceLocation CARD_SUPPLY_GUI_TEXTURE =
            new ResourceLocation(YDM.MOD_ID, "textures/gui/card_supply.png");

    public static final int ROWS = 6;
    public static final int COLUMNS = 9;
    public static final int PAGE = ROWS * COLUMNS;

    private List<CardHolder> cardsList;
    private EditBox textField;
    private Button prevButton;
    private Button nextButton;
    private int page;
    private CardButton[] cardButtons;

    public CardSupplyScreen(CardSupplyContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 114 + ROWS * 18;
        this.cardsList = new ArrayList<>(YdmDatabase.getTotalCardsAndVariants());
    }

    @Override
    protected void init() {
        super.init();
        int right = leftPos + imageWidth;
        // search box
        textField = new EditBox(font, right - 80 - 8 - 1, topPos + 6 - 1, 80 + 2, font.lineHeight + 2, Component.empty());
        addRenderableWidget(textField);
        // card buttons
        cardButtons = new CardButton[PAGE];
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                int idx = x + y * COLUMNS;
                CardButton btn = new CardButton(
                        leftPos + 7 + x * 18,
                        topPos + 17 + y * 18,
                        18, 18,
                        idx,
                        this::onCardClicked,
                        this::getCard
                );
                cardButtons[idx] = btn;
                addRenderableWidget(btn);
            }
        }
        // navigation buttons
        prevButton = new ImprovedButton(
                leftPos + imageWidth - 80 - 8,
                topPos + imageHeight - 96,
                40, 12,
                Component.translatable("generic.ydm.left_arrow"),
                this::onButtonClicked
        );
        nextButton = new ImprovedButton(
                leftPos + imageWidth - 40 - 8,
                topPos + imageHeight - 96,
                40, 12,
                Component.translatable("generic.ydm.right_arrow"),
                this::onButtonClicked
        );
        addRenderableWidget(prevButton);
        addRenderableWidget(nextButton);
        applyName();
        updateCards();
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        renderBackground(gui);
        super.render(gui, mouseX, mouseY, partialTicks);
        renderTooltip(gui, mouseX, mouseY);
        // custom tooltips
        for (CardButton btn : cardButtons) {
            if (btn.isHoveredOrFocused() && btn.getCard() != null) {
                CardRenderUtil.renderCardInfo(gui, btn.getCard(), this);
                List<Component> info = new LinkedList<>();
                btn.getCard().addInformation(info);
                gui.renderComponentTooltip(font, info, mouseX, mouseY);
                break;
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTicks, int mouseX, int mouseY) {
        ScreenUtil.white();
        RenderSystem.setShaderTexture(0, CARD_SUPPLY_GUI_TEXTURE);
        gui.blit(
                CARD_SUPPLY_GUI_TEXTURE,
                leftPos, topPos,
                0, 0,
                imageWidth, imageHeight
        );
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, title, 8, 6, 0x404040, false);
        gui.drawString(
                font,
                playerInventoryTitle.getVisualOrderText(),
                8,
                imageHeight - 96 + 2,
                0x404040,
                false
        );
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (textField != null && textField.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                applyName();
                return true;
            } else {
                return textField.keyPressed(keyCode, scanCode, modifiers);
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void onButtonClicked(Button b) {
        int min = 0;
        int max = cardsList.size() / PAGE;
        if (b == prevButton) {
            page = (page - 1 < min) ? max : page - 1;
        } else if (b == nextButton) {
            page = (page + 1 > max) ? min : page + 1;
        }
    }

    private CardHolder getCard(int idx0) {
        int idx = page * PAGE + idx0;
        return idx < cardsList.size() ? cardsList.get(idx) : null;
    }

    public void applyName() {
        String name = textField.getValue().toLowerCase();
        cardsList.clear();
        page = 0;
        YdmDatabase.forAllCardVariants((card, img) -> {
            if (card.getName().toLowerCase().contains(name)) {
                cardsList.add(new CardHolder(card, img, Rarities.SUPPLY.name));
            }
        });
    }

    public void updateCards() {
        page = 0;
        cardsList.clear();
        YdmDatabase.forAllCardVariants((card, img) ->
                cardsList.add(new CardHolder(card, img, Rarities.SUPPLY.name))
        );
    }

    private void onCardClicked(CardButton btn, int idx) {
        CardHolder ch = btn.getCard();
        if (ch != null && ch.getCard() != null) {
            YDM.channel.send(PacketDistributor.SERVER.noArg(),
                    new CardSupplyMessages.RequestCard(ch.getCard(), ch.getImageIndex())
            );
        }
    }
}