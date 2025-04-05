package psqq_item.psqq_item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import psqq_item.psqq_item.recipe.MotorGeneratorsRecipe;
import psqq_item.psqq_item.screen.ModMenuTypes;
import psqq_item.psqq_item.screen.MotorGenerators.MotorGeneratorsScreen;
import net.minecraft.client.gui.screens.MenuScreens;

@Mod(ModMain.MOD_ID)
public class ModMain {
    // 定义MOD_ID常量，这个常量将被其他类引用
    public static final String MOD_ID = "psqq_item";

    // 注册配方序列化器
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID);

    // 添加配方序列化器注册
    static {
        RECIPE_SERIALIZERS.register("motor_generators",
                () -> MotorGeneratorsRecipe.Serializer.INSTANCE);
    }

    // Mod构造函数
    public ModMain() {
        // 获取事件总线
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册物品和创造模式物品栏
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus); // 添加方块注册
        ModBlockEntities.register(modEventBus); // 添加方块实体注册
        ModMenuTypes.register(modEventBus); // 添加菜单类型注册
        ModCreativeTabs.register(modEventBus);

        // 注册通用设置事件，用于初始化网络
        modEventBus.addListener(this::clientSetup);

        // 注册配方序列化器
        RECIPE_SERIALIZERS.register(modEventBus);

        // 注册配方类型事件
        modEventBus.addListener(this::registerRecipeTypes);

        // 注册到Forge事件总线
        MinecraftForge.EVENT_BUS.register(this);
    }

    // 注册配方类型
    private void registerRecipeTypes(RegisterEvent event) {
        event.register(BuiltInRegistries.RECIPE_TYPE.key(), helper -> {
            helper.register(new ResourceLocation(MOD_ID, "motor_generators"),
                    MotorGeneratorsRecipe.Type.INSTANCE);
        });
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.MOTOR_GENERATORS_MENU.get(), MotorGeneratorsScreen::new);
        });
    }
}