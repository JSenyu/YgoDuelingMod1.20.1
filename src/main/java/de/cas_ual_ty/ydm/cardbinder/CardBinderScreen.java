package de.cas_ual_ty.ydm.cardbinder;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.cardinventory.CardInventory;
import de.cas_ual_ty.ydm.clientutil.CardRenderUtil;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.widget.ImprovedButton;
import de.cas_ual_ty.ydm.clientutil.widget.TextureButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CardBinderScreen extends AbstractContainerScreen<CardBinderContainer> {
    private static final ResourceLocation CARD_BINDER_GUI_TEXTURE =
            new ResourceLocation(YDM.MOD_ID, "textures/gui/card_binder.png");

    private static final int LEFT_SHIFT = 340;
    private static final int Q = 81;

    protected CardButton[] cardButtons;
    protected Button reloadButton;
    protected Button prevButton;
    protected Button nextButton;
    protected EditBox cardSearch;

    public CardBinderScreen(CardBinderContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void init() {
        imageWidth = 176;
        imageHeight = 114 + CardInventory.DEFAULT_PAGE_ROWS * 18;
        super.init();
        imageWidth += 27;

        cardButtons = new CardButton[CardInventory.DEFAULT_CARDS_PER_PAGE];
        int index;
        for (int y = 0; y < CardInventory.DEFAULT_PAGE_ROWS; y++) {
            for (int x = 0; x < CardInventory.DEFAULT_PAGE_COLUMNS; x++) {
                index = x + y * 9;
                CardButton button = new CardButton(
                        leftPos + 7 + x * 18,
                        topPos + 17 + y * 18,
                        18, 18,
                        index,
                        this::onCardClicked,
                        this::getCard
                );
                cardButtons[index] = button;
                addRenderableWidget(button);
            }
        }

        int right = leftPos + imageWidth - 27;
        addRenderableWidget(prevButton = new ImprovedButton(
                right - 24 - 8, topPos + 4, 12, 12,
                Component.translatable("generic.ydm.left_arrow"),
                this::onButtonClicked
        ));
        addRenderableWidget(nextButton = new ImprovedButton(
                right - 12 - 8, topPos + 4, 12, 12,
                Component.translatable("generic.ydm.right_arrow"),
                this::onButtonClicked
        ));
        addRenderableWidget(reloadButton = new TextureButton(
                right - 12 - 8, topPos + imageHeight - 96, 12, 12,
                Component.empty(),
                this::onButtonClicked
        ).setTexture(
                new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png"),
                64, 0, 16, 16
        ));

        cardSearch = new EditBox(
                font,
                right - 82, topPos + imageHeight - 96,
                80, 12,
                Component.empty()
        );
        addRenderableWidget(cardSearch);
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        renderBackground(gui);
        super.render(gui, mouseX, mouseY, partialTicks);
        renderTooltip(gui, mouseX, mouseY);

        for (CardButton button : cardButtons) {
            if (button.isHoveredOrFocused() && button.getCard() != null) {
                CardRenderUtil.renderCardInfo(gui, button.getCard(), this);

                List<Component> infoLines = new LinkedList<>();
                button.getCard().addInformation(infoLines);

                gui.renderComponentTooltip(this.font, infoLines, mouseX, mouseY);
                break;
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        // 标题行
        MutableComponent title = Component.literal(this.title.getString());
        if (!getMenu().loaded) {
            title = title.append(" ")
                    .append(Component.translatable("container.ydm.card_binder.loading"));
        } else {
            title = title.append(" ")
                    .append(Component.literal(menu.page + "/" + menu.clientMaxPage));
        }
        // 使用 GuiGraphics.drawString 来绘制文本
        gui.drawString(font, title, 8, 6, 0x404040, false);

        // 玩家背包标题
        gui.drawString(font,
                playerInventoryTitle.getVisualOrderText(),
                8,
                imageHeight - 96 + 2,
                0x404040,
                false);
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTicks, int mouseX, int mouseY) {
        // 重置渲染颜色
        ScreenUtil.white();

        // 绑定你的 GUI 贴图，并使用 GuiGraphics 进行 blit
//        RenderSystem.setShaderTexture(0, CARD_BINDER_GUI_TEXTURE);
        gui.blit(
                CARD_BINDER_GUI_TEXTURE,  // 贴图资源
                leftPos, topPos,          // 渲染起点（屏幕坐标）
                0, 0,                     // 贴图内起始 UV 坐标
                imageWidth, imageHeight   // 绘制尺寸
        );
    }



    protected void onButtonClicked(Button button) {
        if (!getMenu().loaded) return;
        if (button == prevButton) {
            YDM.channel.send(PacketDistributor.SERVER.noArg(),
                    new CardBinderMessages.ChangePage(false));
        } else if (button == nextButton) {
            YDM.channel.send(PacketDistributor.SERVER.noArg(),
                    new CardBinderMessages.ChangePage(true));
        } else if (button == reloadButton) {
            YDM.channel.send(PacketDistributor.SERVER.noArg(),
                    new CardBinderMessages.ChangeSearch(cardSearch.getValue()));
        }
    }

    protected void onCardClicked(CardButton button, int index) {
        if (!getMenu().loaded || button.getCard() == null) return;
        YDM.channel.send(PacketDistributor.SERVER.noArg(),
                new CardBinderMessages.IndexClicked(index));
    }

    protected CardHolder getCard(int index) {
        return index < getMenu().clientList.size()
                ? getMenu().clientList.get(index)
                : null;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (cardSearch != null && cardSearch.isFocused()) {
            return cardSearch.keyPressed(keyCode, scanCode, modifiers);
        }
        if (getMenu().loaded && keyCode == Q) {
            for (CardButton button : cardButtons) {
                if (button.isHoveredOrFocused() && button.getCard() != null) {
                    YDM.channel.send(PacketDistributor.SERVER.noArg(),
                            new CardBinderMessages.IndexDropped(button.index));
                    break;
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
