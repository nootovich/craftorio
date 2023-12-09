package net.nootovich.craftorio;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.nootovich.craftorio.blocks.ModBlocks;


public class BeltPath {

    private static final double left          = 0.3125d;
    private static final double right         = 0.6875d;
    private static final double incrementStep = 0.01d;


    public enum SIDE {
        LEFT, RIGHT

    }

    public Level     lvl;
    public Vec3      pos;
    public Direction dir;
    public SIDE      side;
    public double    progress;

    public BeltPath(Level level, BlockPos pos, Direction dir, SIDE side, double progress) {
        this.lvl      = level;
        this.pos      = Vec3.atLowerCornerOf(pos);
        this.dir      = dir;
        this.side     = side;
        this.progress = progress;
    }

    public static BeltPath createPath(Level level, Vec3 itemPos, BlockPos blockPos, Direction dir) {
        Vec3 relativePos = rotateFromDir(itemPos.subtract(Vec3.atLowerCornerOf(blockPos)), dir);
        if (relativePos.x() < 0 || relativePos.x() > 1 || relativePos.z() < 0 || relativePos.z() > 1) {
            throw new RuntimeException("Somehow the `CraftorioItemEntity` position is outside its own blockpos\n"+itemPos+" : "+blockPos);
        }

        SIDE   side     = relativePos.x() < 0.5d ? SIDE.LEFT : SIDE.RIGHT;
        double progress = 1-relativePos.z();

        return new BeltPath(level, blockPos, dir, side, progress);
    }

    public Vec3 getNewPosForItem(Vec3 itemPos) {
        Vec3 relativePos = rotateFromDir(itemPos.subtract(this.pos), dir);

        progress = 1-relativePos.z();

        double diffToSidePos      = Math.max(Math.min(getSidePos()-relativePos.x(), incrementStep), -incrementStep);
        Vec3   newRelativePos     = incrementProgress(relativePos, itemPos, incrementStep-Math.abs(diffToSidePos));
        Vec3   updatedRelativePos = newRelativePos.add(diffToSidePos, 0, 0);

        return rotateToDir(updatedRelativePos, dir).add(this.pos);
    }

    public Vec3 incrementProgress(Vec3 relativePos, Vec3 itemPos, double step) {
        progress += step;

        if (progress < 1) {
            return relativePos.with(Direction.Axis.Z, 1-progress);
        }

        progress -= 1;
        pos = pos.relative(dir, 1.0d);
        relativePos = relativePos.subtract(0, 0, incrementStep);

        return updatePath(getFractional(relativePos), itemPos);
    }

    public Vec3 updatePath(Vec3 relativePos, Vec3 itemPos) {
        BlockState newBlock = lvl.getBlockState(BlockPos.containing(this.pos));
        if (!newBlock.is(ModBlocks.BELT.get())) return relativePos;

        Vec3 absPos = rotateToDir(relativePos, dir);

        Direction prevDir = dir;
        dir = newBlock.getValue(HorizontalDirectionalBlock.FACING);

        Vec3 newRelativePos = rotateFromDir(absPos, dir);

        side     = (prevDir.get2DDataValue()+dir.get2DDataValue())%2 == 1 ? side : newRelativePos.x() < 0.5d ? SIDE.LEFT : SIDE.RIGHT;
        progress = 1-newRelativePos.z();

        return newRelativePos;
    }

    public double getSidePos() {
        return side == SIDE.LEFT ? left : right;
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

    public static Vec3 getFractional(Vec3 input) {
        return new Vec3(((input.x()%1)+1)%1, ((input.y()%1)+1)%1, ((input.z()%1)+1)%1);
    }

}
