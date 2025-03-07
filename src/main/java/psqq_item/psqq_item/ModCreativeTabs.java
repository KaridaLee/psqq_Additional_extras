package psqq_item.psqq_item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, psqq_item.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MOD_TAB = CREATIVE_MODE_TABS.register("mod_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.psqq_item"))
                    .icon(() -> new ItemStack(ModItems.OVERLOAD_ELECTRON_TUBE.get()))
                    .displayItems((parameters, output) -> {
                        // 添加您的物品到物品栏
                        output.accept(ModItems.OVERLOAD_ELECTRON_TUBE.get());
                        output.accept(ModItems.OVERLOAD_PRECISION_MECHANISM.get());
                        output.accept(ModItems.UNKNOWN_STONE.get());
                        output.accept(ModItems.CHARGED_UNKNOWN_STONE.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
