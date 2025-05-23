package de.cas_ual_ty.ydm.duel.screen.widget;

import de.cas_ual_ty.ydm.clientutil.widget.ITooltip;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;

public interface IWidgetToolTip {

    ITooltip getToolTip();


    default void renderToolTip(GuiGraphics gui, int pMouseX, int pMouseY) {
        this.getToolTip().onTooltip((AbstractWidget) this, gui, pMouseX, pMouseY);
    }


    default int getYImage(AbstractWidget w, boolean pIsHovered) {
        int i = 1;
        if (!w.active) {
            i = 0;
        } else if (pIsHovered) {
            i = 2;
        }

        return i;
    }
}
