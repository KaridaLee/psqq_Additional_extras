package psqq_item.psqq_item;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import psqq_item.psqq_item.block.entity.InfiniteSinkBlockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, psqq_item.MOD_ID);

    public static final RegistryObject<BlockEntityType<InfiniteSinkBlockEntity>> INFINITE_SINK =
            BLOCK_ENTITIES.register("infinite_sink",
                    () -> BlockEntityType.Builder.of(InfiniteSinkBlockEntity::new,
                            ModBlocks.INFINITE_SINK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
