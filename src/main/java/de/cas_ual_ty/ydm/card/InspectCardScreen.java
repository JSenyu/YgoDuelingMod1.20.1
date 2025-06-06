package de.cas_ual_ty.ydm.card;

import com.mojang.blaze3d.platform.InputConstants;
import de.cas_ual_ty.ydm.clientutil.CardRenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class InspectCardScreen extends Screen
{
    public final CardHolder cardHolder;
    
    public InspectCardScreen(Component pTitle, CardHolder cardHolder)
    {
        super(pTitle);
        this.cardHolder = cardHolder;
    }
    
    public InspectCardScreen(CardHolder cardHolder)
    {
        this(Component.empty(), cardHolder);
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if(minecraft != null)
        {
            renderBackground(pGuiGraphics);
        }

        CardRenderUtil.renderInfoCardWithRarity(pGuiGraphics, pMouseX, pMouseY, width / 2, height / 2, 128, 128, cardHolder);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers)
    {
        InputConstants.Key mouseKey = InputConstants.getKey(pKeyCode, pScanCode);

        if (minecraft != null && minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) {
            onClose();
            return true;
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
    
    @Override
    public void handleDelayedNarration()
    {
    }
    
    @Override
    public void triggerImmediateNarration(boolean p_169408_)
    {
    }
}
