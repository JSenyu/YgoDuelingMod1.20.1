package de.cas_ual_ty.ydm.set;

import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.YdmItems;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CardSetItem extends CardSetBaseItem
{

    public CardSetItem(Item.Properties properties)
    {
        super(properties);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
    {
        ItemStack stack = CardSetItem.getActiveSet(player);
        
        if(player.getItemInHand(hand) == stack)
        {
            unseal(stack, player, hand);
            
            if(!world.isClientSide)
            {
                return player.getItemInHand(hand).getItem().use(world, player, hand);
            }
        }
        
        return super.use(world, player, hand);
    }
    
    public void unseal(ItemStack itemStack, Player player, InteractionHand hand)
    {
        ItemStack newStack = YdmItems.OPENED_SET.get().createItemForSet(getCardSet(itemStack));
        player.setItemInHand(hand, newStack);
        
        if(itemStack.getCount() > 1)
        {
            itemStack.shrink(1);
            
            if(!player.level().isClientSide)
            {
                player.getInventory().placeItemBackInInventory(itemStack);
            }
        }
    }
    
    public static ItemStack createItemForSet(CardSet set)
    {
//        ItemStack itemStack = new ItemStack(YdmItems.SET.get());
        ItemStack itemStack = YdmItems.SET.get().getDefaultInstance();
        setCardSet(itemStack, set);
        return itemStack;
    }
    
    public static ItemStack getActiveSet(Player player)
    {
        if(player.getMainHandItem().getItem() == YdmItems.SET.get())
        {
            return player.getMainHandItem();
        }
        else if(player.getOffhandItem().getItem() == YdmItems.SET.get())
        {
            return player.getOffhandItem();
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }


}
