package net.nootovich.craftorio.entities.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.nootovich.craftorio.blocks.ModBlocks;
import net.nootovich.craftorio.entities.ModEntities;

public class CraftorioItemEntity extends Entity {

    private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(CraftorioItemEntity.class, EntityDataSerializers.ITEM_STACK);

    public CraftorioItemEntity(EntityType<? extends CraftorioItemEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        setYRot(random.nextFloat()*360.f);
    }

    public CraftorioItemEntity(Level pLevel, ItemStack pItemStack, Entity pOther) {
        this(pLevel, pOther.getX(), pOther.getY(), pOther.getZ(), pItemStack);
    }

    public CraftorioItemEntity(Level pLevel, double pPosX, double pPosY, double pPosZ, ItemStack pItemStack) {
        this(pLevel, pPosX, pPosY, pPosZ, pItemStack, 0, 0, 0);
    }

    public CraftorioItemEntity(Level pLevel, Vec3 pos, ItemStack pItemStack) {
        this(pLevel, pos, pItemStack, Vec3.ZERO);
    }

    public CraftorioItemEntity(Level pLevel, Vec3 pos, ItemStack pItemStack, Vec3 delta) {
        this(pLevel, pos.x(), pos.y(), pos.z(), pItemStack, delta.x(), delta.y(), delta.z());
    }

    public CraftorioItemEntity(Level pLevel, double pPosX, double pPosY, double pPosZ, ItemStack pItemStack, double pDeltaX, double pDeltaY, double pDeltaZ) {
        this(ModEntities.CRAFTORIO_ITEM.get(), pLevel);
        setPos(pPosX, pPosY, pPosZ);
        setDeltaMovement(pDeltaX, pDeltaY, pDeltaZ);
        setItem(pItemStack);
    }

    @Override
    public void tick() {
        if (this.getItem().isEmpty()) {
            this.discard();
            return;
        }
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        Vec3 delta = this.getDeltaMovement();
        if (!this.isNoGravity()) this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));

        if (this.level().isClientSide) {
            this.noPhysics = false;
        } else {
            this.noPhysics = !this.level().noCollision(this, this.getBoundingBox().deflate(1.0E-7D));
            if (this.noPhysics) {
                this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY+this.getBoundingBox().maxY)/2.0D, this.getZ());
            }
        }

        if (!this.onGround() || this.getDeltaMovement().horizontalDistanceSqr() > (double) 1.0E-5F || (this.tickCount+this.getId())%4 == 0) {
            this.move(MoverType.SELF, this.getDeltaMovement());
            float f1 = 0.98F;
            if (this.onGround()) {
                BlockPos groundPos = getBlockPosBelowThatAffectsMyMovement();
                f1 = this.level().getBlockState(groundPos).getFriction(level(), groundPos, this)*0.98F;
            }

            this.setDeltaMovement(this.getDeltaMovement().multiply(f1, 0.98D, f1));
            if (this.onGround()) {
                Vec3 vec31 = this.getDeltaMovement();
                if (vec31.y < 0.0D) this.setDeltaMovement(vec31.multiply(1.0D, -0.5D, 1.0D));
            }
        }

        if (!this.level().isClientSide) {
            double d0 = this.getDeltaMovement().subtract(delta).lengthSqr();
            if (d0 > 0.01D) this.hasImpulse = true;

            if (this.onGround() && !this.getBlockStateOn().is(ModBlocks.BELT.get())) {
                this.discard();
                this.level().addFreshEntity(new ItemEntity(
                    this.level(), xo, yo, zo, this.getItem(), delta.x(), delta.y(), delta.z()));
            }
        }

        if (this.getItem().isEmpty() && !this.isRemoved()) this.discard();
    }

    // MISC


    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    public ItemStack getItem() {
        return this.getEntityData().get(DATA_ITEM);
    }

    public void setItem(ItemStack pStack) {
        this.getEntityData().set(DATA_ITEM, pStack);
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        CompoundTag compoundtag = pCompound.getCompound("Item");
        this.setItem(ItemStack.of(compoundtag));
        if (this.getItem().isEmpty()) this.discard();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        if (this.getItem().isEmpty()) return;
        pCompound.put("Item", this.getItem().save(new CompoundTag()));
    }
}
