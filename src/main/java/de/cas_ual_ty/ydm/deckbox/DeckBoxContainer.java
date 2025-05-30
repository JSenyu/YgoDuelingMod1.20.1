package de.cas_ual_ty.ydm.deckbox;

import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardSleevesItem;
import de.cas_ual_ty.ydm.card.properties.YdmProperties;
import de.cas_ual_ty.ydm.util.YDMItemHandler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class DeckBoxContainer extends AbstractContainerMenu
{
    public ItemStack itemStack;
    public YDMItemHandler itemHandler;
    public Slot cardSleevesSlot;
    
    public DeckBoxContainer(MenuType<?> type, int id, Inventory playerInventory)
    {
        this(type, id, playerInventory, DeckBoxItem.getActiveDeckBox(playerInventory.player));
    }
    
    public DeckBoxContainer(MenuType<?> type, int id, Inventory playerInventory, ItemStack itemStack)
    {
        super(type, id);
        
        this.itemStack = itemStack;
        
        itemHandler = ((DeckBoxItem) itemStack.getItem()).getItemHandler(itemStack);
        itemHandler.load();
        
        final int itemsPerRow = 15;
        
        // main deck
        for(int y = 0; y < DeckHolder.MAIN_DECK_SIZE / itemsPerRow; ++y)
        {
            for(int x = 0; x < itemsPerRow && x + y * itemsPerRow < DeckHolder.MAIN_DECK_SIZE; ++x)
            {
                addSlot(new DeckBoxSlot(itemHandler, x + y * itemsPerRow + DeckHolder.MAIN_DECK_INDEX_START, 8 + x * 18, 18 + y * 18));
            }
        }
        
        // extra deck
        for(int x = 0; x < DeckHolder.EXTRA_DECK_SIZE; ++x)
        {
            addSlot(new DeckBoxSlot(itemHandler, x + DeckHolder.EXTRA_DECK_INDEX_START, 8 + x * 18, 104));
        }
        
        // side deck
        for(int x = 0; x < DeckHolder.SIDE_DECK_SIZE; ++x)
        {
            addSlot(new DeckBoxSlot(itemHandler, x + DeckHolder.SIDE_DECK_INDEX_START, 8 + x * 18, 136));
        }
        
        for(int x = 0; x < DeckHolder.SIDE_DECK_SIZE; ++x)
        {
            addSlot(new DeckBoxSlot(itemHandler, x + DeckHolder.SIDE_DECK_INDEX_START, 8 + x * 18, 136));
        }
        
        addSlot(cardSleevesSlot = new SlotItemHandler(itemHandler, DeckHolder.SLEEVES_INDEX, 8 + 12 * 18, 168 + 0 * 18)
        {
            @Override
            public boolean mayPlace(ItemStack stack)
            {
                return stack.getItem() instanceof CardSleevesItem;
            }
            
            @Override
            public int getMaxStackSize()
            {
                return 1;
            }
        });
        
        // player inventory
        for(int y = 0; y < 3; ++y)
        {
            for(int x = 0; x < 9; ++x)
            {
                addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 168 + y * 18)
                {
                    @Override
                    public boolean mayPickup(Player pPlayer)
                    {
                        return getItem() != itemStack && super.mayPickup(pPlayer);
                    }
                });
            }
        }
        
        // player hot bar
        for(int x = 0; x < 9; ++x)
        {
            addSlot(new Slot(playerInventory, x, 8 + x * 18, 226)
            {
                @Override
                public boolean mayPickup(Player pPlayer)
                {
                    return getItem() != itemStack && super.mayPickup(pPlayer);
                }
            });
        }
    }
    
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index)
    {
        Slot slot = slots.get(index);
        ItemStack original = slot.getItem().copy();
        
        if(index < DeckHolder.TOTAL_DECK_SIZE || index == cardSleevesSlot.index)
        {
            //deck box slot or sleeves slot into inventory
            ItemStack itemStack = slot.getItem();
            
            if(moveItemStackTo(itemStack, cardSleevesSlot.index + 1, slots.size(), false))
            {
                slot.set(ItemStack.EMPTY);
                return ItemStack.EMPTY;
            }
            
            return ItemStack.EMPTY;
        }
        else if(original.getItem() == YdmItems.CARD.get())
        {
            //inventory to deck box
            
            YdmProperties card = YdmItems.CARD.get().getCardHolder(original).getCard();
            boolean isExtraDeck = card.getIsInExtraDeck();
            
            int minTarget;
            int maxTarget;
            
            if(!isExtraDeck)
            {
                minTarget = DeckHolder.MAIN_DECK_INDEX_START;
                maxTarget = DeckHolder.MAIN_DECK_INDEX_END;
            }
            else
            {
                minTarget = DeckHolder.EXTRA_DECK_INDEX_START;
                maxTarget = DeckHolder.EXTRA_DECK_INDEX_END;
            }
            
            ItemStack itemStack = slot.getItem().split(1);
            
            if(moveItemStackTo(itemStack, minTarget, maxTarget, false))
            {
                return slot.getItem();
            }
            // side deck
            else if(moveItemStackTo(itemStack, DeckHolder.SIDE_DECK_INDEX_START, DeckHolder.SIDE_DECK_INDEX_END, false))
            {
                return slot.getItem();
            }
            
            slot.set(original);
        }
        else if(original.getItem() instanceof CardSleevesItem && !cardSleevesSlot.hasItem())
        {
            cardSleevesSlot.set(slot.getItem().split(1));
            return slot.getItem();
        }
        
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean stillValid(Player playerIn)
    {
        return true;
    }
    
    @Override
    public void removed(Player playerIn)
    {
        itemHandler.save();
        super.removed(playerIn);
    }
}
