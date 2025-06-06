package de.cas_ual_ty.ydm.card.properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.ClientResouresLocationGet;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class YdmProperties {

    public static final YdmProperties DUMMY = new YdmProperties()
    {
        @Override
        public String getImageName(byte imageIndex)
        {
            return "blanc_card";
        }
        
        @Override
        public void addCardType(List<Component> list)
        {
            
        }
    };
    
    static
    {
        YdmProperties.DUMMY.isHardcoded = true;
        YdmProperties.DUMMY.name = "Dummy";
        YdmProperties.DUMMY.id = 0;
        YdmProperties.DUMMY.isIllegal = false;
        YdmProperties.DUMMY.isCustom = true;
        YdmProperties.DUMMY.text = "This is a replacement card!";
        YdmProperties.DUMMY.type = null;
        YdmProperties.DUMMY.images = null;
    }
    
    public boolean isHardcoded;
    public String name;
    public long id;
    public boolean isIllegal;
    public boolean isCustom;
    public String text;
    public Type type;
    public String[] images;
    
    protected int imageIndicesAmt;
    
    public YdmProperties(YdmProperties p0)
    {
        isHardcoded = false;
        name = p0.name;
        id = p0.id;
        isIllegal = p0.isIllegal;
        isCustom = p0.isCustom;
        text = p0.text;
        type = p0.type;
        images = p0.images;
        imageIndicesAmt = images.length;
    }
    
    public YdmProperties(JsonObject j)
    {
        isHardcoded = false;
        readAllProperties(j);
        imageIndicesAmt = 1;
    }
    
    public YdmProperties()
    {
        isHardcoded = false;
        imageIndicesAmt = 1;
    }
    
    public void postDBInit()
    {
        
    }
    
    public void readAllProperties(JsonObject j)
    {
        readProperties(j);
    }
    
    public void writeAllProperties(JsonObject j)
    {
        writeProperties(j);
    }
    
    public void readProperties(JsonObject j)
    {
        name = j.get(JsonKeys.NAME).getAsString();
        id = j.get(JsonKeys.ID).getAsLong();
        isIllegal = j.get(JsonKeys.IS_ILLEGAL).getAsBoolean();
        isCustom = j.get(JsonKeys.IS_CUSTOM).getAsBoolean();
        text = j.get(JsonKeys.TEXT).getAsString();
        type = Type.fromString(j.get(JsonKeys.TYPE).getAsString());
        
        JsonArray images = j.get(JsonKeys.IMAGES).getAsJsonArray();
        this.images = new String[images.size()];
        for(int i = 0; i < this.images.length; ++i)
        {
            this.images[i] = images.get(i).getAsString();
        }
    }
    
    public void writeProperties(JsonObject j)
    {
        j.addProperty(JsonKeys.NAME, name);
        j.addProperty(JsonKeys.ID, id);
        j.addProperty(JsonKeys.IS_ILLEGAL, isIllegal);
        j.addProperty(JsonKeys.IS_CUSTOM, isCustom);
        j.addProperty(JsonKeys.TEXT, text);
        j.addProperty(JsonKeys.TYPE, type.name);
        
        JsonArray images = new JsonArray();
        for(String image : this.images)
        {
            images.add(image);
        }
        j.add(JsonKeys.IMAGES, images);
    }
    
    public boolean getIsHardcoded()
    {
        return isHardcoded;
    }
    
    public boolean getIsSpell()
    {
        return getType() == Type.SPELL;
    }
    
    public boolean getIsTrap()
    {
        return getType() == Type.TRAP;
    }
    
    public boolean getIsMonster()
    {
        return getType() == Type.MONSTER;
    }
    
    public boolean getIsInExtraDeck()
    {
        return false;
    }
    
    public int getImageIndicesAmt()
    {
        return imageIndicesAmt;
    }
    
    public boolean isAcceptedImageIndex(byte imageIndex)
    {
        return imageIndex >= 0 && imageIndex < getImageIndicesAmt();
    }
    
    public byte adjustImageIndex(byte imageIndex)
    {
        if(!isAcceptedImageIndex(imageIndex))
        {
            return 0;
        }
        else
        {
            return imageIndex;
        }
    }
    
    public String getImageURL(byte imageIndex)
    {
        return getImages()[adjustImageIndex(imageIndex)];
    }
    
    public String getImageName(byte imageIndex)
    {
        return getId() + "_" + adjustImageIndex(imageIndex);
    }
    
    public String getInfoImageName(byte imageIndex)
    {
        return YDM.proxy.addCardInfoTag(getImageName(imageIndex));
    }
    
    public String getItemImageName(byte imageIndex)
    {
        return YDM.proxy.addCardItemTag(getImageName(imageIndex));
    }
    
    public String getMainImageName(byte imageIndex)
    {
        return YDM.proxy.addCardMainTag(getImageName(imageIndex));
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getInfoImageResourceLocation(byte imageIndex) {
        return ClientResouresLocationGet.getClientResouresLocation("cards", YDM.proxy.getCardInfoReplacementImage(this, adjustImageIndex(imageIndex)));

//        try {
//            Path path = Path.of("ydm_db_images/cards/" + YDM.proxy.getCardInfoReplacementImage(this, adjustImageIndex(imageIndex)) + ".png");
//            NativeImage image = NativeImage.read(Files.newInputStream(path));
//            DynamicTexture dynTex = new DynamicTexture(image);
//            ResourceLocation resourceLocation = new ResourceLocation(YDM.MOD_ID, "textures/item/" + YDM.proxy.getCardInfoReplacementImage(this, adjustImageIndex(imageIndex)) + ".png");
//            Minecraft.getInstance().getTextureManager().register(resourceLocation, dynTex);
//            return resourceLocation;
//
//        } catch (IOException e) {
//            return new ResourceLocation(YDM.MOD_ID, "textures/item/" + YDM.proxy.getCardInfoReplacementImage(this, adjustImageIndex(imageIndex)) + ".png");
//        }
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getItemImageResourceLocation(byte imageIndex) {
        return ClientResouresLocationGet.getClientResouresLocation("cards", getItemImageName(imageIndex));

//        try {
//            Path path = Path.of("ydm_db_images/cards/" + getItemImageName(imageIndex) + ".png");
//            NativeImage image = NativeImage.read(Files.newInputStream(path));
//            DynamicTexture dynTex = new DynamicTexture(image);
//            ResourceLocation resourceLocation = new ResourceLocation(YDM.MOD_ID, "textures/item/" + getItemImageName(imageIndex) + ".png");
//            Minecraft.getInstance().getTextureManager().register(resourceLocation, dynTex);
//            return resourceLocation;
//
//        } catch (IOException e) {
//            return new ResourceLocation(YDM.MOD_ID, "textures/item/" + getItemImageName(imageIndex) + ".png");
//        }
//        return new ResourceLocation(YDM.MOD_ID, "item/" + getItemImageName(imageIndex));
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getMainImageResourceLocation(byte imageIndex) {
        return ClientResouresLocationGet.getClientResouresLocation("cards", YDM.proxy.getCardMainReplacementImage(this, adjustImageIndex(imageIndex)));

//        try {
//            Path path = Path.of("ydm_db_images/cards/" + YDM.proxy.getCardMainReplacementImage(this, adjustImageIndex(imageIndex)) + ".png");
//            NativeImage image = NativeImage.read(Files.newInputStream(path));
//            DynamicTexture dynTex = new DynamicTexture(image);
//            ResourceLocation resourceLocation = new ResourceLocation(YDM.MOD_ID, "textures/item/" + YDM.proxy.getCardMainReplacementImage(this, adjustImageIndex(imageIndex)) + ".png");
//            Minecraft.getInstance().getTextureManager().register(resourceLocation, dynTex);
//            return resourceLocation;
//
//        } catch (IOException e) {
//            return new ResourceLocation(YDM.MOD_ID, "textures/item/" + YDM.proxy.getCardMainReplacementImage(this, adjustImageIndex(imageIndex)) + ".png");
//        }
//        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + YDM.proxy.getCardMainReplacementImage(this, adjustImageIndex(imageIndex)) + ".png");
    }
    
    public void addInformation(List<Component> list)
    {
        addHeader(list);
        list.add(Component.empty());
        addText(list);
    }
    
    public void addHeader(List<Component> list)
    {
        list.add(Component.literal(getName()));
        
        if(isCustom)
        {
            list.add(Component.literal("Custom Card").setStyle(Style.EMPTY.applyFormat(ChatFormatting.RED)));
        }
        
        list.add(Component.empty());
        addCardType(list);
    }
    
    public void addText(List<Component> list)
    {
        list.add(Component.literal(getText()));
    }
    
    public void addCardType(List<Component> list)
    {
        list.add(Component.literal(type.name));
    }
    
    // --- Getters ---
    
    public String getName()
    {
        return name;
    }
    
    public long getId()
    {
        return id;
    }
    
    public boolean getIllegal()
    {
        return isIllegal;
    }
    
    public boolean getCustom()
    {
        return isCustom;
    }
    
    public String getText()
    {
        return text;
    }
    
    public Type getType()
    {
        return type;
    }
    
    public String[] getImages()
    {
        return images;
    }
}
