package de.cas_ual_ty.ydm.duel.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import de.cas_ual_ty.ydm.duel.screen.animation.Animation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.LinkedList;
import java.util.Queue;

public class AnimationsWidget extends AbstractWidget
{
    public Queue<Animation> animations;
    
    public AnimationsWidget(int x, int y, int width, int height)
    {
        super(x, y, width, height, Component.empty());
        animations = new LinkedList<>();
    }
    
    public void addAnimation(Animation animation)
    {
        animations.add(animation);
    }
    
    public void forceFinish()
    {
        Animation a;
        while(!animations.isEmpty())
        {
            a = animations.poll();
            
            while(!a.ended())
            {
                a.tick();
            }
        }
    }
    
    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks)
    {
        RenderSystem.setShaderColor(1F, 1F, 1F, alpha);
        
        if(visible)
        {
            for(Animation a : animations)
            {
                a.render(gui, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {

    }

    public void tick()
    {
        if(!animations.isEmpty())
        {
            Animation a = animations.element();
            
            a.tick();
            
            if(a.ended())
            {
                animations.poll();
            }
        }
    }
    
    public void onInit()
    {
        Animation a;
        
        while(!animations.isEmpty())
        {
            a = animations.poll();
            
            while(!a.ended())
            {
                a.tick();
            }
        }
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput builder) {
        // no extra narration
    }
}
