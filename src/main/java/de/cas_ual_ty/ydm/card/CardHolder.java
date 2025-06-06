package de.cas_ual_ty.ydm.card;

import com.google.gson.JsonObject;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.card.properties.YdmProperties;
import de.cas_ual_ty.ydm.rarity.Rarities;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Objects;

public class CardHolder implements Comparable<CardHolder>
{
    public static final CardHolder DUMMY = new CardHolder(YdmProperties.DUMMY, (byte) 0, Rarities.CREATIVE.name, "DUM-MY");
    public YdmProperties card;
    public byte imageIndex;
    public String rarity;
    public String code;
    
    public CardHolder(YdmProperties card, byte imageIndex, String rarity, String code)
    {
        this.card = card;
        this.imageIndex = imageIndex;
        this.rarity = rarity;
        this.code = code;
    }
    
    public CardHolder(YdmProperties card, byte imageIndex, String rarity)
    {
        this(card, imageIndex, rarity, card.getId() + "_" + imageIndex);
    }
    
    protected CardHolder()
    {
        this(null, (byte) 0, "", "");
    }
    
    public CardHolder(CompoundTag nbt)
    {
        this();
        readCardHolderFromNBT(nbt);
    }
    
    public CardHolder(JsonObject json)
    {
        this();
        readFromJson(json);
    }
    
    public void addInformation(List<Component> tooltip)
    {
        tooltip.add(Component.literal(getCard().getName()));
        tooltip.add(Component.literal(getCode()));
        tooltip.add(Component.literal(getRarity()));
        tooltip.add(Component.literal("Image Variant " + (1 + getImageIndex())));
    }
    
    public String getImageName()
    {
        return getCard().getImageName(getImageIndex());
    }
    
    public String getInfoImageName()
    {
        return getCard().getInfoImageName(getImageIndex());
    }
    
    public String getItemImageName()
    {
        return getCard().getItemImageName(getImageIndex());
    }
    
    public String getMainImageName()
    {
        return getCard().getMainImageName(getImageIndex());
    }
    
    public String getImageURL()
    {
        return getCard().getImageURL(getImageIndex());
    }
    
    public ResourceLocation getInfoImageResourceLocation()
    {
        return getCard().getInfoImageResourceLocation(getImageIndex());
    }
    
    public ResourceLocation getItemImageResourceLocation()
    {
        return getCard().getItemImageResourceLocation(getImageIndex());
    }
    
    public ResourceLocation getMainImageResourceLocation()
    {
        return getCard().getMainImageResourceLocation(getImageIndex());
    }
    
    public void override(CardHolder cardHolder)
    {
        card = cardHolder.card;
        imageIndex = cardHolder.imageIndex;
        rarity = cardHolder.rarity;
        code = cardHolder.code;
    }
    
    public YdmProperties getCard()
    {
        return card;
    }
    
    public void setCard(YdmProperties card)
    {
        this.card = card;
    }
    
    public void setImageIndex(byte imageIndex)
    {
        this.imageIndex = imageIndex;
    }
    
    public byte getImageIndex()
    {
        return imageIndex;
    }
    
    public void setRarity(String rarity)
    {
        this.rarity = rarity;
    }
    
    public String getRarity()
    {
        return rarity;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public void readCardHolderFromNBT(CompoundTag nbt)
    {
        card = YdmDatabase.PROPERTIES_LIST.get(nbt.getLong(JsonKeys.ID));
        
        if(card == null)
        {
            card = YdmProperties.DUMMY;
        }
        
        imageIndex = nbt.getByte(JsonKeys.IMAGE_INDEX);
        rarity = nbt.getString(JsonKeys.RARITY);
        code = nbt.getString(JsonKeys.CODE);
    }
    
    public void writeCardHolderToNBT(CompoundTag nbt)
    {
        if(card != YdmProperties.DUMMY)
        {
            nbt.putLong(JsonKeys.ID, card.getId());
        }
        
        nbt.putByte(JsonKeys.IMAGE_INDEX, imageIndex);
        nbt.putString(JsonKeys.RARITY, rarity);
        nbt.putString(JsonKeys.CODE, code);
    }
    
    public void readFromJson(JsonObject json)
    {
        card = YdmDatabase.PROPERTIES_LIST.get(json.get(JsonKeys.ID).getAsLong());
        
        if(card == null)
        {
            card = YdmProperties.DUMMY;
        }
        
        imageIndex = json.get(JsonKeys.IMAGE_INDEX).getAsByte();
        rarity = json.get(JsonKeys.RARITY).getAsString();
        code = json.get(JsonKeys.CODE).getAsString();
    }
    
    public void writeToJson(JsonObject json)
    {
        if(card != YdmProperties.DUMMY)
        {
            json.addProperty(JsonKeys.ID, card.getId());
        }
        
        json.addProperty(JsonKeys.IMAGE_INDEX, imageIndex);
        json.addProperty(JsonKeys.RARITY, rarity);
        json.addProperty(JsonKeys.CODE, code);
    }
    
    @Override
    public String toString()
    {
        return getCard().getName() + " (" + getCode() + ")";
    }
    
    @Override
    public int compareTo(CardHolder o)
    {
        int c = code.compareTo(o.code);
        
        if(c != 0)
        {
            return c;
        }
        
        c = rarity.compareTo(o.rarity);
        
        if(c != 0)
        {
            return c;
        }
        
        return Byte.compare(imageIndex, o.imageIndex);
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof CardHolder))
        {
            return false;
        }
        
        CardHolder holder = (CardHolder) obj;
        
        return card == holder.card && imageIndex == holder.imageIndex && Objects.equals(rarity, holder.rarity);
    }
}
