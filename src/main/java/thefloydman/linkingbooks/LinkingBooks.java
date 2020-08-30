package thefloydman.linkingbooks;

import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import thefloydman.linkingbooks.block.ModBlocks;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.client.renderer.entity.DescriptiveBookRenderer;
import thefloydman.linkingbooks.client.renderer.entity.LinkingBookRenderer;
import thefloydman.linkingbooks.entity.ModEntityTypes;
import thefloydman.linkingbooks.fluid.ModFluids;
import thefloydman.linkingbooks.inventory.container.ModContainerTypes;
import thefloydman.linkingbooks.item.ModItems;
import thefloydman.linkingbooks.linking.LinkEffects;
import thefloydman.linkingbooks.network.ModNetworkHandler;
import thefloydman.linkingbooks.tileentity.ModTileEntityTypes;
import thefloydman.linkingbooks.util.Reference;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Reference.MOD_ID)
public class LinkingBooks {

    public static final Logger LOGGER = LogManager.getLogger();

    public LinkingBooks() {

        final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(eventBus);
        ModItems.ITEMS.register(eventBus);
        ModFluids.FLUIDS.register(eventBus);
        ModEntityTypes.ENTITIES.register(eventBus);
        ModTileEntityTypes.TILE_ENTITIES.register(eventBus);
        ModContainerTypes.CONTAINERS.register(eventBus);
        LinkEffects.LINK_EFFECTS.register(eventBus);

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        ModNetworkHandler.registerAllMessages();
        LinkData.register();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.LINKING_BOOK.get(),
                    LinkingBookRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.DESCRIPTIVE_BOOK.get(),
                    DescriptiveBookRenderer::new);
        });

    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("examplemod", "helloworld", () -> {
            LOGGER.info("Hello world from the MDK");
            return "Hello world";
        });
    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}",
                event.getIMCStream().map(m -> m.getMessageSupplier().get()).collect(Collectors.toList()));
    }

}
