package de.cas_ual_ty.ydm.carditeminventory;

import com.mojang.blaze3d.systems.RenderSystem;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.widget.ImprovedButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.PacketDistributor;

public class CIIScreen<T extends CIIContainer> extends AbstractContainerScreen<T> {
    private static final ResourceLocation CHEST_GUI_TEXTURE =
            new ResourceLocation("textures/gui/container/generic_54.png");

    private final int inventoryRows;

    protected Button prevButton;
    protected Button nextButton;

    public CIIScreen(T container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
//        this.passEvents = false;
        this.inventoryRows = 6;
        this.imageHeight = 114 + inventoryRows * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }



    @Override
    protected void init() {
        super.init();
        int right = leftPos + imageWidth;
        addRenderableWidget(prevButton = new ImprovedButton(
                right - 24 - 8, topPos + 4,
                12, 12,
                Component.translatable("generic.ydm.left_arrow"),
                this::onButtonClicked
        ));
        addRenderableWidget(nextButton = new ImprovedButton(
                right - 12 - 8, topPos + 4,
                12, 12,
                Component.translatable("generic.ydm.right_arrow"),
                this::onButtonClicked
        ));
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        // background & slots
        renderBackground(gui);
        super.render(gui, mouseX, mouseY, partialTicks);
        // tooltips
        renderTooltip(gui, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        MutableComponent title = Component.literal(this.title.getString())
                .append(" ")
                .append(Component.literal((menu.getPage() + 1) + "/" + menu.getMaxPage()));
        // drawTitle
        gui.drawString(font, title, 8, 6, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTicks, int mouseX, int mouseY) {
        ScreenUtil.white();
        RenderSystem.setShaderTexture(0, CHEST_GUI_TEXTURE);
        int i = (width - imageWidth) / 2;
        int j = (height - imageHeight) / 2;
        // top (container)
        gui.blit(CHEST_GUI_TEXTURE,
                i, j,
                0, 0,
                imageWidth, inventoryRows * 18 + 17);
        // bottom (player inv)
        gui.blit(CHEST_GUI_TEXTURE,
                i, j + inventoryRows * 18 + 17,
                0, 126,
                imageWidth, 96);
    }

    protected void onButtonClicked(Button button) {
        if (button == prevButton) {
            YDM.channel.send(PacketDistributor.SERVER.noArg(),
                    new CIIMessages.ChangePage(false));
        } else if (button == nextButton) {
            YDM.channel.send(PacketDistributor.SERVER.noArg(),
                    new CIIMessages.ChangePage(true));
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // allow number keys to pass through
        if (keyCode >= 49 && keyCode <= 57) {
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
