package de.cas_ual_ty.ydm.duel.screen.widget;

import de.cas_ual_ty.ydm.clientutil.widget.ITooltip;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import de.cas_ual_ty.ydm.duel.screen.IDuelScreenContext;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Consumer;

public class NonSecretStackZoneWidget extends StackZoneWidget
{
    public NonSecretStackZoneWidget(Zone zone, IDuelScreenContext context, Consumer<ZoneWidget> onPress, ITooltip onTooltip)
    {
        super(zone, context, onPress, onTooltip);
    }
    
    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks)
    {
        super.renderWidget(gui, mouseX, mouseY, partialTicks);
        hoverCard = null; // dont select top card when clicking on it, ever
    }
}
