package de.cas_ual_ty.ydm.card;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.properties.YdmProperties;
import de.cas_ual_ty.ydm.rarity.Rarities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class CardItem extends Item
{

    public CardItem(Properties properties)
    {
        super(properties);
    }
    
    @Override
    public void appendHoverText(ItemStack itemStack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        CardHolder holder = getCardHolder(itemStack);
        tooltip.clear();
        holder.addInformation(tooltip);
    }
    
    @Override
    public Component getName(ItemStack itemStack)
    {
        CardHolder holder = getCardHolder(itemStack);
        return Component.literal(holder.getCard().getName());
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand)
    {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        CardHolder cardHolder = getCardHolder(itemStack);
        if(cardHolder != null && pPlayer.level().isClientSide)
        {
            YDM.proxy.openCardInspectScreen(cardHolder);
            return InteractionResultHolder.success(itemStack);
        }
        
        return super.use(pLevel, pPlayer, pUsedHand);
    }
    
    public static CardHolder getCardHolder(ItemStack itemStack)
    {
        return new ItemStackCardHolder(itemStack);
    }
    
    public ItemStack createItemForCard(YdmProperties card, byte imageIndex, String rarity, String code)
    {
        ItemStack itemStack = new ItemStack(this);
        getCardHolder(itemStack).override(new CardHolder(card, imageIndex, rarity, code));
        return itemStack;
    }
    
    public static ItemStack createItemForCard(YdmProperties card, byte imageIndex, String rarity)
    {
        ItemStack itemStack = YdmItems.CARD.get().getDefaultInstance();
        getCardHolder(itemStack).override(new CardHolder(card, imageIndex, rarity));
        return itemStack;
    }
    
    public ItemStack createItemForCard(YdmProperties card)
    {
        return createItemForCard(card, (byte) 0, Rarities.CREATIVE.name);
    }
    
    public ItemStack createItemForCardHolder(CardHolder card)
    {
        ItemStack itemStack = new ItemStack(this);
        getCardHolder(itemStack).override(card);
        return itemStack;
    }

    
    @Override
    public boolean shouldOverrideMultiplayerNbt()
    {
        return true;
    }
}
