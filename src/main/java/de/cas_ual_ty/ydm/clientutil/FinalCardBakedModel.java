package de.cas_ual_ty.ydm.clientutil;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.properties.YdmProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("deprecation")
public class FinalCardBakedModel implements BakedModel
{
    private BakedModel mainModel;
    private ItemStack activeItemStack;
    private Function<ResourceLocation, TextureAtlasSprite> textureGetter;
    
    private List<BakedQuad> singleBackList = null;
    private List<BakedQuad> partneredBackList = null;
    private List<BakedQuad> blancList = null;
    
    private HashMap<YdmProperties, List<BakedQuad>> quadsMap;
    
    public FinalCardBakedModel(BakedModel mainModel)
    {
        this.mainModel = mainModel;
        setActiveItemStack(ItemStack.EMPTY);
        textureGetter = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS);
        quadsMap = new HashMap<>(YdmDatabase.PROPERTIES_LIST.size());
    }
    
    public FinalCardBakedModel setActiveItemStack(ItemStack itemStack)
    {
        activeItemStack = itemStack;
        return this;
    }
    
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState pState, @Nullable Direction pDirection, RandomSource pRandom)
    {
        if(ClientProxy.itemsUseCardImagesActive)
        {
            CardHolder card = YdmItems.CARD.get().getCardHolder(activeItemStack);
            
            if(card != null)
            {
                List<BakedQuad> list = new ArrayList<>();
                list.addAll(getPartneredBackList());
                
                if(card.getCard() != null)
                {
                    ResourceLocation front = card.getItemImageResourceLocation();
                    TextureAtlasSprite spriteFront = textureGetter.apply(front);
                    
                    if(!quadsMap.containsKey(card.getCard()))
                    {
                        ModelState modelState = new SimpleModelState(Transformation.identity());
                        List<BlockElement> unbaked = UnbakedGeometryHelper.createUnbakedItemMaskElements(1, spriteFront.contents());
                        List<BakedQuad> baked = UnbakedGeometryHelper.bakeElements(unbaked, m -> spriteFront, modelState, front);
                        
                        List<BakedQuad> textureQuads = new ArrayList<>(0);
                        textureQuads.addAll(baked);
                        
                        quadsMap.put(card.getCard(), textureQuads);
                    }
                    
                    list.addAll(quadsMap.get(card.getCard()));
                }
                else
                {
                    list.addAll(getBlancList());
                }
                
                return list;
            }
        }
        
        return getSingleBackList();
    }
    
    @Override
    public boolean useAmbientOcclusion()
    {
        return mainModel.useAmbientOcclusion();
    }
    
    @Override
    public boolean isGui3d()
    {
        return mainModel.isGui3d();
    }
    
    @Override
    public boolean usesBlockLight()
    {
        return mainModel.usesBlockLight();
    }
    
    @Override
    public boolean isCustomRenderer()
    {
        return mainModel.isCustomRenderer();
    }
    
    @Override
    public TextureAtlasSprite getParticleIcon()
    {
        return mainModel.getParticleIcon();
    }
    
    @Override
    public ItemOverrides getOverrides()
    {
        return mainModel.getOverrides();
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext context, PoseStack mat, boolean applyLeftHandTransform) {
        switch(context) {
            case THIRD_PERSON_LEFT_HAND:
            case THIRD_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
            case FIRST_PERSON_RIGHT_HAND:
                mat.scale(0.5F, 0.5F, 0.5F);
                mat.translate(0, 0.35, 0);
                break;
            case GROUND:
                mat.scale(0.5F, 0.5F, 0.5F);
                break;
            case FIXED:
                mat.mulPose(new Quaternionf().rotationAxis(
                        (float)Math.toRadians(180),
                        new Vector3f(Direction.UP.step())
                ));
                break;
            default:
                break;
        }
        return this;
    }
    
    private List<BakedQuad> getSingleBackList()
    {
        if(singleBackList == null)
        {
            ResourceLocation rl = new ResourceLocation(YDM.MOD_ID, "item/" + YDM.proxy.addCardItemTag("card_back"));
            TextureAtlasSprite sprite = textureGetter.apply(rl);
            singleBackList = new ArrayList<>(0);
            singleBackList.addAll(convertTexture(Transformation.identity(), sprite, 0.5F, Direction.SOUTH, 0xFFFFFFFF, 1, rl));
            singleBackList.addAll(convertTexture(Transformation.identity(), sprite, 0.5F, Direction.NORTH, 0xFFFFFFFF, 1, rl));
        }
        
        return singleBackList;
    }
    
    private List<BakedQuad> getPartneredBackList()
    {
        if(partneredBackList == null)
        {
            ResourceLocation rl = new ResourceLocation(YDM.MOD_ID, "item/" + YDM.proxy.addCardItemTag("card_back"));
            TextureAtlasSprite sprite = textureGetter.apply(rl);
            partneredBackList = convertTexture(Transformation.identity(), sprite, 0.5F, Direction.NORTH, 0xFFFFFFFF, 1, rl);
        }
        
        return partneredBackList;
    }
    
    private List<BakedQuad> getBlancList()
    {
        if(blancList == null)
        {
            ResourceLocation rl = new ResourceLocation(YDM.MOD_ID, "item/" + YDM.proxy.addCardItemTag("blanc_card"));
            TextureAtlasSprite sprite = textureGetter.apply(rl);
            blancList = convertTexture(Transformation.identity(), sprite, 0.5F, Direction.NORTH, 0xFFFFFFFF, 1, rl);
        }
        
        return blancList;
    }

    public static List<BakedQuad> convertTexture(Transformation t, TextureAtlasSprite sprite, float off, Direction direction, int color, int layerIdx, ResourceLocation rl) {
        ModelState modelState = new SimpleModelState(new Transformation(
                new Vector3f(),
                new Quaternionf(),
                new Vector3f(1F, 1F, 1F),
                new Quaternionf()
        ));

        List<BlockElement> unbaked = UnbakedGeometryHelper.createUnbakedItemElements(layerIdx, sprite.contents());
        unbaked.forEach(e -> {
            for(Direction d : Direction.values()) {
                if(d != direction) {
                    e.faces.remove(d);
                }
            }

            float z = (e.from.z() + e.to.z()) * 0.5F;
            e.from.z = z + off * 0.1F;
            e.to.z = z + off * 0.1F;
            // 重建 Vector3f 对象
//            e.from = new Vector3f(e.from.x(), e.from.y(), z + off * 0.1F);
//            e.to = new Vector3f(e.to.x(), e.to.y(), z + off * 0.1F);
        });

        return UnbakedGeometryHelper.bakeElements(unbaked, m -> sprite, modelState, rl);
    }
}
