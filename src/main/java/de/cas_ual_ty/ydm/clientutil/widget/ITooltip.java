package de.cas_ual_ty.ydm.clientutil.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import org.jline.reader.Widget;

public interface ITooltip
{
    void onTooltip(AbstractWidget widget, GuiGraphics gui, int mouseX, int mouseY);
}