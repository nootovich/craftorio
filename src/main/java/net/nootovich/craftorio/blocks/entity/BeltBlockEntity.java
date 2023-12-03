package net.nootovich.craftorio.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BeltBlockEntity extends BlockEntity {

    private final ItemStackHandler           itemHandler     = new ItemStackHandler(2);
    private       LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    private static final int INPUT  = 0;
    private static final int OUTPUT = 1;

    private static final double speedFactor = 0.05d;


    public BeltBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.MOD_BELT_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        AABB box       = new AABB(pPos, pPos.offset(1, 1, 1));
        Vec3 direction = Vec3.atLowerCornerOf(((Direction) pState.getValues().get(HorizontalDirectionalBlock.FACING)).getNormal()).scale(speedFactor);
        pLevel.getEntities(null, box).forEach(e -> e.move(MoverType.SELF, direction));
        // pLevel.getEntities(null, box).forEach(e -> e.addDeltaMovement(direction));
    }

    // MISC

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }
}
