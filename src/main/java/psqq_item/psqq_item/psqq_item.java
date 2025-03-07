package psqq_item.psqq_item;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(psqq_item.MOD_ID)
public class psqq_item {
    // 定义MOD_ID常量，这个常量将被其他类引用
    public static final String MOD_ID = "psqq_item";

    // Mod构造函数
    public psqq_item() {
        // 获取事件总线
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册物品和创造模式物品栏
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        // 注册通用设置事件，用于初始化网络
        modEventBus.addListener(this::commonSetup);

        // 注册到Forge事件总线
        MinecraftForge.EVENT_BUS.register(this);
    }

    // 通用设置方法，在客户端和服务端都会执行
    private void commonSetup(final FMLCommonSetupEvent event) {
        // 初始化网络
        event.enqueueWork(ModNetwork::init);
    }
}