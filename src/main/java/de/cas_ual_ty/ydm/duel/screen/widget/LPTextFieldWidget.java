package de.cas_ual_ty.ydm.duel.screen.widget;

import de.cas_ual_ty.ydm.clientutil.widget.ITooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;


public class LPTextFieldWidget extends EditBox
{
    // doing it exactly in the super class
    private boolean enableBackgroundDrawing = true;
    public ITooltip tooltip;
    
    public LPTextFieldWidget(Font fontrenderer, int x, int y, int width, int height, ITooltip tooltip)
    {
        super(fontrenderer, x, y, width, height, Component.empty());
        this.tooltip = tooltip;
        setMaxLength(6);
        
        setFilter((text) ->
        {
            if(text.isEmpty())
            {
                return true;
            }
            else
            {
                String pre = text.substring(0, 1);
                
                if(!pre.equals("+") && !pre.equals("-"))
                {
                    return false;
                }
                
                if(text.length() == 1)
                {
                    return true;
                }
                else
                {
                    return text.substring(1).matches("\\d+");
                }
            }
        });
    }
    
    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks)
    {
        int xOld = this.getX();
        int yOld = this.getY();
        int widthOld = this.getWidth();
        int heightOld = this.getHeight();

        this.setX((this.getX() * 2) + 1);
        this.setY((this.getY() * 2) + 1);

        this.setWidth((this.getWidth() * 2) - 2);
        this.setHeight((this.getHeight() * 2) - 2);


        gui.pose().pushPose();
        gui.pose().scale(0.5F, 0.5F, 1);
        
        super.renderWidget(gui, mouseX * 2, mouseY * 2, partialTicks);

        gui.pose().popPose();

        this.setX(xOld);
        this.setY(yOld);
        this.setWidth(widthOld);
        this.setHeight(heightOld);
        
        if(isMouseOver(mouseX, mouseY)) {
            tooltip.onTooltip(this, gui, mouseX, mouseY);
        }
    }
    
    @Override
    public int getInnerWidth()
    {
        return 2 * (enableBackgroundDrawing ? width - 8 : width);
    }
    
    // the getter is private,
    // so we gotta catch the value here
    @Override
    public void setBordered(boolean enableBackgroundDrawingIn)
    {
        enableBackgroundDrawing = enableBackgroundDrawingIn;
        super.setBordered(enableBackgroundDrawingIn);
    }
}
