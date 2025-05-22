package de.cas_ual_ty.ydm.cardbinder;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.clientutil.CardRenderUtil;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.YdmBlitUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class CardButton extends AbstractButton {
    public final int index;
    private final Function<Integer, CardHolder> cardHolder;
    private final BiConsumer<CardButton, Integer> onPress;

    public CardButton(int x, int y, int width, int height, int index,
                      BiConsumer<CardButton, Integer> onPress,
                      Function<Integer, CardHolder> cardHolder) {
        super(x, y, width, height, Component.empty());
        this.index = index;
        this.cardHolder = cardHolder;
        this.onPress = onPress;
    }

    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        CardHolder card = getCard();
        if (card != null) {
            // render the card icon
            ScreenUtil.white();
//            CardRenderUtil.bindMainResourceLocation(card);
            YdmBlitUtil.fullBlit(gui, card.getMainImageResourceLocation(),getX() + 1, getY() + 1, 16, 16);

            // hover overlay
            if (isHoveredOrFocused()) {
                drawHover(gui);
            }
        }
    }

    private void drawHover(GuiGraphics gui) {
        // disable depth so overlay draws atop
        RenderSystem.disableDepthTest();
        int x0 = this.getX() + 1;
        int y0 = this.getY() + 1;
        // draw a semi‑transparent gradient
        gui.fillGradient(x0, y0, x0 + 16, y0 + 16,
                /* startColor */ 0x80_000000,
                /* endColor   */ 0x80_000000);
        RenderSystem.enableDepthTest();
    }

    @Override
    public void onPress() {
        onPress.accept(this, index);
    }

    public CardHolder getCard() {
        return cardHolder.apply(index);
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput builder) {
        // no extra narration
    }
}
