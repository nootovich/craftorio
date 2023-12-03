package net.nootovich.craftorio.blocks.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.nootovich.craftorio.Craftorio;
import net.nootovich.craftorio.blocks.ModBlocks;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> MOD_BLOCK_ENTITIES =
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Craftorio.MOD_ID);

    public static final RegistryObject<BlockEntityType<BeltBlockEntity>> MOD_BELT_BLOCK_ENTITY =
        MOD_BLOCK_ENTITIES.register("belt_block_entity", () ->
            BlockEntityType.Builder.of(BeltBlockEntity::new, ModBlocks.BELT.get()).build(null));

    public static void register(IEventBus eventBus) {
        MOD_BLOCK_ENTITIES.register(eventBus);
    }
}
