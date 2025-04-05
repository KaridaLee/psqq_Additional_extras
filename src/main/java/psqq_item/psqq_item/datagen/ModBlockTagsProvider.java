package psqq_item.psqq_item.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import psqq_item.psqq_item.ModBlocks;
import psqq_item.psqq_item.ModMain;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ModMain.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // 添加到MINEABLE_WITH_PICKAXE标签，表示可以用镐挖掘
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.INFINITE_SINK.get());

        // 添加到NEEDS_IRON_TOOL标签，表示需要铁镐或更好的工具
        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.INFINITE_SINK.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.MOTOR_GENERATORS.get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.MOTOR_GENERATORS.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.POLYMERIC_ALLOY_BLOCK.get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.POLYMERIC_ALLOY_BLOCK.get());
    }
}