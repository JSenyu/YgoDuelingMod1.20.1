package de.cas_ual_ty.ydm.deckbox;

import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.duel.DeckSource;
import net.minecraft.world.item.ItemStack;

public class PatreonDeckBoxItem extends DeckBoxItem
{
    public PatreonDeckBoxItem(Properties properties)
    {
        super(properties);
    }

    public static ItemStack makeItemStackFromDeckSource(DeckSource s)
    {
        ItemStack itemStack = new ItemStack(YdmItems.PATREON_DECK_BOX.get());
        setDeckHolder(itemStack, s.deck);
        itemStack.setHoverName(s.name);
        return itemStack;
    }
}
