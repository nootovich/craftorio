package net.nootovich.craftorio.blocks.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nootovich.craftorio.blocks.ModBlocks;
import net.nootovich.craftorio.blocks.entity.BeltBlockEntity;
import net.nootovich.craftorio.blocks.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class ModBelt extends BaseEntityBlock {

    private enum Connection {
        NONE(0b0000),
        FRONT(0b0001),
        RIGHT(0b0010),
        BACK_(0b0100),
        LEFT_(0b1000);

        public final int val;

        Connection(int val) {
            this.val = val;
        }
    }

    public static final VoxelShape   BASE_X      = Block.box(1, 0, 2, 15, 8, 14);
    public static final VoxelShape   BASE_Z      = Block.box(2, 0, 1, 14, 8, 15);
    public static final VoxelShape   CONN_WEST   = Block.box(0, 0, 2, 2, 8, 14);
    public static final VoxelShape   CONN_EAST   = Block.box(14, 0, 2, 16, 8, 14);
    public static final VoxelShape   CONN_NORTH  = Block.box(2, 0, 0, 14, 8, 2);
    public static final VoxelShape   CONN_SOUTH  = Block.box(2, 0, 14, 14, 8, 16);
    public static final VoxelShape[] CONN_SHAPES = {CONN_NORTH, CONN_EAST, CONN_SOUTH, CONN_WEST};

    public static final DirectionProperty FACING      = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty   CONNECTIONS = IntegerProperty.create("connections", 0, 15);

    public ModBelt(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(CONNECTIONS, Connection.NONE.val));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BeltBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, ModBlockEntities.MOD_BELT_BLOCK_ENTITY.get(), (pLevel1, pPos, pState1, pBlockEntity) ->
            pBlockEntity.tick(pLevel1, pPos, pState1));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pPlayer.isShiftKeyDown() && pState.is(this) && pPlayer.getItemInHand(pHand).is(ModBlocks.BELT.get().asItem())) {

            Direction dir = pPlayer.getDirection();
            if (pState.getValue(FACING) != dir) {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(FACING, dir).setValue(CONNECTIONS, getConnections(pLevel, pPos, dir)));
                return InteractionResult.SUCCESS;
            }

            BlockPos   newPos        = pPos.relative(dir);
            BlockState newBlockState = pLevel.getBlockState(newPos);
            int        counter       = 5;

            while (counter-- > 0) {
                if (newBlockState.canBeReplaced()) {
                    pLevel.setBlockAndUpdate(newPos, defaultBlockState().setValue(FACING, dir).setValue(CONNECTIONS, getConnections(pLevel, newPos, dir)));
                    break;
                }

                if (!newBlockState.is(this) || newBlockState.getValue(FACING) != dir) break;

                newPos        = newPos.relative(dir);
                newBlockState = pLevel.getBlockState(newPos);
            }

            return InteractionResult.SUCCESS;
        }

        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        pLevel.setBlock(pPos, pState.setValue(CONNECTIONS, getConnections(pLevel, pPos, pState.getValue(FACING))), 2, 0);
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
    }

    // MISC

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Level     level = pContext.getLevel();
        BlockPos  pos   = pContext.getClickedPos();
        Direction dir   = pContext.getHorizontalDirection();

        return this.defaultBlockState().setValue(FACING, dir).setValue(CONNECTIONS, getConnections(level, pos, dir));
    }

    private int getConnections(Level level, BlockPos pos, Direction dir) {
        int conns = 0;

        BlockState frontBlock = level.getBlockState(pos.relative(dir));
        BlockState leftBlock  = level.getBlockState(pos.relative(dir.getCounterClockWise()));
        BlockState rightBlock = level.getBlockState(pos.relative(dir.getClockWise()));
        BlockState backBlock  = level.getBlockState(pos.relative(dir.getOpposite()));

        if (frontBlock.is(ModBlocks.BELT.get()) && frontBlock.getValue(FACING) != dir.getOpposite()) conns += Connection.FRONT.val;
        if (leftBlock.is(ModBlocks.BELT.get()) && leftBlock.getValue(FACING) == dir.getClockWise()) conns += Connection.LEFT_.val;
        if (rightBlock.is(ModBlocks.BELT.get()) && rightBlock.getValue(FACING) == dir.getCounterClockWise()) conns += Connection.RIGHT.val;
        if (backBlock.is(ModBlocks.BELT.get()) && backBlock.getValue(FACING) == dir) conns += Connection.BACK_.val;

        return conns;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(FACING, CONNECTIONS);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction direction   = pState.getValue(FACING);
        int       dirOffset   = direction.get2DDataValue();
        int       connections = pState.getValue(CONNECTIONS);

        VoxelShape base  = direction.getAxis() == Direction.Axis.X ? BASE_X : BASE_Z;
        VoxelShape extra = Block.box(0, 0, 0, 0, 0, 0);

        if ((connections&Connection.BACK_.val) > 0) extra = Shapes.or(extra, CONN_SHAPES[(0+dirOffset)%4]);
        if ((connections&Connection.LEFT_.val) > 0) extra = Shapes.or(extra, CONN_SHAPES[(1+dirOffset)%4]);
        if ((connections&Connection.FRONT.val) > 0) extra = Shapes.or(extra, CONN_SHAPES[(2+dirOffset)%4]);
        if ((connections&Connection.RIGHT.val) > 0) extra = Shapes.or(extra, CONN_SHAPES[(3+dirOffset)%4]);

        return Shapes.or(base, extra);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return false;
    }

    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }
}
