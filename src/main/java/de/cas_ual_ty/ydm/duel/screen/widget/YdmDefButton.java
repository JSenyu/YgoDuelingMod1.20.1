package de.cas_ual_ty.ydm.duel.screen.widget;

import de.cas_ual_ty.ydm.clientutil.widget.ITooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class YdmDefButton extends Button implements IWidgetToolTip {
    protected final ITooltip tooltip;

    public YdmDefButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, ITooltip tooltip) {
        super(Button.builder(pMessage, pOnPress::onPress).pos(pX, pY).size(pWidth, pHeight));
        this.tooltip = tooltip;
    }

    @Override
    public ITooltip getToolTip() {
        return this.tooltip;
    }
}
