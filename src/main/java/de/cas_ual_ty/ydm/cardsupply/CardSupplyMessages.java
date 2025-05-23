package de.cas_ual_ty.ydm.cardsupply;

import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.card.properties.YdmProperties;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CardSupplyMessages
{
    public static void doForBinderContainer(Player player, Consumer<CardSupplyContainer> consumer)
    {
        if(player != null && player.containerMenu instanceof CardSupplyContainer)
        {
            consumer.accept((CardSupplyContainer) player.containerMenu);
        }
    }
    
    public static class RequestCard
    {
        public YdmProperties card;
        public byte imageIndex;
        
        public RequestCard(YdmProperties card, byte imageIndex)
        {
            this.card = card;
            this.imageIndex = imageIndex;
        }
        
        public static void encode(RequestCard msg, FriendlyByteBuf buf)
        {
            buf.writeLong(msg.card.getId());
            buf.writeByte(msg.imageIndex);
        }
        
        public static RequestCard decode(FriendlyByteBuf buf)
        {
            return new RequestCard(YdmDatabase.PROPERTIES_LIST.get(buf.readLong()), buf.readByte());
        }
        
        public static void handle(RequestCard msg, Supplier<NetworkEvent.Context> ctx)
        {
            NetworkEvent.Context context = ctx.get();
            
            if(msg.card != null && msg.card != YdmProperties.DUMMY)
            {
                context.enqueueWork(() ->
                {
                    CardSupplyMessages.doForBinderContainer(context.getSender(), (container) ->
                    {
                        container.giveCard(msg.card, msg.imageIndex);
                    });
                });
            }
            
            context.setPacketHandled(true);
        }
    }
}
