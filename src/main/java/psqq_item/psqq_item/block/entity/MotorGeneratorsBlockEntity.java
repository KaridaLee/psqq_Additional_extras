package psqq_item.psqq_item.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import java.util.List;
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
                    // 将progress拆分为高低位进行传输
                    case 0 -> MotorGeneratorsBlockEntity.this.progress & 0xFFFF; // progress低16位
                    case 1 -> (MotorGeneratorsBlockEntity.this.progress >> 16) & 0xFFFF; // progress高16位

                    // 将maxProgress拆分为高低位进行传输
                    case 2 -> MotorGeneratorsBlockEntity.this.maxProgress & 0xFFFF; // maxProgress低16位
                    case 3 -> (MotorGeneratorsBlockEntity.this.maxProgress >> 16) & 0xFFFF; // maxProgress高16位

                    // 能量存储相关数据
                    case 4 -> MotorGeneratorsBlockEntity.this.energyStorage.getEnergyStored() & 0xFFFF; // 低16位
                    case 5 -> (MotorGeneratorsBlockEntity.this.energyStorage.getEnergyStored() >> 16) & 0xFFFF; // 高16位
                    case 6 -> MotorGeneratorsBlockEntity.this.energyStorage.getMaxEnergyStored() & 0xFFFF; // 低16位
                    case 7 -> (MotorGeneratorsBlockEntity.this.energyStorage.getMaxEnergyStored() >> 16) & 0xFFFF; // 高16位

                    // 能量消耗
                    case 8 -> MotorGeneratorsBlockEntity.this.energyPerTick;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    // 设置progress的高低位
                    case 0 -> {
                        int high = MotorGeneratorsBlockEntity.this.progress & 0xFFFF0000;
                        MotorGeneratorsBlockEntity.this.progress = high | (value & 0xFFFF);
                    }
                    case 1 -> {
                        int low = MotorGeneratorsBlockEntity.this.progress & 0xFFFF;
                        MotorGeneratorsBlockEntity.this.progress = low | ((value & 0xFFFF) << 16);
                    }

                    // 设置maxProgress的高低位
                    case 2 -> {
                        int high = MotorGeneratorsBlockEntity.this.maxProgress & 0xFFFF0000;
                        MotorGeneratorsBlockEntity.this.maxProgress = high | (value & 0xFFFF);
                    }
                    case 3 -> {
                        int low = MotorGeneratorsBlockEntity.this.maxProgress & 0xFFFF;
                        MotorGeneratorsBlockEntity.this.maxProgress = low | ((value & 0xFFFF) << 16);
                    }

                    // 能量消耗
                    case 8 -> MotorGeneratorsBlockEntity.this.energyPerTick = value;
                }
            }

            @Override
            public int getCount() {
                return 9; // 增加到9个数据项
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

        // 确保总能量需求与当前参数一致
        this.totalEnergyRequired = this.maxProgress * this.energyPerTick;
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
        tag.putInt("total_energy_required", this.totalEnergyRequired);

        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        progress = tag.getInt("motor_generators.progress");

        // 加载自定义合成时间
        if (tag.contains("motor_generators.max_progress")) {
            maxProgress = tag.getInt("motor_generators.max_progress");
        }

        // 加载能量值
        if (tag.contains("energy")) {
            try {
                java.lang.reflect.Field energyField = EnergyStorage.class.getDeclaredField("energy");
                energyField.setAccessible(true);
                energyField.setInt(energyStorage, tag.getInt("energy"));
            } catch (Exception e) {
                // 忽略反射错误
            }
        }

        // 加载每tick能量消耗
        if (tag.contains("energy_per_tick")) {
            energyPerTick = tag.getInt("energy_per_tick");
        }

        // 加载总能量需求
        if (tag.contains("total_energy_required")) {
            totalEnergyRequired = tag.getInt("total_energy_required");
        } else {
            // 如果没有保存，则重新计算
            totalEnergyRequired = maxProgress * energyPerTick;
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

        boolean changed = false;

        // 缓存当前配方信息
        if (entity.progress == 0) {
            // 只在开始处理时检查配方
            if (hasRecipe(entity)) {
                // hasRecipe已经设置了maxProgress和energyPerTick
                // 开始处理
                if (entity.energyStorage.getEnergyStored() >= entity.energyPerTick) {
                    entity.energyStorage.extractEnergy(entity.energyPerTick, false);
                    entity.progress++;
                    changed = true;
                }
            }
        } else {
            // 正在处理中
            if (entity.energyStorage.getEnergyStored() >= entity.energyPerTick) {
                entity.energyStorage.extractEnergy(entity.energyPerTick, false);
                entity.progress++;
                changed = true;

                // 检查是否完成
                if (entity.progress >= entity.maxProgress) {
                    // 完成处理
                    craftItem(entity);
                    changed = true;
                }
            }
        }

        // 检查配方是否仍然有效
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        boolean stillValid = false;
        if (entity.progress > 0) {
            // 检查配方是否仍然有效
            List<MotorGeneratorsRecipe> recipes = level.getRecipeManager().getAllRecipesFor(MotorGeneratorsRecipe.Type.INSTANCE);
            for (MotorGeneratorsRecipe recipe : recipes) {
                if (recipe.matches(inventory, level)) {
                    stillValid = true;
                    break;
                }
            }

            if (!stillValid) {
                // 配方不再有效，重置进度
                entity.resetProgress();
                changed = true;
            }
        }

        if (changed) {
            entity.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    // 检查是否有足够的能量
    private static boolean hasEnoughEnergy(MotorGeneratorsBlockEntity entity) {
        return entity.energyStorage.getEnergyStored() >= entity.energyPerTick;
    }

    private static boolean hasRecipe(MotorGeneratorsBlockEntity entity) {
        Level level = entity.level;
        if (level == null) return false;

        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        // 直接获取配方实例，而不是通过类型查询
        List<MotorGeneratorsRecipe> recipes = level.getRecipeManager().getAllRecipesFor(MotorGeneratorsRecipe.Type.INSTANCE);

        for (MotorGeneratorsRecipe recipe : recipes) {
            if (recipe.matches(inventory, level)) {
                // 直接从配方实例获取处理时间和能量消耗
                entity.maxProgress = recipe.getProcessingTime();
                entity.energyPerTick = recipe.getEnergyPerTick();

                return canInsertItemIntoOutputSlot(inventory, recipe.getResultItem(level.registryAccess()));
            }
        }

        return false;
    }

    // 辅助方法，用于调试
    private static String inventoryToString(SimpleContainer inventory) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (!inventory.getItem(i).isEmpty()) {
                sb.append("Slot ").append(i).append(": ")
                        .append(inventory.getItem(i).getItem().getDescriptionId())
                        .append(" x").append(inventory.getItem(i).getCount())
                        .append(", ");
            }
        }
        return sb.toString();
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
        if (level == null || level.isClientSide()) return;

        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        // 直接遍历所有配方找匹配的
        List<MotorGeneratorsRecipe> recipes = level.getRecipeManager().getAllRecipesFor(MotorGeneratorsRecipe.Type.INSTANCE);

        for (MotorGeneratorsRecipe recipe : recipes) {
            if (recipe.matches(inventory, level)) {
                NonNullList<Ingredient> ingredients = recipe.getIngredients();
                boolean[] used = new boolean[6];

                // 确保有足够的材料和空间
                if (!hasEnoughMaterialsAndSpace(entity, recipe, level)) {
                    return;
                }

                // 消耗材料
                for (Ingredient ingredient : ingredients) {
                    for (int i = 0; i < 6; i++) {
                        if (!used[i] && ingredient.test(entity.itemHandler.getStackInSlot(i))) {
                            entity.itemHandler.extractItem(i, 1, false);
                            used[i] = true;
                            break;
                        }
                    }
                }

                // 添加输出物品
                ItemStack result = recipe.getResultItem(level.registryAccess());
                entity.itemHandler.insertItem(6, result.copy(), false);

                // 重置进度
                entity.resetProgress();
                return;
            }
        }
    }

    // 添加检查材料和空间的辅助方法
    private static boolean hasEnoughMaterialsAndSpace(MotorGeneratorsBlockEntity entity, MotorGeneratorsRecipe recipe, Level level) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        SimpleContainer tempInventory = new SimpleContainer(entity.itemHandler.getSlots());

        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            tempInventory.setItem(i, entity.itemHandler.getStackInSlot(i).copy());
        }

        boolean[] used = new boolean[6];

        // 检查材料
        for (Ingredient ingredient : ingredients) {
            boolean found = false;
            for (int i = 0; i < 6; i++) {
                if (!used[i] && ingredient.test(tempInventory.getItem(i))) {
                    tempInventory.getItem(i).shrink(1);
                    used[i] = true;
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }

        // 检查输出槽
        ItemStack result = recipe.getResultItem(level.registryAccess());
        ItemStack currentOutput = entity.itemHandler.getStackInSlot(6);

        if (currentOutput.isEmpty()) return true;
        if (!ItemStack.isSameItemSameTags(currentOutput, result)) return false;
        return currentOutput.getCount() + result.getCount() <= currentOutput.getMaxStackSize();
    }

    private void resetProgress() {
        this.progress = 0;
        setChanged();
        syncData(); // 确保数据同步
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

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("motor_generators.progress", this.progress);
        tag.putInt("motor_generators.max_progress", this.maxProgress);
        tag.putInt("energy_per_tick", this.energyPerTick);
        return tag;
    }

    public void syncData() {
        if (level != null && !level.isClientSide()) {
            // 设置更改标志
            setChanged();

            // 发送数据包
            CompoundTag tag = new CompoundTag();
            saveAdditional(tag);
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if (tag.contains("motor_generators.progress")) {
            this.progress = tag.getInt("motor_generators.progress");
        }
        if (tag.contains("motor_generators.max_progress")) {
            this.maxProgress = tag.getInt("motor_generators.max_progress");
        }
        if (tag.contains("energy_per_tick")) {
            this.energyPerTick = tag.getInt("energy_per_tick");
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            // 确保加载所有重要数据
            if (tag.contains("motor_generators.max_progress")) {
                this.maxProgress = tag.getInt("motor_generators.max_progress");
            }
            if (tag.contains("energy_per_tick")) {
                this.energyPerTick = tag.getInt("energy_per_tick");
            }
            if (tag.contains("motor_generators.progress")) {
                this.progress = tag.getInt("motor_generators.progress");
            }
        }
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