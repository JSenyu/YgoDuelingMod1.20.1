package de.cas_ual_ty.ydm.set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.properties.YdmProperties;
import de.cas_ual_ty.ydm.clientutil.ClientResouresLocationGet;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.SortedArraySet;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.text.ParseException;
import java.util.*;

public class CardSet
{
    public static final CardSet DUMMY = new CardSet("Dummy", "DUM-MY", "DUMMY", new Date(0), new CardPuller(null, null)
    {
        @Override
        public List<ItemStack> open(Random random)
        {
            return ImmutableList.of();
        }
        
        @Override
        public boolean addInformationInComposition()
        {
            return false;
        }
        
        @Override
        public void addAllCardEntries(SortedArraySet<CardHolder> sortedSet)
        {
            
        }
    }, ImmutableList.of())
    {
        @Override
        public String getImageName()
        {
            return "blanc_set";
        }
        
        @Override
        public boolean getIsHardcoded()
        {
            return true;
        }
    };
    
    public String name;
    public String code;
    public String type;
    public Date date;
    public String image;
    public CardPuller pull;
    
    // must be same size and order as cards JsonArray
    public List<CardHolder> cards;
    
    // list of all contained rarities
    public Set<String> rarityPool;
    
    public boolean isSubSet;
    public String shownCode;
    
    public CardSet(String name, String code, String type, Date date, CardPuller pull, List<CardHolder> cards)
    {
        this.name = name;
        this.code = code;
        this.type = type;
        this.date = date;
        this.pull = pull;
        this.cards = cards;
        
        init();
    }
    
    public CardSet(JsonObject j) throws IllegalArgumentException
    {
        if(j.has(JsonKeys.NAME))
        {
            name = j.get(JsonKeys.NAME).getAsString();
        }
        else
        {
            name = null;
        }
        
        code = j.get(JsonKeys.CODE).getAsString();
        type = j.get(JsonKeys.TYPE).getAsString();
        
        if(!j.has(JsonKeys.DATE))
        {
            date = null;
        }
        else
        {
            String date = j.get(JsonKeys.DATE).getAsString();
            try
            {
                this.date = YdmDatabase.SET_DATE_PARSER.parse(date);
            }
            catch(ParseException e)
            {
                YDM.log("Can not parse date: " + date);
                throw new IllegalArgumentException(e);
            }
        }
        
        if(!j.has(JsonKeys.IMAGE))
        {
            image = null;
        }
        else
        {
            image = j.get(JsonKeys.IMAGE).getAsString();
        }
        
        pull = PullType.createPull(j.get(JsonKeys.PULL_TYPE).getAsString(), j, this);
        
        if(!j.has(JsonKeys.CARDS))
        {
            cards = ImmutableList.of();
            rarityPool = ImmutableSet.of();
        }
        else
        {
            JsonArray cards = j.get(JsonKeys.CARDS).getAsJsonArray();
            this.cards = new ArrayList<>(cards.size());
            rarityPool = new HashSet<>();
            
            JsonObject c;
            long id;
            YdmProperties card;
            byte imageIndex;
            String rarity;
            for(JsonElement e : cards)
            {
                c = e.getAsJsonObject();
                
                id = c.get(JsonKeys.ID).getAsLong();
                card = YdmDatabase.PROPERTIES_LIST.get(id);
                
                if(card == null)
                {
                    YDM.log("Can not parse card in: " + code + " card: " + id);
                    continue;
                }
                
                imageIndex = c.get(JsonKeys.IMAGE_INDEX).getAsByte();
                
                if(imageIndex >= card.getImageIndicesAmt())
                {
                    YDM.log("Bad image index for card in: " + code + " card: " + card);
                }
                
                rarity = c.get(JsonKeys.RARITY).getAsString();
                rarityPool.add(rarity);
                
                this.cards.add(new CardHolder(card, imageIndex, rarity, c.get(JsonKeys.CODE).getAsString()));
            }
        }
        
        init();
    }
    
    protected void init()
    {
        isSubSet = type.equals("Sub-Set");
        shownCode = code.split("_")[0];
    }
    
    public void postDBInit()
    {
        pull.postDBInit();
    }
    
    public List<ItemStack> open(Random random)
    {
        return pull.open(random);
    }
    
    public SortedArraySet<CardHolder> getAllCardEntries()
    {
        SortedArraySet<CardHolder> sortedSet = SortedArraySet.create(0);
        addAllCardEntries(sortedSet);
        return sortedSet;
    }
    
    public void addAllCardEntries(SortedArraySet<CardHolder> sortedSet)
    {
        pull.addAllCardEntries(sortedSet);
    }
    
    public void addItemInformation(List<Component> tooltip)
    {
        tooltip.add(Component.literal(name));
        tooltip.add(Component.literal(type));
        tooltip.add(Component.literal(shownCode));
    }
    
    public void addInformation(List<Component> tooltip)
    {
        tooltip.add(Component.literal(name));
        tooltip.add(Component.literal(type));
        tooltip.add(Component.literal(YdmDatabase.SET_DATE_PARSER.format(date)));
        tooltip.add(Component.literal(shownCode));
        tooltip.add(Component.empty());
        pull.addInformation(tooltip);
    }
    
    public boolean isIndependentAndItem()
    {
        return this != CardSet.DUMMY && !isSubSet && name != null && date != null;
    }
    
    public String getImageName()
    {
        return code.toLowerCase();
    }
    
    public String getImageURL()
    {
        return image;
    }
    
    public String getInfoImageName()
    {
        return YDM.proxy.addSetInfoTag(getImageName());
    }
    
    public String getItemImageName()
    {
        return YDM.proxy.addSetItemTag(getImageName());
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getInfoImageResourceLocation() {
        return ClientResouresLocationGet.getClientResouresLocation("sets", YDM.proxy.getSetInfoReplacementImage(this));

//        try {
//            Path path = Path.of("ydm_db_images/sets/" + YDM.proxy.getSetInfoReplacementImage(this) + ".png");
//            NativeImage image = NativeImage.read(Files.newInputStream(path));
//            DynamicTexture dynTex = new DynamicTexture(image);
//            ResourceLocation resourceLocation = new ResourceLocation(YDM.MOD_ID, "textures/item/" + YDM.proxy.getSetInfoReplacementImage(this) + ".png");
//            Minecraft.getInstance().getTextureManager().register(resourceLocation, dynTex);
//            return resourceLocation;
//        } catch (IOException e) {
//            return new ResourceLocation(YDM.MOD_ID, "textures/item/" + YDM.proxy.getSetInfoReplacementImage(this) + ".png");
//        }
////        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + YDM.proxy.getSetInfoReplacementImage(this) + ".png");
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getItemImageResourceLocation() {
        return ClientResouresLocationGet.getClientResouresLocation("sets", getItemImageName());

//        try {
//            Path path = Path.of("ydm_db_images/sets/" + getItemImageName() + ".png");
//            NativeImage image = NativeImage.read(Files.newInputStream(path));
//            DynamicTexture dynTex = new DynamicTexture(image);
//            ResourceLocation resourceLocation = new ResourceLocation(YDM.MOD_ID, "textures/item/" + getItemImageName() + ".png");
//            Minecraft.getInstance().getTextureManager().register(resourceLocation, dynTex);
//            return resourceLocation;
//
//        } catch (IOException e) {
//            return new ResourceLocation(YDM.MOD_ID, "textures/item/" + getItemImageName() + ".png");
//        }
//        return new ResourceLocation(YDM.MOD_ID, "item/" + getItemImageName());
    }
    
    public boolean getIsHardcoded()
    {
        return false;
    }
}
