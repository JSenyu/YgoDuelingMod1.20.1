package de.cas_ual_ty.ydm.clientutil;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class YdmResourcePackFinder implements RepositorySource {
    @Override
    public void loadPacks(@NotNull Consumer<Pack> infoConsumer) {
        // 利用 readMetaAndCreate 自动读取 metadata 并创建 Pack
        @Nullable Pack pack = Pack.readMetaAndCreate(
                YDM.MOD_ID,                                      // pack ID
                Component.literal(YDM.MOD_ID),                   // 包显示标题
                true,                                            // 是否始终启用
                (namespace) -> new YdmCardResourcePack(),                              // 资源提供者
                PackType.CLIENT_RESOURCES,                       // 资源包类型
                Pack.Position.TOP,                               // 插入位置：最前面
                PackSource.DEFAULT                               // 来源：默认
        );
        if (pack != null) {
            infoConsumer.accept(pack);
        } else {
            YDM.log("Failed to create resource pack for mod '" + YDM.MOD_ID + "'");
        }
    }

}
