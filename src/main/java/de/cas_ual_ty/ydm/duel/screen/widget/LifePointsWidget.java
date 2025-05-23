package de.cas_ual_ty.ydm.duel.screen.widget;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.widget.ITooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.function.Supplier;

public class LifePointsWidget extends AbstractWidget implements IWidgetToolTip{
    public static final ResourceLocation DUEL_WIDGETS = new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png");
    
    public Supplier<Integer> lpGetter;
    public int maxLP;
    public ITooltip tooltip;
    
    public LifePointsWidget(int x, int y, int width, int height, Supplier<Integer> lpGetter, int maxLP, ITooltip tooltip)
    {
        super(x, y, width, height, Component.empty());
        this.lpGetter = lpGetter;
        this.maxLP = maxLP;
        this.tooltip = tooltip;
    }
    
    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontrenderer = minecraft.font;
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.enableDepthTest();
//        RenderSystem.setShaderColor(1F, 1F, 1F, alpha);
        
        int lp = lpGetter.get();
        float relativeLP = Math.min(1F, lp / (float) maxLP);
        
//        final int margin = 1;
        int x = this.getX();
        int y = this.getY();
//        int w = width;
//        int h = height;

        gui.blit(LifePointsWidget.DUEL_WIDGETS, x, y, 0, 8, width, height);
        gui.blit(LifePointsWidget.DUEL_WIDGETS, x, y, 0, 0, Mth.ceil(width * relativeLP), height);
        gui.blit(LifePointsWidget.DUEL_WIDGETS, x, y, 0, 16, width, height);
//        this.renderBg(gui, minecraft, mouseX, mouseY);
//        super.renderWidget(gui, mouseX, mouseY, partialTicks);
        
        x = this.getX() + width / 2;
        y = this.getY() + height / 2;

        gui.pose().pushPose();

        gui.pose().scale(0.5F, 0.5F, 1F);
        
        int j = getFGColor();
        gui.drawCenteredString(fontrenderer, Component.literal(String.valueOf(lp)), x * 2, y * 2 - fontrenderer.lineHeight / 2, j | Mth.ceil(alpha * 255.0F) << 24);

        gui.pose().popPose();
        
        if(isHoveredOrFocused() && this.isMouseOver(mouseX, mouseY)) {
            this.renderToolTip(gui, mouseX, mouseY);
        }
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput builder) {
        // no extra narration
    }

    @Override
    public ITooltip getToolTip() {
        return this.tooltip;
    }
}
