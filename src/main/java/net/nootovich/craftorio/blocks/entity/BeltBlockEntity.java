package net.nootovich.craftorio.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nootovich.craftorio.blocks.ModBlocks;
import net.nootovich.craftorio.entities.custom.CraftorioItemEntity;

public class BeltBlockEntity extends BlockEntity {

    private static final double speedFactor = 0.05d;


    public BeltBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.MOD_BELT_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        Direction direction      = (Direction) pState.getValues().get(HorizontalDirectionalBlock.FACING);
        Vec3      movementVector = Vec3.atLowerCornerOf(direction.getNormal()).scale(speedFactor);
        AABB      box            = new AABB(pPos).contract(0, -0.5, 0);
        box = direction.getAxis() == Direction.Axis.X ? box.deflate(0, 0, 0.2) : box.deflate(0.2, 0, 0);

        for (Entity e: pLevel.getEntities(null, box)) {
            if (e instanceof ItemEntity i) {
                ItemStack item = i.getItem();
                if (item.getCount() == 1) {
                    movementVector.add(i.getDeltaMovement().with(direction.getClockWise().getAxis(), 0));
                    i.discard();
                    pLevel.addFreshEntity(new CraftorioItemEntity(
                        pLevel, i.getX(), i.getY(), i.getZ(), item,
                        movementVector.x(), movementVector.y(), movementVector.z()));
                } else if (i.getTicksFrozen() == 0) {
                    i.setExtendedLifetime();
                    i.setTicksFrozen(8);
                    pLevel.addFreshEntity(new CraftorioItemEntity(
                        pLevel, i.getX(), i.getY(), i.getZ(), item.split(1),
                        movementVector.x(), movementVector.y(), movementVector.z()));
                } else i.setTicksFrozen(i.getTicksFrozen()-1);
                continue;
            } else if (e instanceof CraftorioItemEntity) {
                AABB itemBB = e.getBoundingBox();
                AABB blockBB;

                BlockPos   nextBlockPos = pPos.relative(direction);
                BlockState nextBlock    = pLevel.getBlockState(nextBlockPos);
                VoxelShape blockShape   = nextBlock.getCollisionShape(pLevel, nextBlockPos);
                if (!blockShape.isEmpty()) {
                    blockBB = blockShape.bounds().move(nextBlockPos).inflate(0.1d);
                    if (!nextBlock.is(ModBlocks.BELT.get()) && itemBB.intersects(blockBB)) {
                        e.setDeltaMovement(0, 0, 0);
                        continue;
                    }
                } else if (!nextBlock.is(ModBlocks.BELT.get())) {
                    Vec3 lowerCorner  = Vec3.atLowerCornerOf(nextBlockPos).add(-.1, -.1, -.1);
                    Vec3 higherCorner = Vec3.atLowerCornerOf(nextBlockPos).add(1.1, 1.1, 1.1);
                    if (itemBB.intersects(lowerCorner, higherCorner)) {
                        e.setDeltaMovement(0, 0, 0);
                        continue;
                    }
                }

                nextBlockPos = pPos.relative(direction).relative(direction.getClockWise());
                nextBlock    = pLevel.getBlockState(nextBlockPos);
                blockShape   = nextBlock.getCollisionShape(pLevel, nextBlockPos);
                if (!blockShape.isEmpty()) {
                    blockBB = blockShape.bounds().move(nextBlockPos).inflate(0.1d);
                    if (!nextBlock.is(ModBlocks.BELT.get()) && itemBB.intersects(blockBB)) {
                        e.setDeltaMovement(0, 0, 0);
                        continue;
                    }
                }

                nextBlockPos = pPos.relative(direction).relative(direction.getCounterClockWise());
                nextBlock    = pLevel.getBlockState(nextBlockPos);
                blockShape   = nextBlock.getCollisionShape(pLevel, nextBlockPos);
                if (!blockShape.isEmpty()) {
                    blockBB = blockShape.bounds().move(nextBlockPos).inflate(0.1d);
                    if (!nextBlock.is(ModBlocks.BELT.get()) && itemBB.intersects(blockBB)) {
                        e.setDeltaMovement(0, 0, 0);
                        continue;
                    }
                }
            }

            e.addDeltaMovement(movementVector);

            if (e instanceof Player p && p.getMotionDirection() == direction) continue;

            Direction.Axis axis    = direction.getAxis();
            double         delta   = e.getDeltaMovement().get(axis);
            double         desired = movementVector.get(axis);

            if (Math.abs(delta) > Math.abs(desired)) e.addDeltaMovement(new Vec3(0, 0, 0).with(axis, desired-delta));
        }
    }

    // MISC

    public boolean hasAnalogOutputSignal(BlockState pState) {
        return false;
    }

    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }
}
