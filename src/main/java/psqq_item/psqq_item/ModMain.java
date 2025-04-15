package psqq_item.psqq_item;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
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

    // 注册配方类型
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MOD_ID);

    // 添加配方序列化器注册
    public static final RegistryObject<RecipeSerializer<MotorGeneratorsRecipe>> MOTOR_GENERATORS_SERIALIZER =
            RECIPE_SERIALIZERS.register("motor_generators",
                    () -> MotorGeneratorsRecipe.Serializer.INSTANCE);

    // 添加配方类型注册
    public static final RegistryObject<RecipeType<MotorGeneratorsRecipe>> MOTOR_GENERATORS_TYPE =
            RECIPE_TYPES.register("motor_generators",
                    () -> MotorGeneratorsRecipe.Type.INSTANCE);

    // Mod构造函数
    public ModMain() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册物品和创造模式物品栏
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        // 注册配方类型和序列化器
        RECIPE_TYPES.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);

        // 注册通用设置事件
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        // 注册到Forge事件总线
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.MOTOR_GENERATORS_MENU.get(), MotorGeneratorsScreen::new);
        });
    }
}