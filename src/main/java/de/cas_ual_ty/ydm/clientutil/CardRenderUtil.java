package de.cas_ual_ty.ydm.clientutil;

import com.mojang.blaze3d.systems.RenderSystem;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.CardSleevesType;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.duel.playfield.CardPosition;
import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.rarity.RarityEntry;
import de.cas_ual_ty.ydm.rarity.RarityLayer;
import de.cas_ual_ty.ydm.rarity.RarityLayerType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedList;
import java.util.List;

public class CardRenderUtil
{
    public static final ResourceLocation MASK_RL = new ResourceLocation(YDM.MOD_ID, "textures/gui/rarity_mask.png");

    private static LimitedTextureBinder infoTextureBinder;
    private static LimitedTextureBinder mainTextureBinder;

    // called from ClientProxy
    public static void init(int maxInfoImages, int maxMainImages)
    {
        CardRenderUtil.infoTextureBinder = new LimitedTextureBinder(ClientProxy.getMinecraft(), maxInfoImages);
        CardRenderUtil.mainTextureBinder = new LimitedTextureBinder(ClientProxy.getMinecraft(), maxMainImages);
    }

    public static void renderCardInfo(GuiGraphics gui, CardHolder card, AbstractContainerScreen<?> screen)
    {
        CardRenderUtil.renderCardInfo(gui, card, screen.getGuiLeft());
    }

    public static void renderCardInfo(GuiGraphics gui, CardHolder card)
    {
        CardRenderUtil.renderCardInfo(gui, card, 100);
    }

    public static void renderCardInfo(GuiGraphics gui, CardHolder card, int width)
    {
        CardRenderUtil.renderCardInfo(gui, card, false, width);
    }

    public static void renderCardInfo(GuiGraphics gui, CardHolder card, boolean token, int width) {
        if(card == null || card.getCard() == null)
        {
            return;
        }

        final float f = 0.5f;
        final int imageSize = 64;
        int margin = 2;

        int maxWidth = width - margin * 2;

        gui.pose().pushPose();
        ScreenUtil.white();

        int x = margin;

        if(maxWidth < imageSize)
        {
            // draw it centered if the space we got is limited
            // to make sure the image is NOT rendered more to the right of the center
            x = (maxWidth - imageSize) / 2 + margin;
        }

        // card texture

//        CardRenderUtil.bindInfoResourceLocation(card);
        YdmBlitUtil.fullBlit(gui, card.getInfoImageResourceLocation(), x, margin, imageSize, imageSize);

        if(token) {
//            RenderSystem.setShaderTexture(0, CardRenderUtil.getInfoTokenOverlay());
            YdmBlitUtil.fullBlit(gui, CardRenderUtil.getInfoTokenOverlay(), x, margin, imageSize, imageSize);
        }

        // need to multiply x2 because we are scaling the text to x0.5
        maxWidth *= 2;
        margin *= 2;
        gui.pose().scale(f, f, f);

        // card description text

        Font fontRenderer = ClientProxy.getMinecraft().font;

        List<Component> list = new LinkedList<>();
        card.getCard().addInformation(list);

        ScreenUtil.drawSplitString(gui, fontRenderer, list, margin, imageSize * 2 + margin * 2, maxWidth, 0xFFFFFF);

        gui.pose().popPose();
    }

    public static void bindInfoResourceLocation(CardHolder c)
    {
        CardRenderUtil.infoTextureBinder.bind(c.getInfoImageResourceLocation());
    }

    public static void bindMainResourceLocation(CardHolder c)
    {
        CardRenderUtil.mainTextureBinder.bind(c.getMainImageResourceLocation());
    }

    public static void bindInfoResourceLocation(Properties p, byte imageIndex)
    {
        CardRenderUtil.infoTextureBinder.bind(p.getInfoImageResourceLocation(imageIndex));
    }

    public static void bindMainResourceLocation(Properties p, byte imageIndex)
    {
        CardRenderUtil.mainTextureBinder.bind(p.getMainImageResourceLocation(imageIndex));
    }

