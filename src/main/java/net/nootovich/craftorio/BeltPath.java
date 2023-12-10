package net.nootovich.craftorio;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.nootovich.craftorio.blocks.ModBlocks;


public class BeltPath {

    private static final double left          = 0.3125d;
    private static final double right         = 0.6875d;
    private static final double incrementStep = 0.01d;


    public enum SIDE {
        LEFT, RIGHT
    }

    public enum QUADRANT {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    public Level     lvl;
    public Vec3      pos;
    public Direction dir;
    public SIDE      side;

    public BeltPath(Level level, BlockPos pos, Direction dir, SIDE side) {
        this.lvl  = level;
        this.pos  = Vec3.atLowerCornerOf(pos);
        this.dir  = dir;
        this.side = side;
    }

    public static BeltPath createPath(Level level, Vec3 itemPos) {
        BlockPos   blockPos   = BlockPos.containing(itemPos);
        BlockState blockState = level.getBlockState(blockPos);
        if (!blockState.is(ModBlocks.BELT.get())) return null;
        Direction itemDir = level.getBlockState(blockPos).getValue(HorizontalDirectionalBlock.FACING);
        return createPath(level, itemPos, blockPos, itemDir);
    }

    public static BeltPath createPath(Level level, Vec3 itemPos, BlockPos blockPos, Direction dir) {
        Vec3 relativePos = rotateFromDir(itemPos.subtract(Vec3.atLowerCornerOf(blockPos)), dir);
        if (relativePos.x() < 0 || relativePos.x() > 1 || relativePos.z() < 0 || relativePos.z() > 1) {
            throw new RuntimeException("Somehow the `CraftorioItemEntity` position is outside its own blockpos\n"+itemPos+" : "+blockPos);
        }

        SIDE side = relativePos.x() < 0.5d ? SIDE.LEFT : SIDE.RIGHT;

        return new BeltPath(level, blockPos, dir, side);
    }

    public Vec3 getNewPosForItem(Vec3 itemPos) {
        Vec3 relativePos = rotateFromDir(itemPos.subtract(this.pos), dir);

        double diffToSidePos      = Math.max(Math.min(getSidePos()-relativePos.x(), incrementStep), -incrementStep);
        Vec3   newRelativePos     = incrementProgress(relativePos, incrementStep-Math.abs(diffToSidePos));
        Vec3   updatedRelativePos = newRelativePos.add(diffToSidePos, 0, 0);

        return rotateToDir(updatedRelativePos, dir).add(this.pos);
    }

    public Vec3 incrementProgress(Vec3 relativePos, double step) {
        relativePos = relativePos.subtract(0, 0, step);

        if (relativePos.z() > 0) return relativePos;

        relativePos = relativePos.add(0, 0, 1);
        pos         = pos.relative(dir, 1.0d);

        return updatePath(relativePos);
    }

    public Vec3 updatePath(Vec3 relativePos) {
        BlockState newBlock = lvl.getBlockState(BlockPos.containing(this.pos));
        if (!newBlock.is(ModBlocks.BELT.get())) return relativePos;

        Vec3 absPos = rotateToDir(relativePos, dir);

        Direction prevDir = dir;
        dir = newBlock.getValue(HorizontalDirectionalBlock.FACING);

        Vec3 newRelativePos = rotateFromDir(absPos, dir);

        side = (prevDir.get2DDataValue()+dir.get2DDataValue())%2 == 1 ? side : newRelativePos.x() < 0.5d ? SIDE.LEFT : SIDE.RIGHT;

        return newRelativePos;
    }

    public double getSidePos() {
        return side == SIDE.LEFT ? left : right;
    }

    public static QUADRANT getQuadrantFromPos(Vec3 relativePos) {
        if (relativePos.x() < .5) {
            if (relativePos.z() < .5) return QUADRANT.TOP_LEFT;
            else return QUADRANT.BOTTOM_LEFT;
        } else {
            if (relativePos.z() < .5) return QUADRANT.TOP_RIGHT;
            else return QUADRANT.BOTTOM_RIGHT;
        }
    }

    public static Vec3 getPosFromQuadrant(QUADRANT relativeQuad) {
        Vec3 result = new Vec3(0, .5, 0);
        result = result.add(relativeQuad == QUADRANT.TOP_LEFT || relativeQuad == QUADRANT.BOTTOM_LEFT ? left : right, 0, 0);
        result = result.add(0, 0, relativeQuad == QUADRANT.TOP_LEFT || relativeQuad == QUADRANT.TOP_RIGHT ? .25 : .75);
        return result;
    }

    public static AABB getQuadrantBB(QUADRANT relativeQuad) {
        Vec3 a = switch (relativeQuad) {
            case TOP_LEFT -> new Vec3(0, 1, 0);
            case TOP_RIGHT -> new Vec3(1, 1, 0);
            case BOTTOM_LEFT -> new Vec3(0, 1, 1);
            case BOTTOM_RIGHT -> new Vec3(1, 1, 1);
        };
        Vec3 b = new Vec3(.5, .5, .5);
        return new AABB(a, b);
    }

    public static AABB getQuadrantBBFromPos(Vec3 relativePos) {
        return getQuadrantBB(getQuadrantFromPos(relativePos));
    }

    public static Vec3 relativeRoundPosToQuad(Vec3 relativePos) {
        return getPosFromQuadrant(getQuadrantFromPos(relativePos));
    }

    public static Vec3 roundPosToQuad(Vec3 pos, Direction pDir) {
        Vec3 blockPos    = Vec3.atLowerCornerOf(BlockPos.containing(pos));
        Vec3 relativePos = rotateFromDir(pos.subtract(blockPos), pDir);
        Vec3 roundedPos  = rotateToDir(relativeRoundPosToQuad(relativePos), pDir).add(blockPos);
        return roundedPos;
    }

    public static QUADRANT getOppositeQuadH(QUADRANT q) {
        int ord = q.ordinal();
        if (ord%2 == 0) {
            return QUADRANT.values()[(ord+1)%4];
        } else {
            return QUADRANT.values()[(ord+3)%4];
        }
    }

    public static QUADRANT getOppositeQuadV(QUADRANT q) {
        return QUADRANT.values()[(q.ordinal()+2)%4];
    }

    public static boolean isTopQuad(QUADRANT q) {
        return q.ordinal() < 2;
    }

    public static boolean isBottomQuad(QUADRANT q) {
        return q.ordinal() > 1;
    }

    public static boolean isLeftQuad(QUADRANT q) {
        return q.ordinal() % 2 == 0;
    }

    public static boolean isRightQuad(QUADRANT q) {
        return q.ordinal() % 2 == 1;
    }

    // Assuming `NORTH` as the base direction
    public static Vec3 rotateToDir(Vec3 input, Direction dir) {
        return switch (dir) {
            case NORTH -> input;
            case WEST -> input.yRot((float) (Math.PI*0.5f)).add(0, 0, 1);
            case SOUTH -> input.yRot((float) (Math.PI*1.f)).add(1, 0, 1);
            case EAST -> input.yRot((float) (Math.PI*1.5f)).add(1, 0, 0);
            default -> throw new IllegalStateException("Something isn't right...\n[ERROR]: BeltPath.rotateToDir() -> Illegal direction. This should be unreachable.");
        };
    }

    // To `NORTH` as the base direction
    public static Vec3 rotateFromDir(Vec3 input, Direction dir) {
        return rotateToDir(input, Direction.from2DDataValue(4-dir.get2DDataValue()));
    }

}
