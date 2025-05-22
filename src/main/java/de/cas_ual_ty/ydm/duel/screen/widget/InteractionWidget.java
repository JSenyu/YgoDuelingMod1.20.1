package de.cas_ual_ty.ydm.duel.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.YdmBlitUtil;
import de.cas_ual_ty.ydm.clientutil.widget.ITooltip;
import de.cas_ual_ty.ydm.duel.action.ActionIcon;
import de.cas_ual_ty.ydm.duel.playfield.ZoneInteraction;
import de.cas_ual_ty.ydm.duel.screen.IDuelScreenContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class InteractionWidget extends Button implements IWidgetToolTip{
    private final ZoneInteraction interaction;
    private final IDuelScreenContext context;
    private final ITooltip tooltip;

    public InteractionWidget(ZoneInteraction interaction,
                             IDuelScreenContext context,
                             int x, int y, int width, int height,
                             Component title,
                             Consumer<InteractionWidget> onPress,
                             ITooltip tooltip) {
        super(Button.builder(title, b -> onPress.accept((InteractionWidget)b))
                .pos(x, y)
                .size(width, height));
        this.tooltip = tooltip;
        this.interaction = interaction;
        this.context = context;
    }

    public InteractionWidget(ZoneInteraction interaction,
                             IDuelScreenContext context,
                             int x, int y, int width, int height,
                             Consumer<InteractionWidget> onPress,
                             ITooltip tooltip) {
        this(interaction, context,
                x, y, width, height,
                interaction.icon.getLocal(),
                onPress,
                tooltip);
    }

    @Override
    protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        // 提升 Z 深度
        gui.pose().pushPose();
        gui.pose().translate(0, 0, 5);

        ActionIcon icon = interaction.icon;
        int iconW = icon.iconWidth;
        int iconH = icon.iconHeight;

        // 等比缩放至按钮区域内
        if (iconH > this.height) {
            iconW = this.height * iconW / iconH;
            iconH = this.height;
        }
        if (iconW > this.width) {
            iconH = this.width * iconH / iconW;
            iconW = this.width;
        }

//        ScreenUtil.white();
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.enableDepthTest();

        // 绘制图标
//        RenderSystem.setShaderTexture(0, icon.sourceFile);
        YdmBlitUtil.blit(
                gui,
                icon.sourceFile,
                // 在屏幕上渲染的坐标
                (int) (this.getX() + (this.width - iconW) / 2f),
                (int) (this.getY() + (this.height - iconH) / 2f),

                // 在屏幕上渲染的大小
                iconW, iconH,

                // 图片uv坐标
                icon.iconX, icon.iconY,
                icon.iconWidth, icon.iconHeight,

                // 图片大小
                icon.fileSize, icon.fileSize
        );


        if(isHoveredOrFocused() && active)
        {
            ScreenUtil.renderHoverRect(gui, this.getX(), this.getY(), this.getWidth(), this.getHeight());

            if(this.isMouseOver(mouseX, mouseY)){
                this.renderToolTip(gui, mouseX, mouseY);
            }
        }

        gui.pose().popPose();

        // 悬停高亮（框）及提示由 Button 自身处理，无需手动调用
//        super.renderWidget(gui, mouseX, mouseY, partialTicks);
    }

    public ZoneInteraction getInteraction() {
        return interaction;
    }
    public IDuelScreenContext getContext() {
        return context;
    }

    @Override
    public ITooltip getToolTip() {
        return this.tooltip;
    }
}
