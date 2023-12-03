package net.nootovich.craftorio;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.nootovich.craftorio.blocks.ModBlocks;
import net.nootovich.craftorio.items.ModItems;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> MOD_CREATIVE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Craftorio.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MOD_CREATIVE_TAB =
    MOD_CREATIVE_TABS.register("craftorio_creative_tab", () ->
        CreativeModeTab.builder().icon(()->new ItemStack(ModBlocks.BELT.get()))
                       .title(Component.translatable("creativetab.craftorio"))
            .displayItems((pParameters, pOutput)->{
                // pOutput.accept(ModItems.BELT_ITEM.get());
                pOutput.accept(ModBlocks.BELT.get());
            })
                       .build());

    public static void register(IEventBus eventBus) {
        MOD_CREATIVE_TABS.register(eventBus);
    }
}
