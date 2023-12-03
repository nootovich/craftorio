package net.nootovich.craftorio.items;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.nootovich.craftorio.Craftorio;
import net.nootovich.craftorio.blocks.ModBlocks;

public class ModItems {

    public static final DeferredRegister<Item> MOD_ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, Craftorio.MOD_ID);

    // public static final RegistryObject<Item> BELT_ITEM =
    //     MOD_ITEMS.register("belt_item", () ->
    //         new BlockItem(ModBlocks.BELT.get(), new Item.Properties().stacksTo(7)));

    public static void register(IEventBus eventBus) {
        MOD_ITEMS.register(eventBus);
    }
}
