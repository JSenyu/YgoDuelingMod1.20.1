package de.cas_ual_ty.ydm.clientutil;

import com.mojang.blaze3d.platform.NativeImage;
import de.cas_ual_ty.ydm.YDM;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@OnlyIn(Dist.CLIENT)
public class ClientResouresLocationGet {


    @OnlyIn(Dist.CLIENT)
    public static ResourceLocation getClientResouresLocation(String type , String fileName) {
        try {
            Path path = Path.of("ydm_db_images/" + type+ "/" + fileName + ".png");
            NativeImage image = NativeImage.read(Files.newInputStream(path));
            DynamicTexture dynTex = new DynamicTexture(image);
            ResourceLocation resourceLocation = new ResourceLocation(YDM.MOD_ID, "textures/item/" + fileName + ".png");
            Minecraft.getInstance().getTextureManager().register(resourceLocation, dynTex);
            return resourceLocation;

        } catch (IOException e) {
            return new ResourceLocation(YDM.MOD_ID, "textures/item/" + fileName + ".png");
        }

    }


}
