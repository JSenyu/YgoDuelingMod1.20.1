package de.cas_ual_ty.ydm.util;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.properties.YdmProperties;
import de.cas_ual_ty.ydm.rarity.RarityLayer;
import de.cas_ual_ty.ydm.set.CardSet;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.IEventBus;

import javax.annotation.Nullable;

public interface ISidedProxy
{
    default void registerModEventListeners(IEventBus bus)
    {
    }
    
    default void registerForgeEventListeners(IEventBus bus)
    {
    }
    
    default void preInit()
    {
        
    }
    
    default void init()
    {
        
    }
    
    default void initFolders()
    {
        
    }
    
    default void initFiles()
    {
        
    }
    
    @Nullable
    default Player getClientPlayer()
    {
        return null;
    }
    
    default String addCardInfoTag(String imageName)
    {
        return null;
    }
    
    default String addCardItemTag(String imageName)
    {
        return null;
    }
    
    default String addCardMainTag(String imageName)
    {
        return null;
    }
    
    default String addSetInfoTag(String imageName)
    {
        return null;
    }
    
    default String addSetItemTag(String imageName)
    {
        return null;
    }
    
    default String getCardInfoReplacementImage(YdmProperties ydmProperties, byte imageIndex)
    {
        return null;
    }
    
    default String getCardMainReplacementImage(YdmProperties ydmProperties, byte imageIndex)
    {
        return null;
    }
    
    default String getSetInfoReplacementImage(CardSet set)
    {
        return null;
    }
    
    default String getRarityMainImage(RarityLayer layer)
    {
        return null;
    }
    
    default String getRarityInfoImage(RarityLayer layer)
    {
        return null;
    }
    
    default boolean continueTasks()
    {
        return YDM.continueTasks;
    }
    
    default boolean forceTaskStop()
    {
        return YDM.forceTaskStop;
    }
    
    default void openCardInspectScreen(CardHolder card)
    {
    
    }
}
