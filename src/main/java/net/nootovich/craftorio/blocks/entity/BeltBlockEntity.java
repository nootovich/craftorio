package net.nootovich.craftorio.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nootovich.craftorio.blocks.ModBlocks;
import net.nootovich.craftorio.entities.ModEntities;
import net.nootovich.craftorio.entities.custom.CraftorioItemEntity;

import static net.nootovich.craftorio.BeltPath.*;

public class BeltBlockEntity extends BlockEntity {

    public BeltBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.MOD_BELT_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        Direction      dir    = pState.getValue(HorizontalDirectionalBlock.FACING);
        Direction.Axis axis   = dir.getAxis();
        AABB           beltBB = new AABB(pPos).contract(0, -0.6, 0).contract(0, 0.2, 0);
        beltBB = axis == Direction.Axis.X ? beltBB.deflate(0, 0, 0.2) : beltBB.deflate(0.2, 0, 0);

        for (Entity e: pLevel.getEntities(null, beltBB)) {
            if (e instanceof ItemEntity ie) {
                ie.setExtendedLifetime();
                trySpawnCraftorioItemEntity(pLevel, ie, dir);
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

            }*/
        }
    }

    private void trySpawnCraftorioItemEntity(Level pLevel, ItemEntity ie, Direction dir) {
        Vec3     pos         = ie.position();
        Vec3     blockPos    = Vec3.atLowerCornerOf(BlockPos.containing(pos));
        Vec3     relativePos = rotateFromDir(pos.subtract(blockPos), dir);
        QUADRANT q           = getQuadrantFromPos(relativePos);

        Vec3[] spawnPositions = new Vec3[4];
        spawnPositions[0] = rotateToDir(getPosFromQuadrant(q), dir).add(blockPos);
        spawnPositions[1] = rotateToDir(getPosFromQuadrant(getOppositeQuadH(q)), dir).add(blockPos);
        spawnPositions[2] = rotateToDir(getPosFromQuadrant(getOppositeQuadV(q)), dir).add(blockPos);
        spawnPositions[3] = rotateToDir(getPosFromQuadrant(getOppositeQuadH(getOppositeQuadV(q))), dir).add(blockPos);

        ItemStack item = ie.getItem();

        for (Vec3 spawnPos: spawnPositions) {
            if (spawnPos == null || item.getCount() < 1) break;

            AABB CIEAABB = ModEntities.CRAFTORIO_ITEM.get().getAABB(spawnPos.x(), spawnPos.y(), spawnPos.z());
            if (!pLevel.getEntities(ie, CIEAABB, (cie) -> cie instanceof CraftorioItemEntity).isEmpty()) continue;

            pLevel.addFreshEntity(new CraftorioItemEntity(pLevel, spawnPos, item.split(1), Vec3.ZERO, dir));
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
}
