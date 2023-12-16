package net.nootovich.craftorio;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.nootovich.craftorio.blocks.ModBlocks;
import net.nootovich.craftorio.blocks.entity.ModBlockEntities;
import net.nootovich.craftorio.blocks.entity.renderer.DebugBeltBlockEntityRenderer;
import net.nootovich.craftorio.entities.ModEntities;
import net.nootovich.craftorio.entities.client.CraftorioItemEntityRenderer;
import net.nootovich.craftorio.items.ModItems;

@Mod(Craftorio.MOD_ID)
public class Craftorio {

    public static final String MOD_ID = "craftorio";

    public Craftorio() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModCreativeTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntities.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    @Mod.EventBusSubscriber(modid=MOD_ID, bus=Mod.EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntities.CRAFTORIO_ITEM.get(), CraftorioItemEntityRenderer::new);

            // DEBUG
            BlockEntityRenderers.register(ModBlockEntities.MOD_BELT_BLOCK_ENTITY.get(), DebugBeltBlockEntityRenderer::new);
        }
    }
}
