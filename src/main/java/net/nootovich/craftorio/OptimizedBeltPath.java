package net.nootovich.craftorio;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.nootovich.craftorio.blocks.custom.ModBelt;

public class OptimizedBeltPath {

    public enum SIDE {LEFT, RIGHT}

    public static final double leftSide  = 0.3125d;
    public static final double rightSide = 0.6875d;

    public Vec3       start;
    public Direction  dir;
    public double     len;
    public BeltItem[] items;

    public OptimizedBeltPath(BlockState state, BlockPos pos, double len, SIDE side) {
        this.dir   = state.getValue(ModBelt.FACING);
        this.items = new BeltItem[0];
        this.len   = len;

        Vec3 vSide = new Vec3(side == SIDE.LEFT ? leftSide : rightSide, 0, 1);
        Vec3 vPos  = Vec3.atLowerCornerOf(pos);
        this.start = vPos.add(BeltPath.rotateToDir(vSide, dir));
    }

    public class BeltItem {

        public Item   item;
        public double toNext;

        BeltItem(Item item, double toNext) {
            this.item   = item;
            this.toNext = toNext;
        }
    }
}
