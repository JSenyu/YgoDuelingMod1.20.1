package de.cas_ual_ty.ydm;


import de.cas_ual_ty.ydm.card.CardItem;
import de.cas_ual_ty.ydm.deckbox.CustomDecks;
import de.cas_ual_ty.ydm.deckbox.PatreonDeckBoxItem;
import de.cas_ual_ty.ydm.rarity.Rarities;
import de.cas_ual_ty.ydm.set.CardSet;
import de.cas_ual_ty.ydm.set.CardSetItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class YdmItemGroup {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, YDM.MOD_ID);

    // 主模组总览页
    public static final RegistryObject<CreativeModeTab> YDM_TAB;

    // 卡牌物品页（带搜索框）
    public static final RegistryObject<CreativeModeTab> CARDS_TAB;

    // 套装物品页
    public static final RegistryObject<CreativeModeTab> SETS_TAB;

    static {
        YDM_TAB = TABS.register("ydm_tab", () ->
                CreativeModeTab.builder()
                        .icon(() -> YdmItems.CARD_BACK.get().getDefaultInstance())
                        .title(Component.translatable("itemGroup." + YDM.MOD_ID))
                        .displayItems((params, output) -> {
                            output.accept(YdmItems.CARD_BACK.get().getDefaultInstance());
                            output.accept(YdmItems.BLANC_CARD.get().getDefaultInstance());
                            output.accept(YdmItems.CARD_BACK.get().getDefaultInstance());
                            output.accept(YdmItems.BLANC_SET.get().getDefaultInstance());
                            output.accept(YdmItems.OPENED_SET.get().getDefaultInstance());
                            output.accept(YdmItems.CARD_BINDER.get().getDefaultInstance());
                            output.accept(YdmItems.DUEL_PLAYMAT.get().getDefaultInstance());
                            output.accept(YdmItems.DUEL_TABLE.get().getDefaultInstance());
                            output.accept(YdmItems.CARD_SUPPLY.get().getDefaultInstance());
                            output.accept(YdmItems.SIMPLE_BINDER_3.get().getDefaultInstance());
                            output.accept(YdmItems.SIMPLE_BINDER_9.get().getDefaultInstance());
                            output.accept(YdmItems.SIMPLE_BINDER_27.get().getDefaultInstance());
                            output.accept(YdmItems.MILLENIUM_EYE.get().getDefaultInstance());
                            output.accept(YdmItems.MILLENIUM_KEY.get().getDefaultInstance());
                            output.accept(YdmItems.MILLENIUM_NECKLACE.get().getDefaultInstance());
                            output.accept(YdmItems.MILLENIUM_PUZZLE.get().getDefaultInstance());
                            output.accept(YdmItems.MILLENIUM_RING.get().getDefaultInstance());
                            output.accept(YdmItems.MILLENIUM_ROD.get().getDefaultInstance());
                            output.accept(YdmItems.MILLENIUM_SCALE.get().getDefaultInstance());
                            output.accept(YdmItems.DUEL_DISK.get().getDefaultInstance());
                            output.accept(YdmItems.CHAOS_DISK.get().getDefaultInstance());
                            output.accept(YdmItems.ACADEMIA_DISK.get().getDefaultInstance());
                            output.accept(YdmItems.ACADEMIA_DISK_RED.get().getDefaultInstance());
                            output.accept(YdmItems.ACADEMIA_DISK_BLUE.get().getDefaultInstance());
                            output.accept(YdmItems.ACADEMIA_DISK_YELLOW.get().getDefaultInstance());
                            output.accept(YdmItems.ROCK_SPIRIT_DISK.get().getDefaultInstance());
                            output.accept(YdmItems.TRUEMAN_DISK.get().getDefaultInstance());
                            output.accept(YdmItems.JEWEL_DISK.get().getDefaultInstance());
                            output.accept(YdmItems.KAIBAMAN_DISK.get().getDefaultInstance());
                            output.accept(YdmItems.CYBER_DESIGN_INTERFACE.get().getDefaultInstance());
                            output.accept(YdmItems.BLACK_DECK_BOX.get().getDefaultInstance());
                            output.accept(YdmItems.RED_DECK_BOX.get().getDefaultInstance());
                            output.accept(YdmItems.GREEN_DECK_BOX.get().getDefaultInstance());
                            output.accept(YdmItems.BROWN_DECK_BOX.get().getDefaultInstance());
                            output.accept(YdmItems.BLUE_DECK_BOX.get().getDefaultInstance());
                            output.accept(YdmItems.PURPLE_DECK_BOX.get().getDefaultInstance());
                            output.accept(YdmItems.CYAN_DECK_BOX.get().getDefaultInstance());
                            output.accept(YdmItems.LIGHT_GRAY_DECK_BOX.get().getDefaultInstance());
                            output.accept(YdmItems.GRAY_DECK_BOX.get().getDefaultInstance());
                            output.accept(YdmItems.PINK_DECK_BOX.get().getDefaultInstance());
                            output.accept(YdmItems.LIME_DECK_BOX.get().getDefaultInstance());
                            output.accept(YdmItems.YELLOW_DECK_BOX.get().getDefaultInstance());
                            output.accept(YdmItems.LIGHT_BLUE_DECK_BOX.get().getDefaultInstance());
                            output.accept(YdmItems.MAGENTA_DECK_BOX.get().getDefaultInstance());
                            output.accept(YdmItems.ORANGE_DECK_BOX.get().getDefaultInstance());
                            output.accept(YdmItems.WHITE_DECK_BOX.get().getDefaultInstance());
                            output.accept(YdmItems.IRON_DECK_BOX.get().getDefaultInstance());
                            output.accept(YdmItems.GOLD_DECK_BOX.get().getDefaultInstance());
                            output.accept(YdmItems.DIAMOND_DECK_BOX.get().getDefaultInstance());
                            output.accept(YdmItems.EMERALD_DECK_BOX.get().getDefaultInstance());
                            output.accept(YdmItems.PATREON_DECK_BOX.get().getDefaultInstance());
                            YdmItems.REGISTRY_OBJECT_LIST.forEach(obj -> output.accept(obj.get().getDefaultInstance()));
                            if (YdmDatabase.databaseReady) {
                                CustomDecks.getAllPatreonDeckSources().stream().map(PatreonDeckBoxItem::makeItemStackFromDeckSource).forEach(output::accept);
                            }
                        })
                        .build()
        );

        CARDS_TAB = TABS.register("cards", () ->
                        CreativeModeTab.builder()
                                .icon(() -> YdmItems.BLANC_CARD.get().getDefaultInstance())
                                .title(Component.translatable("itemGroup." + YDM.MOD_ID + ".cards"))
                                .backgroundSuffix("item_search.png")
                                .withSearchBar()
                                .displayItems((params, output) -> {
//                        output.accept(YdmItems.CARD.get().getDefaultInstance());

                                    YdmDatabase.forAllCardVariants((card, idx) ->
                                            output.accept(CardItem.createItemForCard(card, idx, Rarities.CREATIVE.name))
                                    );
                                })
                                .build()
        );

        SETS_TAB = TABS.register("sets", () ->
                        CreativeModeTab.builder()
                                .icon(() -> YdmItems.BLANC_SET.get().getDefaultInstance())
                                .title(Component.translatable("itemGroup." + YDM.MOD_ID + ".sets"))
                                .backgroundSuffix("item_search.png")
                                .withSearchBar()
                                .displayItems((params, output) -> {
//                        output.accept(YdmItems.BLANC_SET.get().getDefaultInstance());

                                    for (CardSet set : YdmDatabase.SETS_LIST.getList()) {
                                        if (set.isIndependentAndItem()) {
                                            ItemStack setItem = CardSetItem.createItemForSet(set);
                                            output.accept(setItem);
//                                            System.out.println("Set item: " + setItem.getRarity().name());
                                        }
                                    }
                                })
                                .build()
        );
    }

    public static void register(IEventBus bus){
        TABS.register(bus);
    }


