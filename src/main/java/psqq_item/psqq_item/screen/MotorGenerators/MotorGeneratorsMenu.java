package psqq_item.psqq_item.screen.MotorGenerators;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import psqq_item.psqq_item.block.entity.MotorGeneratorsBlockEntity;
import psqq_item.psqq_item.screen.ModMenuTypes;
import psqq_item.psqq_item.screen.slot.ResultSlot;

public class MotorGeneratorsMenu extends AbstractContainerMenu {
    private final MotorGeneratorsBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public MotorGeneratorsMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(9));
    }

    public MotorGeneratorsMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.MOTOR_GENERATORS_MENU.get(), id);
        checkContainerSize(inv, 7);
        blockEntity = (MotorGeneratorsBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            // 左侧六个输入槽
            this.addSlot(new SlotItemHandler(handler, 0, 22, 27));
            this.addSlot(new SlotItemHandler(handler, 1, 22, 45));
            this.addSlot(new SlotItemHandler(handler, 2, 40, 27));
            this.addSlot(new SlotItemHandler(handler, 3, 40, 45));
            this.addSlot(new SlotItemHandler(handler, 4, 58, 27));
            this.addSlot(new SlotItemHandler(handler, 5, 58, 45));

            // 右侧一个输出槽
            this.addSlot(new ResultSlot(handler, 6, 116, 36));
        });

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = getActualProgress();
        int maxProgress = getActualMaxProgress();
        int progressArrowSize = 26; // 进度条长度

        if (maxProgress <= 0) return 0;
        progress = Math.min(progress, maxProgress);

        // 使用double避免整数除法问题
        return (int)((double)progress * progressArrowSize / maxProgress);
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT = 7;  // 总共7个槽位

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // 检查点击的是什么槽位，并相应地处理
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // 从玩家物品栏移动到方块物品栏
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT - 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // 从方块物品栏移动到玩家物品栏
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        // 如果堆叠大小 == 0，则槽位置空
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, blockEntity.getBlockState().getBlock());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 86 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 144));
        }
    }

    // 添加这个方法来获取精确的进度百分比（保留三位小数）
    public float getProgressPercentage() {
        int progress = getActualProgress();
        int maxProgress = getActualMaxProgress();

        return maxProgress != 0 ? (float) progress / maxProgress * 100 : 0;
    }

    public int getDataValue(int index) {
        if (index == 0) {
            return getActualProgress();
        } else if (index == 1) {
            return getActualMaxProgress();
        } else {
            return this.data.get(index);
        }
    }

    public ContainerData getData() {
        return this.data;
    }

    public int getActualProgress() {
        return (this.data.get(1) << 16) | this.data.get(0);
    }

    public int getActualMaxProgress() {
        return (this.data.get(3) << 16) | this.data.get(2);
    }
}

