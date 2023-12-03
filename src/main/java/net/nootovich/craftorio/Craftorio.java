package net.nootovich.craftorio;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.nootovich.craftorio.blocks.ModBlocks;
import net.nootovich.craftorio.blocks.entity.ModBlockEntities;
import net.nootovich.craftorio.items.ModItems;

@Mod(Craftorio.MOD_ID)
public class Craftorio {

    public static final String MOD_ID = "craftorio";

    public Craftorio() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModCreativeTabs.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }
}