//    private void initCreativeTab(){
//
//        YDM.ydmItemGroup = CreativeModeTab.builder().icon(() -> YdmItems.CARD_BACK.get().getDefaultInstance())
//                .title(Component.translatable(YDM.MOD_ID, YdmItems.BLANC_CARD))
//                .displayItems(
//                        (itemDisplayParameters, output) -> {
//                            output.accept(YdmItems.CARD_BACK.get().getDefaultInstance());
//                            output.accept(YdmItems.BLANC_CARD.get().getDefaultInstance());
//                            output.accept(YdmItems.CARD_BACK.get().getDefaultInstance());
//                            output.accept(YdmItems.BLANC_SET.get().getDefaultInstance());
//                            output.accept(YdmItems.OPENED_SET.get().getDefaultInstance());
//                            output.accept(YdmItems.CARD_BINDER.get().getDefaultInstance());
//                            output.accept(YdmItems.DUEL_PLAYMAT.get().getDefaultInstance());
//                            output.accept(YdmItems.DUEL_TABLE.get().getDefaultInstance());
//                            output.accept(YdmItems.CARD_SUPPLY.get().getDefaultInstance());
//                            output.accept(YdmItems.SIMPLE_BINDER_3.get().getDefaultInstance());
//                            output.accept(YdmItems.SIMPLE_BINDER_9.get().getDefaultInstance());
//                            output.accept(YdmItems.SIMPLE_BINDER_27.get().getDefaultInstance());
//                            output.accept(YdmItems.MILLENIUM_EYE.get().getDefaultInstance());
//                            output.accept(YdmItems.MILLENIUM_KEY.get().getDefaultInstance());
//                            output.accept(YdmItems.MILLENIUM_NECKLACE.get().getDefaultInstance());
//                            output.accept(YdmItems.MILLENIUM_PUZZLE.get().getDefaultInstance());
//                            output.accept(YdmItems.MILLENIUM_RING.get().getDefaultInstance());
//                            output.accept(YdmItems.MILLENIUM_ROD.get().getDefaultInstance());
//                            output.accept(YdmItems.MILLENIUM_SCALE.get().getDefaultInstance());
//                            output.accept(YdmItems.DUEL_DISK.get().getDefaultInstance());
//                            output.accept(YdmItems.CHAOS_DISK.get().getDefaultInstance());
//                            output.accept(YdmItems.ACADEMIA_DISK.get().getDefaultInstance());
//                            output.accept(YdmItems.ACADEMIA_DISK_RED.get().getDefaultInstance());
//                            output.accept(YdmItems.ACADEMIA_DISK_BLUE.get().getDefaultInstance());
//                            output.accept(YdmItems.ACADEMIA_DISK_YELLOW.get().getDefaultInstance());
//                            output.accept(YdmItems.ROCK_SPIRIT_DISK.get().getDefaultInstance());
//                            output.accept(YdmItems.TRUEMAN_DISK.get().getDefaultInstance());
//                            output.accept(YdmItems.JEWEL_DISK.get().getDefaultInstance());
//                            output.accept(YdmItems.KAIBAMAN_DISK.get().getDefaultInstance());
//                            output.accept(YdmItems.CYBER_DESIGN_INTERFACE.get().getDefaultInstance());
//                            output.accept(YdmItems.BLACK_DECK_BOX.get().getDefaultInstance());
//                            output.accept(YdmItems.RED_DECK_BOX.get().getDefaultInstance());
//                            output.accept(YdmItems.GREEN_DECK_BOX.get().getDefaultInstance());
//                            output.accept(YdmItems.BROWN_DECK_BOX.get().getDefaultInstance());
//                            output.accept(YdmItems.BLUE_DECK_BOX.get().getDefaultInstance());
//                            output.accept(YdmItems.PURPLE_DECK_BOX.get().getDefaultInstance());
//                            output.accept(YdmItems.CYAN_DECK_BOX.get().getDefaultInstance());
//                            output.accept(YdmItems.LIGHT_GRAY_DECK_BOX.get().getDefaultInstance());
//                            output.accept(YdmItems.GRAY_DECK_BOX.get().getDefaultInstance());
//                            output.accept(YdmItems.PINK_DECK_BOX.get().getDefaultInstance());
//                            output.accept(YdmItems.LIME_DECK_BOX.get().getDefaultInstance());
//                            output.accept(YdmItems.YELLOW_DECK_BOX.get().getDefaultInstance());
//                            output.accept(YdmItems.LIGHT_BLUE_DECK_BOX.get().getDefaultInstance());
//                            output.accept(YdmItems.MAGENTA_DECK_BOX.get().getDefaultInstance());
//                            output.accept(YdmItems.ORANGE_DECK_BOX.get().getDefaultInstance());
//                            output.accept(YdmItems.WHITE_DECK_BOX.get().getDefaultInstance());
//                            output.accept(YdmItems.IRON_DECK_BOX.get().getDefaultInstance());
//                            output.accept(YdmItems.GOLD_DECK_BOX.get().getDefaultInstance());
//                            output.accept(YdmItems.DIAMOND_DECK_BOX.get().getDefaultInstance());
//                            output.accept(YdmItems.EMERALD_DECK_BOX.get().getDefaultInstance());
//                            output.accept(YdmItems.PATREON_DECK_BOX.get().getDefaultInstance());
//                            YdmItems.REGISTRY_OBJECT_LIST.forEach(obj -> output.accept(obj.get().getDefaultInstance()));
//
//                        }
//                )
//                .build();
//
////                new YdmItemGroup(() -> YdmItems.CARD_BACK.get().getDefaultInstance());
//        YDM.cardsItemGroup = CreativeModeTab.builder()
//                .icon(() -> YdmItems.BLANC_CARD.get().getDefaultInstance())
//                .title(Component.translatable(YDM.MOD_ID + ".cards", YdmItems.BLANC_CARD))
//                .backgroundSuffix("item_search.png")
//                .withSearchBar()
//                .displayItems(
//                        (itemDisplayParameters, output) -> {
//                            output.accept(YdmItems.CARD.get().getDefaultInstance());
//                            YdmDatabase.forAllCardVariants((card, imageIndex) -> output.accept(CardItem.createItemForCard(card, imageIndex, Rarities.CREATIVE.name)));
//
//                        }
//                ).build();
//
////                new YdmItemGroup(() -> YdmItems.BLANC_CARD.get().getDefaultInstance(), "item_search.png")
////        {
////            @Override
////            public boolean hasSearchBar()
////            {
////                return true;
////            }
////        };
//        YDM.setsItemGroup = CreativeModeTab.builder()
//                .icon(() -> YdmItems.BLANC_SET.get().getDefaultInstance())
//                .title(Component.translatable(YDM.MOD_ID + ".sets", YdmItems.BLANC_SET))
//                .backgroundSuffix("item_search.png")
//                .withSearchBar()
//                .displayItems(
//                        (itemDisplayParameters, output) -> {
//                            output.accept(YdmItems.SET.get().getDefaultInstance());
//
//                            YdmDatabase.SETS_LIST.forEach(cardSet -> {
//                                if(cardSet.isIndependentAndItem()) {
//                                    output.accept(CardSetItem.createItemForSet(cardSet));
//                                }
//                            });
//                        }
//                )
//                .build();
//
////                new YdmItemGroup(() -> YdmItems.BLANC_SET.get().getDefaultInstance(), "item_search.png")
////        {
////            @Override
////            public boolean hasSearchBar()
////            {
////                return true;
////            }
////        };
//    }
}
