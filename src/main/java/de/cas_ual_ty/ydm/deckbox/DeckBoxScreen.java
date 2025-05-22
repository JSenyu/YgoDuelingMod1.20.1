package de.cas_ual_ty.ydm.deckbox;

import com.mojang.blaze3d.systems.RenderSystem;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.YdmBlitUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class DeckBoxScreen extends AbstractContainerScreen<DeckBoxContainer> {
    public static final ResourceLocation DECK_BOX_GUI_TEXTURE =
            new ResourceLocation(YDM.MOD_ID, "textures/gui/deck_box.png");

    public DeckBoxScreen(DeckBoxContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void init() {
        this.imageWidth = 284;
        this.imageHeight = 250;
        super.init();
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        renderBackground(gui);
        super.render(gui, mouseX, mouseY, partialTicks);
        renderTooltip(gui, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        int amount;
        // 主卡组统计
        amount = 0;
        for (int i = DeckHolder.MAIN_DECK_INDEX_START; i < DeckHolder.MAIN_DECK_INDEX_END; i++) {
            Slot s = menu.getSlot(i);
            if (s.hasItem()) amount++;
        }
        gui.drawString(
                font,
                Component.translatable("container.ydm.deck_box.main")
                        .append(" " + amount + "/" + DeckHolder.MAIN_DECK_SIZE),
                8, 6, 0x404040, false
        );
        // 额外卡组统计
        amount = 0;
        for (int i = DeckHolder.EXTRA_DECK_INDEX_START; i < DeckHolder.EXTRA_DECK_INDEX_END; i++) {
            Slot s = menu.getSlot(i);
            if (s.hasItem()) amount++;
        }
        gui.drawString(
                font,
                Component.translatable("container.ydm.deck_box.extra")
                        .append(" " + amount + "/" + DeckHolder.EXTRA_DECK_SIZE),
                8, 92, 0x404040, false
        );
        // 备用卡组统计
        amount = 0;
        for (int i = DeckHolder.SIDE_DECK_INDEX_START; i < DeckHolder.SIDE_DECK_INDEX_END; i++) {
            Slot s = menu.getSlot(i);
            if (s.hasItem()) amount++;
        }
        gui.drawString(
                font,
                Component.translatable("container.ydm.deck_box.side")
                        .append(" " + amount + "/" + DeckHolder.SIDE_DECK_SIZE),
                8, 124, 0x404040, false
        );
        // 袖珍统计
        gui.drawString(font,
                Component.translatable("container.ydm.deck_box.sleeves"),
                224, imageHeight - 96 + 2,
                0x404040, false
        );
        // 玩家背包标题
        gui.drawString(font,
                playerInventoryTitle.getVisualOrderText(),
                8, imageHeight - 96 + 2,
                0x404040, false
        );
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTicks, int mouseX, int mouseY) {
        ScreenUtil.white();
//        RenderSystem.setShaderTexture(0, DECK_BOX_GUI_TEXTURE);
        gui.blit(
                DECK_BOX_GUI_TEXTURE,
                leftPos, topPos,
                0, 0,
                imageWidth, imageHeight,
                512, 256
        );
    }
}
