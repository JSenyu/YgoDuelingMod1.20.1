package de.cas_ual_ty.ydm.duel.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import de.cas_ual_ty.ydm.duel.DuelContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class DuelScreenBase<E extends DuelContainer> extends DuelContainerScreen<E> {
    public DuelScreenBase(E screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    /**
     * 新版：覆盖 renderLabels(GuiGraphics, int, int) 而非 PoseStack。
     */
    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        // drawString(Font font, Component text, int x, int y, int color)
        gui.drawString(this.font,
                Component.literal("Waiting for server..."),
                8,                                     // 左侧偏移
                6,                                     // 顶部偏移
                0x404040                              // 文本颜色
        );
    }
}
