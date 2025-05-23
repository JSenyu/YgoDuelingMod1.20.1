package de.cas_ual_ty.ydm.clientutil.widget;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.CardItem;
import de.cas_ual_ty.ydm.clientutil.YdmBlitUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ItemStackWidget extends AbstractWidget {
    public ItemStack itemStack;
    public ItemRenderer itemRenderer;
    public ResourceLocation replacement;

    public ItemStackWidget(int x, int y, int size, ItemRenderer itemRenderer, ResourceLocation replacement) {
        super(x, y, size, size, Component.empty());
        this.itemStack = ItemStack.EMPTY;
        this.itemRenderer = itemRenderer;
        this.replacement = replacement;
    }

    public ItemStackWidget setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    @Override
    protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        ResourceLocation rl = replacement;
        int x = this.getX();
        int y = this.getY();

        if (!itemStack.isEmpty()) {
            if (itemStack.getItem() == YdmItems.CARD.get()) {
                CardHolder c = CardItem.getCardHolder(itemStack);

                if (c.getCard() != null) {
                    rl = c.getMainImageResourceLocation();
                }
            } else {
                // Custom item rendering（非卡牌物品）

                BakedModel bakedmodel = itemRenderer.getModel(itemStack, null, null, 0); // FIXME: context参数可能需根据需求调整
                minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);

                RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                // ✅ 使用 gui.pose() 替代 ModelViewStack + blitOffset
                gui.pose().pushPose();
                gui.pose().translate(x, y, 100.0F); // 这里的 100F 相当于原来的 blitOffset 效果
                gui.pose().translate(width / 2F, height / 2F, 0.0F);
                gui.pose().scale(width, -height, 16.0F); // 翻转 Y

                PoseStack stack = gui.pose();
                MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().bufferSource();

                boolean flat = !bakedmodel.usesBlockLight();
                if (flat) {
                    Lighting.setupForFlatItems();
                }

                itemRenderer.render(itemStack, ItemDisplayContext.GUI, false, stack, buffer, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
                buffer.endBatch();

                if (flat) {
                    Lighting.setupFor3DItems();
                }

                gui.pose().popPose();

                return;
            }
        }

        // 卡牌图片渲染
//        gui.blit(rl, x, y, 0, 0, width, height, width, height);
//        RenderSystem.setShaderTexture(0, rl);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);


        gui.setColor(1.0F, 1.0F, 1.0F, alpha);
        YdmBlitUtil.fullBlit(gui, rl, x, y, width, height);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
        // 无无障碍叙述
    }
}
