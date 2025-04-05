package psqq_item.psqq_item;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import psqq_item.psqq_item.block.entity.InfiniteSinkBlockEntity;
import psqq_item.psqq_item.block.entity.MotorGeneratorsBlockEntity; // 添加这个导入

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ModMain.MOD_ID);

    public static final RegistryObject<BlockEntityType<InfiniteSinkBlockEntity>> INFINITE_SINK =
            BLOCK_ENTITIES.register("infinite_sink",
                    () -> BlockEntityType.Builder.of(InfiniteSinkBlockEntity::new,
                            ModBlocks.INFINITE_SINK.get()).build(null));

    // 添加 motor_generators 方块实体
    public static final RegistryObject<BlockEntityType<MotorGeneratorsBlockEntity>> MOTOR_GENERATORS =
            BLOCK_ENTITIES.register("motor_generators",
                    () -> BlockEntityType.Builder.of(MotorGeneratorsBlockEntity::new,
                            ModBlocks.MOTOR_GENERATORS.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
