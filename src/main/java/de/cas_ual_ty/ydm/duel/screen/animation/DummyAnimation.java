package de.cas_ual_ty.ydm.duel.screen.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;

public class DummyAnimation extends Animation
{
    public DummyAnimation()
    {
        super(1);
    }
    
    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks)
    {
    }
}
