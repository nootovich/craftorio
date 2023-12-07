package net.nootovich.craftorio.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.nootovich.craftorio.Craftorio;
import net.nootovich.craftorio.entities.custom.CraftorioItemEntity;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> MOD_ENTITY_TYPES =
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Craftorio.MOD_ID);

    public static final RegistryObject<EntityType<CraftorioItemEntity>> CRAFTORIO_ITEM =
        MOD_ENTITY_TYPES.register("craftorio_item", () ->
            EntityType.Builder.<CraftorioItemEntity>of(CraftorioItemEntity::new, MobCategory.MISC)
                              .sized(0.275f,0.275f).build("craftorio_item"));


    public static void register(IEventBus eventBus) {
        MOD_ENTITY_TYPES.register(eventBus);
    }
}
