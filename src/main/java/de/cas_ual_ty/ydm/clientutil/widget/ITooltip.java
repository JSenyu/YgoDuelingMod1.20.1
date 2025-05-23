package de.cas_ual_ty.ydm.clientutil.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;

public interface ITooltip
{
    void onTooltip(AbstractWidget widget, GuiGraphics gui, int mouseX, int mouseY);
}