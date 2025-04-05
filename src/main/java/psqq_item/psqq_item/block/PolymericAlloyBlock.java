package psqq_item.psqq_item.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public class PolymericAlloyBlock extends Block{
    // 添加方向属性，使方块可以旋转
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public PolymericAlloyBlock(Properties properties) {
        super(properties);
        // 设置默认方向为北方
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    // 创建方块状态定义，添加FACING属性
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    // 当方块被放置时确定其朝向
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // 方块朝向与玩家面对的方向相反
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL; // 使用模型渲染而不是特殊渲染器
    }
}
