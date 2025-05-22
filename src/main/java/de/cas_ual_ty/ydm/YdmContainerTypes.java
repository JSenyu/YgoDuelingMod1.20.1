package de.cas_ual_ty.ydm;

import de.cas_ual_ty.ydm.cardbinder.CardBinderContainer;
import de.cas_ual_ty.ydm.cardsupply.CardSupplyContainer;
import de.cas_ual_ty.ydm.deckbox.DeckBoxContainer;
import de.cas_ual_ty.ydm.duel.block.DuelBlockContainer;
import de.cas_ual_ty.ydm.duel.dueldisk.DuelEntityContainer;
import de.cas_ual_ty.ydm.set.CardSetContainer;
import de.cas_ual_ty.ydm.set.CardSetContentsContainer;
import de.cas_ual_ty.ydm.simplebinder.SimpleBinderContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class YdmContainerTypes {
    private static final DeferredRegister<MenuType<?>> DEFERRED_REGISTER =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, YDM.MOD_ID);

    // 由于所有容器注册都需 IContainerFactory，统一使用三参数 lambda
    public static final RegistryObject<MenuType<CardBinderContainer>> CARD_BINDER =
            DEFERRED_REGISTER.register("card_binder", () ->
                    IForgeMenuType.create((windowId, inv, buf) ->
                            new CardBinderContainer(YdmContainerTypes.CARD_BINDER.get(), windowId, inv)
                    )
            );

    public static final RegistryObject<MenuType<DeckBoxContainer>> DECK_BOX =
            DEFERRED_REGISTER.register("deck_box", () ->
                    IForgeMenuType.create((windowId, inv, buf) ->
                            new DeckBoxContainer(YdmContainerTypes.DECK_BOX.get(), windowId, inv)
                    )
            );

    public static final RegistryObject<MenuType<DuelBlockContainer>> DUEL_BLOCK_CONTAINER =
            DEFERRED_REGISTER.register("duel_block_container", () ->
                    IForgeMenuType.create((windowId, inv, buf) ->
                            new DuelBlockContainer(YdmContainerTypes.DUEL_BLOCK_CONTAINER.get(), windowId, inv, buf)
                    )
            );

    public static final RegistryObject<MenuType<DuelEntityContainer>> DUEL_ENTITY_CONTAINER =
            DEFERRED_REGISTER.register("duel_entity_container", () ->
                    IForgeMenuType.create((windowId, inv, buf) ->
                            new DuelEntityContainer(YdmContainerTypes.DUEL_ENTITY_CONTAINER.get(), windowId, inv, buf)
                    )
            );

    public static final RegistryObject<MenuType<CardSupplyContainer>> CARD_SUPPLY =
            DEFERRED_REGISTER.register("card_supply", () ->
                    IForgeMenuType.create((windowId, inv, buf) ->
                            new CardSupplyContainer(YdmContainerTypes.CARD_SUPPLY.get(), windowId, inv, buf)
                    )
            );

    public static final RegistryObject<MenuType<CardSetContainer>> CARD_SET =
            DEFERRED_REGISTER.register("card_set", () ->
                    IForgeMenuType.create((windowId, inv, buf) ->
                            new CardSetContainer(YdmContainerTypes.CARD_SET.get(), windowId, inv, buf)
                    )
            );

    public static final RegistryObject<MenuType<CardSetContentsContainer>> CARD_SET_CONTENTS =
            DEFERRED_REGISTER.register("card_set_contents", () ->
                    IForgeMenuType.create((windowId, inv, buf) ->
                            new CardSetContentsContainer(YdmContainerTypes.CARD_SET_CONTENTS.get(), windowId, inv, buf)
                    )
            );

    public static final RegistryObject<MenuType<SimpleBinderContainer>> SIMPLE_BINDER =
            DEFERRED_REGISTER.register("simple_binder", () ->
                    IForgeMenuType.create((windowId, inv, buf) ->
                            new SimpleBinderContainer(YdmContainerTypes.SIMPLE_BINDER.get(), windowId, inv, buf)
                    )
            );

    public static void register(IEventBus bus) {
        DEFERRED_REGISTER.register(bus);
    }
}
