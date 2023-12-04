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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nootovich.craftorio.blocks.entity.BeltBlockEntity;
import net.nootovich.craftorio.blocks.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class ModBelt extends BaseEntityBlock {

    public static final VoxelShape        SHAPE  = Block.box(0, 0, 0, 16, 10, 16);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public ModBelt(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
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
        if (pState.is(this) && !pPlayer.isShiftKeyDown()) {
            Direction playerDirection = pPlayer.getDirection();
            if (pState.getValue(FACING) == playerDirection) {

                BlockPos   newPos        = pPos.relative(playerDirection);
                BlockState newBlockState = pLevel.getBlockState(newPos);
                int        counter       = 5;

                while (counter-- > 0) {
                    if (newBlockState.canBeReplaced()) {
                        pLevel.setBlockAndUpdate(newPos, this.defaultBlockState().setValue(FACING, playerDirection));
                        break;
                    } else if (!newBlockState.is(this) || newBlockState.getValue(FACING) != playerDirection) break;
                    newPos        = newPos.relative(playerDirection);
                    newBlockState = pLevel.getBlockState(newPos);
                }
            } else pLevel.setBlockAndUpdate(pPos, pState.setValue(FACING, playerDirection));
            return InteractionResult.SUCCESS;
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    // MISC

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}
