package psqq_item.psqqitem;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    // 使用ExampleMod类中定义的MOD_ID
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, psqq_item.MOD_ID);

    // 注册物品
    public static final RegistryObject<Item> OVERLOAD_ELECTRON_TUBE = ITEMS.register("overload_electron_tube",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> OVERLOAD_PRECISION_MECHANISM = ITEMS.register("overload_precision_mechanism",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> UNKNOWN_STONE = ITEMS.register("unknown_stone",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> CHARGED_UNKNOWN_STONE = ITEMS.register("charged_unknown_stone",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> INCOMPLETE_OVERLOAD_PRECISION_MECHANISM = ITEMS.register("incomplete_overload_precision_mechanism",
            () -> new Item(new Item.Properties()));

    // 注册方法
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}