package psqq_item.psqq_item.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.material.Fluids;
import psqq_item.psqq_item.ModBlockEntities;

public class InfiniteSinkBlockEntity extends BlockEntity {

    // 创建一个自定义的IFluidHandler实现，实现无限水
    private class InfiniteWaterHandler implements IFluidHandler {

        @Override
        public int getTanks() {
            return 1;
        }

        @NotNull
        @Override
        public FluidStack getFluidInTank(int tank) {
            // 创建一个特殊的FluidStack，其中amount为-1表示无限
            FluidStack infiniteWater = new FluidStack(Fluids.WATER, -1);
            return infiniteWater;
        }

        @Override
        public int getTankCapacity(int tank) {
            return -1; // -1表示无限容量
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return stack.getFluid() == Fluids.WATER; // 只接受水
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return 0; // 不允许填充，因为已经是无限的了
        }

        @NotNull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if (resource.getFluid() == Fluids.WATER) {
                // 如果请求提取水，返回请求的量，永远不会耗尽
                return new FluidStack(Fluids.WATER, resource.getAmount());
            }
            return FluidStack.EMPTY;
        }

        @NotNull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            // 提供请求的水量，永远不会耗尽
            return new FluidStack(Fluids.WATER, maxDrain);
        }
    }

    private final InfiniteWaterHandler waterHandler = new InfiniteWaterHandler();
    private final LazyOptional<IFluidHandler> fluidHandlerLazyOptional = LazyOptional.of(() -> waterHandler);

    public InfiniteSinkBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INFINITE_SINK.get(), pos, state);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        // 不需要从NBT加载，因为水量总是无限的
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        // 不需要保存到NBT，因为水量总是无限的
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandlerLazyOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fluidHandlerLazyOptional.invalidate();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }
}