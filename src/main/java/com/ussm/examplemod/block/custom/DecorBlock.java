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

import java.util.EnumMap;
import java.util.Map;

// les decos du gloom et du bloom (modeles 3D), generées dans le monde et dans l'onglet créa
public class DecorBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private final Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
    private final boolean solid;
    private final boolean hanging;

    public DecorBlock(double x0, double y0, double z0, double x1, double y1, double z1, boolean solid, boolean hanging, Properties properties) {
        super(properties);
        this.solid = solid;
        this.hanging = hanging;
        shapes.put(Direction.NORTH, Block.box(x0, y0, z0, x1, y1, z1));
        shapes.put(Direction.EAST, Block.box(16 - z1, y0, x0, 16 - z0, y1, x1));
        shapes.put(Direction.SOUTH, Block.box(16 - x1, y0, 16 - z1, 16 - x0, y1, 16 - z0));
        shapes.put(Direction.WEST, Block.box(z0, y0, 16 - x1, z1, y1, 16 - x0));
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes.get(state.getValue(FACING));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return solid ? shapes.get(state.getValue(FACING)) : Shapes.empty();
    }

    // la chaine pend du plafond, son modele fait 2 blocs de haut
    // (posée a la main juste sous le plafond, le haut rentre dans le bloc)
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (hanging) {
            BlockPos above = pos.above();
            BlockPos ceiling = pos.above(2);
            return level.getBlockState(above).isFaceSturdy(level, above, Direction.DOWN)
                    || level.getBlockState(ceiling).isFaceSturdy(level, ceiling, Direction.DOWN);
        }
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return canSurvive(state, level, pos) ? state : Blocks.AIR.defaultBlockState();
    }
}
