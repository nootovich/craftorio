package net.nootovich.craftorio.blocks;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.nootovich.craftorio.Craftorio;
import net.nootovich.craftorio.blocks.custom.ModBelt;
import net.nootovich.craftorio.items.ModItems;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> MOD_BLOCKS =
        DeferredRegister.create(ForgeRegistries.BLOCKS, Craftorio.MOD_ID);

    public static final RegistryObject<Block> BELT =
        registerBlock("belt", () -> new ModBelt(BlockBehaviour.Properties.copy(Blocks.STONE).noOcclusion()));
    // MOD_BLOCKS.register("belt", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = MOD_BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.MOD_ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        MOD_BLOCKS.register(eventBus);
    }
}
