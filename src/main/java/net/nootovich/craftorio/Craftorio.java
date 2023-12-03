package net.nootovich.craftorio;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.nootovich.craftorio.blocks.ModBlocks;
import net.nootovich.craftorio.items.ModItems;

@Mod(Craftorio.MOD_ID)
public class Craftorio {

    public static final String MOD_ID = "craftorio";

    // public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);


    // public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB
    //     = CREATIVE_MODE_TABS.register("example_tab", () ->
    //     CreativeModeTab.builder().build());

    public Craftorio() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModCreativeTabs.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        // modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    // private void addCreative(BuildCreativeModeTabContentsEvent event) {
    //     if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
    //         event.accept(ModItems.BELT_ITEM);
    //     }
    // }

}
