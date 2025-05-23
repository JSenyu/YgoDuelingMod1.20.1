package de.cas_ual_ty.ydm.clientutil;

import com.google.common.base.CharMatcher;
import com.google.gson.JsonObject;
import de.cas_ual_ty.ydm.YDM;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.io.InputStream;
import java.util.Set;

public class YdmCardResourcePack extends FilePackResources
{
    public static final String PATH_PREFIX = "assets/" + YDM.MOD_ID + "/textures/item/";
    
    private static final boolean OS_WINDOWS = Util.getPlatform() == Util.OS.WINDOWS;
    private static final CharMatcher BACKSLASH_MATCHER = CharMatcher.is('\\');

    private final File resourceRoot; // 新增字段
    private JsonObject packMeta;

    public YdmCardResourcePack() {
        super(
                "YDM Images",     // packName
                ClientProxy.cardImagesFolder, // file
                false             // isBuiltin
        );
        this.resourceRoot = ClientProxy.cardImagesFolder;
        this.packMeta = createPackMetadata();
    }

    private static JsonObject createPackMetadata() {
        JsonObject pack = new JsonObject();
        pack.addProperty("description", "YDM Dynamic Images");
        pack.addProperty("pack_format", 15); // 1.20.1使用格式15
        JsonObject root = new JsonObject();
        root.add("pack", pack);
        return root;
    }

    public static String convertPath(String s)
    {
        if(YdmCardResourcePack.OS_WINDOWS)
        {
            s = YdmCardResourcePack.BACKSLASH_MATCHER.replaceFrom(s, '/');
        }
        
        return s;
    }

    @Override
    public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
        if(type != PackType.CLIENT_RESOURCES) return null;

        String path = location.getPath();
        File targetFile = resolveFile(path);

        return targetFile != null && targetFile.exists() ?
                IoSupplier.create(targetFile.toPath()) :
                null;
    }

    private File resolveFile(String resourcePath) {
        // 保持原有路径转换逻辑
        String convertedPath = convertPath(resourcePath);
        if(!convertedPath.endsWith(".png")) return null;

        // 示例路径转换逻辑
        String relativePath = convertedPath.replace("assets/ydm/textures/item/", "");
        return new File(resourceRoot, relativePath);
    }

    @Override
    public void listResources(@NotNull PackType type, @NotNull String namespace, @NotNull String path, @NotNull ResourceOutput output) {
        if(type != PackType.CLIENT_RESOURCES || !namespace.equals(YDM.MOD_ID)) return;

        File targetDir = new File(resourceRoot, path.replace('.', '/'));
        if(!targetDir.isDirectory()) return;

        for(File file : targetDir.listFiles((dir, name) -> name.endsWith(".png"))) {
            String relativePath = path + "/" + file.getName().replace(".png", "");
            output.accept(
                    new ResourceLocation(YDM.MOD_ID, relativePath),
                    IoSupplier.create(file.toPath())
            );
        }
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        return type == PackType.CLIENT_RESOURCES ?
                Set.of(YDM.MOD_ID) :
                Set.of();
    }

    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) {
        return deserializer.getMetadataSectionName().equals("pack") ?
                deserializer.fromJson(packMeta.getAsJsonObject("pack")) :
                null;
    }
    
//    @Override
//    protected InputStream getResource(String resourcePath) throws IOException
//    {
//        //TODO pack.png needs to be returned as well
//
//        // We get system dependent resource paths here (so eg. \ for windows, / for mac) so we need to convert
//        File image = getFile(YdmCardResourcePack.convertPath(resourcePath));
//
//        if(image == null)
//        {
//            throw new ResourcePackFileNotFoundException(ClientProxy.cardImagesFolder, resourcePath);
//        }
//        else
//        {
//            return new FileInputStream(image);
//        }
//    }
    
//    @Override
//    public boolean hasResource(String resourcePath)
//    {
//        return getFile(resourcePath) != null;
//    }
    
    @Nullable
    private File getFile(String filename)
    {
        if(!filename.endsWith(".png"))
        {
            return null;
        }
        
        // We only look for assets with this path as prefix (so eg. no models)
        if(!filename.startsWith(YdmCardResourcePack.PATH_PREFIX))
        {
            return null;
        }
        
        // We remove that prefix part
        filename = filename.substring(YdmCardResourcePack.PATH_PREFIX.length());
        
        // Get the file
        File image = ImageHandler.getCardFile(filename);
        
        if(image.exists())
        {
            return image;
        }
        else
        {
            image = ImageHandler.getSetFile(filename);
            
            if(image.exists())
            {
                return image;
            }
            else
            {
                image = ImageHandler.getRarityFile(filename);
                
                if(image.exists())
                {
                    return image;
                }
                else
                {
                    return null;
                }
            }
        }
    }

    
    @Override
    public void close()
    {
    }
    
//    @Override
//    public Collection<ResourceLocation> getResources(PackType type, String namespaceIn, String pathIn, Predicate<ResourceLocation> filterIn)
//    {
//        // This is only needed for fonts and sounds afaik
//        /*
//        List<ResourceLocation> list = Lists.newArrayList();
//
//        if(type == ResourcePackType.CLIENT_RESOURCES)
//        {
//            File[] listFiles = ClientProxy.cardImagesFolder.listFiles(this.filter);
//
//            if(listFiles != null)
//            {
//                for(File f : listFiles)
//                {
//                    list.add(new ResourceLocation(YDM.MOD_ID, f.getName().replace(this.filter.getRequiredSuffix(), "")));
//                }
//            }
//        }
//        */
//        return Collections.emptyList();
//    }
    
//    @Override
//    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) throws IOException
//    {
//        if(deserializer.getMetadataSectionName().equals("pack"))
//        {
//            return deserializer.fromJson(GsonHelper.getAsJsonObject(packMeta, deserializer.getMetadataSectionName()));
//        }
//        return null;
//    }
    
//    @Override
//    public String getName()
//    {
//        return "YDM Images";
//    }
}
