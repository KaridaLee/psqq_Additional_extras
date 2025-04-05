package psqq_item.psqq_item.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import psqq_item.psqq_item.screen.MotorGenerators.MotorGeneratorsMenu;
import psqq_item.psqq_item.ModBlockEntities;
import psqq_item.psqq_item.recipe.MotorGeneratorsRecipe;

public class MotorGeneratorsBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(7) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    // 添加能量存储
    private final EnergyStorage energyStorage = new EnergyStorage(6400000, 3200000, 3200000, 0) {
        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int energyExtracted = super.extractEnergy(maxExtract, simulate);
            if (!simulate && energyExtracted > 0) {
                setChanged();
            }
            return energyExtracted;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int energyReceived = super.receiveEnergy(maxReceive, simulate);
            if (!simulate && energyReceived > 0) {
                setChanged();
            }
            return energyReceived;
        }
    };

    // 正确声明 LazyOptional<IEnergyStorage>
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 200; // 修改默认合成时间为200刻（10秒）

    // 每刻消耗的能量
    private int energyPerTick = 50; // 每刻消耗50FE
    // 总能量消耗
    private int totalEnergyRequired = maxProgress * energyPerTick; // 总能量需求

    public MotorGeneratorsBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOTOR_GENERATORS.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> MotorGeneratorsBlockEntity.this.progress;
                    case 1 -> MotorGeneratorsBlockEntity.this.maxProgress;
                    case 2 -> MotorGeneratorsBlockEntity.this.energyStorage.getEnergyStored() & 0xFFFF; // 低16位
                    case 3 -> (MotorGeneratorsBlockEntity.this.energyStorage.getEnergyStored() >> 16) & 0xFFFF; // 高16位
                    case 4 -> MotorGeneratorsBlockEntity.this.energyStorage.getMaxEnergyStored() & 0xFFFF; // 低16位
                    case 5 -> (MotorGeneratorsBlockEntity.this.energyStorage.getMaxEnergyStored() >> 16) & 0xFFFF; // 高16位
                    case 6 -> MotorGeneratorsBlockEntity.this.energyPerTick;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> MotorGeneratorsBlockEntity.this.progress = value;
                    case 1 -> MotorGeneratorsBlockEntity.this.maxProgress = value;
                    case 6 -> MotorGeneratorsBlockEntity.this.energyPerTick = value;
                }
            }

            @Override
            public int getCount() {
                return 7; // 增加数据项数量
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.psqq_item.motor_generators");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new MotorGeneratorsMenu(id, inventory, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }

        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("motor_generators.progress", this.progress);
        tag.putInt("motor_generators.max_progress", this.maxProgress);
        tag.putInt("energy", this.energyStorage.getEnergyStored());
        tag.putInt("energy_per_tick", this.energyPerTick);

        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        progress = tag.getInt("motor_generators.progress");

        // 加载自定义合成时间，如果存在
        if (tag.contains("motor_generators.max_progress")) {
            maxProgress = tag.getInt("motor_generators.max_progress");
        }

        // 加载能量值
        if (tag.contains("energy")) {
            // 通过反射设置能量值，因为EnergyStorage没有公共的setter方法
            try {
                java.lang.reflect.Field energyField = EnergyStorage.class.getDeclaredField("energy");
                energyField.setAccessible(true);
                energyField.setInt(energyStorage, tag.getInt("energy"));
            } catch (Exception e) {
                // 处理可能的反射错误
            }
        }

        // 加载每刻能量消耗
        if (tag.contains("energy_per_tick")) {
            energyPerTick = tag.getInt("energy_per_tick");
        }
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MotorGeneratorsBlockEntity entity) {
        if (level.isClientSide()) {
            return;
        }

        // 首先检查是否有配方，同时会更新energyPerTick
        if (hasRecipe(entity)) {
            // 检查是否有足够的能量
            if (entity.energyStorage.getEnergyStored() >= entity.energyPerTick) {
                // 尝试消耗能量
                int extracted = entity.energyStorage.extractEnergy(entity.energyPerTick, false);

                if (extracted == entity.energyPerTick) {  // 确保消耗了正确数量的能量
                    entity.progress++;
                    setChanged(level, pos, state);

                    if (entity.progress >= entity.maxProgress) {
                        craftItem(entity);
                    }
                }
            }
        } else {
            entity.resetProgress();
            setChanged(level, pos, state);
        }
    }

    // 检查是否有足够的能量
    private static boolean hasEnoughEnergy(MotorGeneratorsBlockEntity entity) {
        return entity.energyStorage.getEnergyStored() >= entity.energyPerTick;
    }

    private static boolean hasRecipe(MotorGeneratorsBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());

        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        // 使用配方系统查找匹配的配方
        Optional<MotorGeneratorsRecipe> match = level.getRecipeManager()
                .getRecipeFor(MotorGeneratorsRecipe.Type.INSTANCE, inventory, level);

        if (match.isPresent()) {
            // 更新实体的最大进度值为配方中指定的处理时间
            entity.maxProgress = match.get().getProcessingTime();
            // 更新实体的每tick能量消耗为配方中指定的值
            entity.energyPerTick = match.get().getEnergyPerTick();
            return canInsertItemIntoOutputSlot(inventory, match.get().getResultItem(level.registryAccess()));
        }

        return false;
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack resultItem) {
        ItemStack outputSlot = inventory.getItem(6);

        if (outputSlot.isEmpty()) {
            return true;
        }

        // 使用更通用的方法检查物品是否相同
        if (outputSlot.getItem() != resultItem.getItem()) {
            return false;
        }

        return outputSlot.getCount() + resultItem.getCount() <= outputSlot.getMaxStackSize();
    }

    private static void craftItem(MotorGeneratorsBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());

        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<MotorGeneratorsRecipe> match = level.getRecipeManager()
                .getRecipeFor(MotorGeneratorsRecipe.Type.INSTANCE, inventory, level);

        if (match.isPresent()) {
            // 消耗输入物品
            MotorGeneratorsRecipe recipe = match.get();
            NonNullList<Ingredient> ingredients = recipe.getIngredients();
            boolean[] used = new boolean[6]; // 跟踪哪些槽位已被使用

            // 找到匹配的槽位并消耗一个物品
            for (Ingredient ingredient : ingredients) {
                for (int i = 0; i < 6; i++) {
                    if (!used[i] && ingredient.test(entity.itemHandler.getStackInSlot(i))) {
                        entity.itemHandler.extractItem(i, 1, false);
                        used[i] = true;
                        break;
                    }
                }
            }

            // 添加输出物品，传递 level.registryAccess() 参数
            entity.itemHandler.setStackInSlot(6, new ItemStack(recipe.getResultItem(level.registryAccess()).getItem(),
                    entity.itemHandler.getStackInSlot(6).getCount() + recipe.getResultItem(level.registryAccess()).getCount()));

            entity.resetProgress();
        }
    }

    private void resetProgress() {
        this.progress = 0;
        // 不重置maxProgress，因为它应该由当前配方决定
    }

    // 设置合成时间的方法
    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        setChanged();
    }

    // 设置每刻能量消耗的方法
    public void setEnergyPerTick(int energyPerTick) {
        this.energyPerTick = energyPerTick;
        setChanged();
    }

    // 获取当前能量存储的方法
    public int getEnergyStored() {
        return this.energyStorage.getEnergyStored();
    }

    // 获取最大能量存储的方法
    public int getMaxEnergyStored() {
        return this.energyStorage.getMaxEnergyStored();
    }
}