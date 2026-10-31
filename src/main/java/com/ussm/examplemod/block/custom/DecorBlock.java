package com.ussm.examplemod.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DecorBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private final boolean solid;
    private final boolean hanging;
    private final VoxelShape north, east, south, west;

    // hitbox vers le nord, copiée de blockbench
    public DecorBlock(double x0, double y0, double z0, double x1, double y1, double z1, boolean solid, boolean hanging, Properties properties) {
        super(properties);
        this.solid = solid;
        this.hanging = hanging;

        north = Block.box(x0, y0, z0, x1, y1, z1);
        east = Block.box(16 - z1, y0, x0, 16 - z0, y1, x1);
        south = Block.box(16 - x1, y0, 16 - z1, 16 - x0, y1, 16 - z0);
        west = Block.box(z0, y0, 16 - x1, z1, y1, 16 - x0);

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST -> east;
            case SOUTH -> south;
            case WEST -> west;
            default -> north;
        };
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (solid) {
            return getShape(state, level, pos, context);
        }
        return Shapes.empty();
    }

    // la chaine fait 2 blocs de haut, si on la pose a la main sous le plafond le haut rentre dedans
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (!hanging) {
            return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
        }
        BlockPos above = pos.above();
        BlockPos ceiling = pos.above(2);
        return level.getBlockState(above).isFaceSturdy(level, above, Direction.DOWN)
                || level.getBlockState(ceiling).isFaceSturdy(level, ceiling, Direction.DOWN);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return canSurvive(state, level, pos) ? state : Blocks.AIR.defaultBlockState();
    }
}