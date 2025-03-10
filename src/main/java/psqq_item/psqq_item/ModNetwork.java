package psqq_item.psqq_item;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    // 硬编码版本号
    private static final String PROTOCOL_VERSION = "1.0.2";

    // 创建网络通道
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(psqq_item.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,  // 客户端版本检查
            PROTOCOL_VERSION::equals   // 服务端版本检查
    );

    // 初始化网络
    public static void init() {
        // 此处可以注册网络消息，如果将来需要的话
    }
}
