package net.nootovich.craftorio.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
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
        Direction      direction = pState.getValue(HorizontalDirectionalBlock.FACING);
        Direction.Axis axis      = direction.getAxis();
        // Vec3           movementVector = Vec3.atLowerCornerOf(direction.getNormal()).scale(speedFactor);
        Vec3 movementVector = Vec3.ZERO;
        AABB movementBB     = new AABB(pPos).contract(0, -0.6, 0).contract(0, 0.2, 0);
        movementBB = axis == Direction.Axis.X ? movementBB.deflate(0, 0, 0.2) : movementBB.deflate(0.2, 0, 0);

        for (Entity e: pLevel.getEntities(null, movementBB)) {
            if (e instanceof ItemEntity ie) {
                ItemStack item = ie.getItem();

                if (item.getCount() == 1) {

                    // movementVector.add(ie.getDeltaMovement().with(direction.getClockWise().getAxis(), 0));
                    ie.discard();
                    pLevel.addFreshEntity(new CraftorioItemEntity(pLevel, ie.position(), item, movementVector, direction));

                } else if (ie.getTicksFrozen() == 0) {

                    ie.setExtendedLifetime();
                    ie.setTicksFrozen(8);
                    pLevel.addFreshEntity(new CraftorioItemEntity(pLevel, ie.position(), item.split(1), movementVector, direction));

                } else ie.setTicksFrozen(ie.getTicksFrozen()-1);

                continue;

            }
            /*else if (e instanceof CraftorioItemEntity cie) {
                { // Collision with blocks
                    AABB itemBB = cie.getBoundingBox();

                    BlockPos nextBlockPos = pPos.relative(direction);
                    if (checkCraftorioItemCollisionWithBlock(pLevel, nextBlockPos, itemBB)) {
                        cie.setDeltaMovement(0, 0, 0);
                        continue;
                    } else if (!pLevel.getBlockState(nextBlockPos).is(ModBlocks.BELT.get())) {
                        Vec3 lowerCorner  = Vec3.atLowerCornerOf(nextBlockPos).add(-.05, -.05, -.05);
                        Vec3 higherCorner = Vec3.atLowerCornerOf(nextBlockPos).add(1.05, 1.05, 1.05);
                        if (itemBB.intersects(lowerCorner, higherCorner)) {
                            cie.setDeltaMovement(0, 0, 0);
                            continue;
                        }
                    }

                    if (checkCraftorioItemCollisionWithBlock(pLevel, nextBlockPos.relative(direction.getClockWise()), itemBB)) {
                        cie.setDeltaMovement(0, 0, 0);
                        continue;
                    }

                    if (checkCraftorioItemCollisionWithBlock(pLevel, nextBlockPos.relative(direction.getCounterClockWise()), itemBB)) {
                        cie.setDeltaMovement(0, 0, 0);
                        continue;
                    }
                }

                // { // Movement towards lanes
                //     // 0.2 ---BELT--- 0.8
                //     //   LEFT     RIGHT
                //     //   0.3125  0.6875
                //
                //     if (axis == Direction.Axis.X) {
                //         final double fractionalZ       = cie.position().z()-pPos.getZ();
                //         final double distToClosestLane = Math.min(Math.abs(fractionalZ-leftLane), Math.abs(fractionalZ-rightLane));
                //         final double speed             = distToClosestLane*distToClosestLane*speedFactor;
                //
                //         if (fractionalZ > 0 && fractionalZ < 1 && distToClosestLane > leeway) {
                //             if (fractionalZ < leftLane) cie.addDeltaMovement(new Vec3(0, 0, speed));
                //             if (fractionalZ > rightLane) cie.addDeltaMovement(new Vec3(0, 0, -speed));
                //             if (fractionalZ > leftLane && fractionalZ < midPoint) cie.addDeltaMovement(new Vec3(0, 0, -speed));
                //             if (fractionalZ < rightLane && fractionalZ > midPoint) cie.addDeltaMovement(new Vec3(0, 0, speed));
                //         }
                //     } else {
                //         final double fractionalX       = cie.position().x()-pPos.getX();
                //         final double distToClosestLane = Math.min(Math.abs(fractionalX-leftLane), Math.abs(fractionalX-rightLane));
                //         final double speed             = distToClosestLane*distToClosestLane*speedFactor;
                //
                //         if (fractionalX > 0 && fractionalX < 1 && distToClosestLane > leeway) {
                //             if (fractionalX < leftLane) cie.addDeltaMovement(new Vec3(speed, 0, 0));
                //             if (fractionalX > rightLane) cie.addDeltaMovement(new Vec3(-speed, 0, 0));
                //             if (fractionalX > leftLane && fractionalX < midPoint) cie.addDeltaMovement(new Vec3(-speed, 0, 0));
                //             if (fractionalX < rightLane && fractionalX > midPoint) cie.addDeltaMovement(new Vec3(speed, 0, 0));
                //         }
                //     }
                // }
            }*/

            // e.addDeltaMovement(movementVector);
            //
            // if (e instanceof Player) continue;
            //
            // double delta   = e.getDeltaMovement().get(axis);
            // double desired = movementVector.get(axis);
            // if (Math.abs(delta) > Math.abs(desired)) {
            //     e.addDeltaMovement(new Vec3(0, 0, 0).with(axis, desired-delta));
            // }
        }
    }

    private boolean checkCraftorioItemCollisionWithBlock(Level pLevel, BlockPos nextBlockPos, AABB itemBB) {
        BlockState nextBlock  = pLevel.getBlockState(nextBlockPos);
        VoxelShape blockShape = nextBlock.getCollisionShape(pLevel, nextBlockPos);

        if (!blockShape.isEmpty()) {
            AABB blockBB = blockShape.bounds().move(nextBlockPos).inflate(0.05d);
            return !nextBlock.is(ModBlocks.BELT.get()) && itemBB.intersects(blockBB);
        }

        return false;
    }

    // MISC

    public boolean hasAnalogOutputSignal(BlockState pState) {
        return false;
    }

    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }
}
