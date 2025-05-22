package de.cas_ual_ty.ydm.clientutil.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.duel.screen.widget.IWidgetToolTip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Supplier;

public class ReadyCheckboxWidget extends Button implements IWidgetToolTip {
    private final Supplier<Boolean> isChecked;
    private final Supplier<Boolean> isActiveSupplier;

    public ReadyCheckboxWidget(int x, int y, int width, int height,
                               Supplier<Boolean> isChecked,
                               Supplier<Boolean> isActive,
                               OnPress onPress) {
        super(Button.builder(Component.empty(), onPress)
                .pos(x, y)
                .size(width, height));
        this.isChecked = isChecked;
        this.isActiveSupplier = isActive;
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        // Update active state dynamically
        this.active = this.isActiveSupplier.get();
        super.render(gui, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        ScreenUtil.white();
        int i = getYImage(this, isHoveredOrFocused());
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.enableDepthTest();
        int x = this.getX();
        int y = this.getY();
        gui.blit(WIDGETS_LOCATION, x, y, 0, 46 + i * 20, width / 2, height);
        gui.blit(WIDGETS_LOCATION, x + width / 2, y, 200 - width / 2, 46 + i * 20, width / 2, height);

        // If checked, draw a check mark
        if (this.isChecked.get()) {
            int color = this.getFGColor() | (int)(this.alpha * 255f) << 24;
            gui.drawCenteredString(minecraft.font, Component.literal("✔"),
                    x + this.getWidth() / 2,
                    y + (this.getHeight() - 8) / 2,
                    color);
        }
    }

    @Override
    public ITooltip getToolTip() {
        return null;
    }
}
