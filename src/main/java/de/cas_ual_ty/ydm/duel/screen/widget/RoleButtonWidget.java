package de.cas_ual_ty.ydm.duel.screen.widget;

import de.cas_ual_ty.ydm.duel.PlayerRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class RoleButtonWidget extends Button {
    private final Supplier<Boolean> available;
    public final PlayerRole role;

    public RoleButtonWidget(int x, int y, int width, int height,
                            Component text,
                            OnPress onPress,
                            Supplier<Boolean> available,
                            PlayerRole role) {
        super(Button.builder(text, onPress)
                .pos(x, y)
                .size(width, height));
        this.available = available;
        this.role = role;
        // 初始时就根据 available 决定是否可点击
        this.active = available.get();
    }

    @Override
    protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        // 每帧根据 available 更新 active 状态
        this.active = available.get();
        super.renderWidget(gui, mouseX, mouseY, partialTicks);
    }
}
