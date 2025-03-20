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
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor; // 替代 MaterialColor
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {
    // 创建方块注册表
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, psqq_item.MOD_ID);

    public static final RegistryObject<Block> INFINITE_SINK = BLOCKS.register("infinite_sink",
            () -> new InfiniteSinkBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL) // 使用 mapColor 代替直接指定 Material
                    .requiresCorrectToolForDrops()
                    .strength(3.5f, 6.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .pushReaction(PushReaction.NORMAL)
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
