package de.cas_ual_ty.ydm;

import de.cas_ual_ty.ydm.deckbox.CustomDecks;
import de.cas_ual_ty.ydm.deckbox.DeckBoxItem;
import de.cas_ual_ty.ydm.duel.DeckSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = YDM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class YdmCreativeModeTabEvent {


    @SubscribeEvent
    public static void onBuildCreativeContents(BuildCreativeModeTabContentsEvent event) {

        // 判断是否是你自定义的标签，或者允许显示的标准标签
        if (event.getTab().equals(YdmItemGroup.YDM_TAB.get())) {
            // 1. 添加基础物品
            event.accept(YdmItems.PATREON_DECK_BOX.get());

            // 2. 动态添加来自数据库的套牌
            if (YdmDatabase.databaseReady) {
                for (DeckSource s : CustomDecks.getAllPatreonDeckSources()) {
                    ItemStack stack = makeItemStackFromDeckSource(s);
                    event.accept(stack);
                }
            }
        }
    }


    public static ItemStack makeItemStackFromDeckSource(DeckSource s)
    {
        ItemStack itemStack = new ItemStack(YdmItems.PATREON_DECK_BOX.get());
        DeckBoxItem.setDeckHolder(itemStack, s.deck);
        itemStack.setHoverName(s.name);
        return itemStack;
    }
}
