package psqq_item.psqq_item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import psqq_item.psqq_item.block.InfiniteSinkBlock;
import psqq_item.psqq_item.block.MotorGeneratorsBlock;
import psqq_item.psqq_item.block.PolymericAlloyBlock;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;

public class ModBlocks {
    // 创建方块注册表
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ModMain.MOD_ID);

    public static final RegistryObject<Block> INFINITE_SINK = BLOCKS.register("infinite_sink",
            () -> new InfiniteSinkBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(3.5f, 6.0f)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .pushReaction(PushReaction.NORMAL)
            ));
    // 添加 motor_generators 方块
    public static final RegistryObject<Block> MOTOR_GENERATORS = BLOCKS.register("motor_generators",
            () -> new MotorGeneratorsBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(3.5f, 6.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .pushReaction(PushReaction.NORMAL)
            ));
    // 添加 polymeric_alloy_block 方块
    public static final RegistryObject<Block> POLYMERIC_ALLOY_BLOCK = BLOCKS.register("polymeric_alloy_block",
            () -> new PolymericAlloyBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(3.5f, 6.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .pushReaction(PushReaction.NORMAL)
                    .lightLevel((state) -> 14)
            ));

    // 注册方块对应的物品
    public static void registerBlockItems(IEventBus eventBus) {
        BLOCKS.getEntries().forEach(block -> {
            ModItems.ITEMS.register(block.getId().getPath(),
                    () -> new BlockItem(block.get(), new Item.Properties()));
        });
    }

    // 注册方法
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        registerBlockItems(eventBus);
    }
}