    public static void bindInfoResourceLocation(ResourceLocation r)
    {
        CardRenderUtil.infoTextureBinder.bind(r);
    }

    public static void bindMainResourceLocation(ResourceLocation r)
    {
        CardRenderUtil.mainTextureBinder.bind(r);
    }

    public static void bindSleeves(CardSleevesType s)
    {
        RenderSystem.setShaderTexture(0, s.getMainRL(ClientProxy.activeCardMainImageSize));
    }

    public static ResourceLocation getInfoCardBack()
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + ClientProxy.activeCardInfoImageSize + "/" + YdmItems.CARD_BACK.getId().getPath() + ".png");
    }

    public static ResourceLocation getMainCardBack()
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + ClientProxy.activeCardMainImageSize + "/" + YdmItems.CARD_BACK.getId().getPath() + ".png");
    }

    public static ResourceLocation getInfoTokenOverlay()
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + ClientProxy.activeCardInfoImageSize + "/" + "token_overlay" + ".png");
    }

    public static ResourceLocation getMainTokenOverlay()
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + ClientProxy.activeCardMainImageSize + "/" + "token_overlay" + ".png");
    }

    public static ResourceLocation getRarityOverlay()
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + ClientProxy.activeCardInfoImageSize + "/" + "token_overlay" + ".png");
    }

    public static void renderInfoCardWithRarity(GuiGraphics pGuiGraphics, int mouseX, int mouseY, int x, int y, int width, int height, CardHolder card)
    {
//        Minecraft mc = ClientProxy.getMinecraft();

        // bind the texture depending on faceup or facedown
//        CardRenderUtil.bindInfoResourceLocation(card);
        YdmBlitUtil.fullBlit(pGuiGraphics, card.getInfoImageResourceLocation(), x - width / 2, y - height / 2, width, height);

        RarityEntry rarity = YdmDatabase.getRarity(card.getRarity());

        if(rarity != null)
        {
            for(RarityLayer layer : rarity.layers)
            {

                Runnable mask = () ->
                {
//                    RenderSystem.setShaderTexture(0, MASK_RL);
                    YdmBlitUtil.fullBlit(pGuiGraphics, MASK_RL, mouseX - width / 2, mouseY - height / 2, width, height);
                };

                Runnable renderer = () ->
                {
//                    RenderSystem.setShaderTexture(0, layer.getInfoImageResourceLocation());
                    YdmBlitUtil.fullBlit(pGuiGraphics, layer.getInfoImageResourceLocation(), x - width / 2, y - height / 2, width, height);
                };

                YdmBlitUtil.advancedMaskedBlit(pGuiGraphics, mask, renderer, layer.type.invertedRendering);
//                YdmBlitUtil.advancedMaskedBlit(pGuiGraphics, x, y, width, height, mask, renderer, layer.type.invertedRendering);
            }
        }
    }

    public static void renderDuelCardAdvanced(GuiGraphics gui, CardSleevesType back, int mouseX, int mouseY, int x, int y, int width, int height, DuelCard card, YdmBlitUtil.FullBlitMethod blitMethod, boolean forceFaceUp)
    {
        CardPosition position = card.getCardPosition();

        // bind the texture depending on faceup or facedown
        if(!card.getCardPosition().isFaceUp && forceFaceUp)
        {
            position = position.flip();
        }

        CardRenderUtil.renderDuelCardAdvanced(gui, back, mouseX, mouseY, x, y, width, height, card, position, blitMethod);
    }

    public static void renderDuelCardAdvanced(GuiGraphics gui, CardSleevesType back, int mouseX, int mouseY, int x, int y, int width, int height, DuelCard card, CardPosition position, YdmBlitUtil.FullBlitMethod blitMethod)
    {
        Minecraft mc = ClientProxy.getMinecraft();
        ResourceLocation resourceLocation;

        // bind the texture depending on faceup or facedown
        if(position.isFaceUp) {
//            CardRenderUtil.bindMainResourceLocation(card.getCardHolder());
            resourceLocation = card.getCardHolder().getMainImageResourceLocation();
        }
        else
        {
//            RenderSystem.setShaderTexture(0, back.getMainRL(ClientProxy.activeCardMainImageSize));
            resourceLocation = back.getMainRL(ClientProxy.activeCardMainImageSize);
        }

        blitMethod.fullBlit(gui, resourceLocation, x, y, width, height);

        if(card.getIsToken())
        {
//            RenderSystem.setShaderTexture(0, CardRenderUtil.getMainTokenOverlay());
            blitMethod.fullBlit(gui, CardRenderUtil.getMainTokenOverlay(), x, y, width, height);
        }

        if(position.isFaceUp && !card.getIsToken())
        {
            RarityEntry rarity = YdmDatabase.getRarity(card.getCardHolder().getRarity());

            if(rarity != null)
            {
                for(RarityLayer layer : rarity.layers)
                {
                    Runnable mask = () ->
                    {
//                        RenderSystem.setShaderTexture(0, MASK_RL);
                        blitMethod.fullBlit(gui, MASK_RL, mouseX - width / 2, mouseY - height / 2, width, height);
                    };

                    Runnable renderer = () ->
                    {
//                        RenderSystem.setShaderTexture(0, layer.getMainImageResourceLocation());
                        blitMethod.fullBlit(gui, layer.getMainImageResourceLocation(), x, y, width, height);
                    };

                    YdmBlitUtil.advancedMaskedBlit(gui, mask, renderer, layer.type.invertedRendering);
                }
            }
        }
    }

    public static void renderDuelCard(GuiGraphics gui, CardSleevesType back, int mouseX, int mouseY, int x, int y, int width, int height, DuelCard card, boolean forceFaceUp)
    {
        CardRenderUtil.renderDuelCardAdvanced(gui, back, mouseX, mouseY, x, y, width, height, card,
                card.getCardPosition().isStraight
                        ? (gui1, texture, x1, y1, width1, height1) -> YdmBlitUtil.fullBlit(gui1, texture, (int) x1, (int) y1, width, height)
                        : (gui2, texture1, x2, y2, blitW, blitH) -> YdmBlitUtil.blit90(gui2, texture1, x2, y2, blitW, blitH, 0, 0, width, height), forceFaceUp);
    }

    public static void renderDuelCardReversed(GuiGraphics gui, CardSleevesType back, int mouseX, int mouseY, int x, int y, int width, int height, DuelCard card, boolean forceFaceUp)
    {
        CardRenderUtil.renderDuelCardAdvanced(gui, back, mouseX, mouseY, x, y, width, height, card,
                card.getCardPosition().isStraight
                        ? (gui1, texture, x1, y1, blitW, blitH) -> YdmBlitUtil.blit180(gui1, texture, x1, y1, blitW, blitH, 0, 0, width, height)
                        : (gui2, texture1, x2, y2, blitW1, blitH1) -> YdmBlitUtil.blit270(gui2, texture1, x2, y2, blitW1, blitH1, 0, 0, width, height), forceFaceUp);
    }

    public static void renderDuelCardCentered(GuiGraphics gui, CardSleevesType back, int mouseX, int mouseY, int x, int y, int width, int height, DuelCard card, boolean forceFaceUp) {
        // if width and height are more of a rectangle, this centers the texture horizontally
        x -= (height - width) / 2;
        width = height;

        CardRenderUtil.renderDuelCard(gui, back, mouseX, mouseY, x, y, width, height, card, forceFaceUp);
    }

    public static void renderDuelCardReversedCentered(GuiGraphics gui, CardSleevesType back, int mouseX, int mouseY, int x, int y, int width, int height, DuelCard card, boolean forceFaceUp)
    {
        // if width and height are more of a rectangle, this centers the texture horizontally
        x -= (height - width) / 2;
        width = height;

        CardRenderUtil.renderDuelCardReversed(gui, back, mouseX, mouseY, x, y, width, height, card, forceFaceUp);
    }
}
