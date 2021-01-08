package thefloydman.linkingbooks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import thefloydman.linkingbooks.block.ModBlocks;
import thefloydman.linkingbooks.capability.ColorCapability;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.client.gui.screen.LinkingBookScreen;
import thefloydman.linkingbooks.client.renderer.entity.LinkingBookRenderer;
import thefloydman.linkingbooks.client.renderer.tileentity.LinkingLecternRenderer;
import thefloydman.linkingbooks.config.ModConfig;
import thefloydman.linkingbooks.entity.ModEntityTypes;
import thefloydman.linkingbooks.fluid.ModFluids;
import thefloydman.linkingbooks.inventory.container.ModContainerTypes;
import thefloydman.linkingbooks.item.LinkingBookItem;
import thefloydman.linkingbooks.item.ModItems;
import thefloydman.linkingbooks.item.crafting.ModRecipeSerializers;
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
        ModRecipeSerializers.RECIPES.register(eventBus);

        // Register the setup methods.
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register configs.
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ModConfig.SPEC,
                Reference.MOD_ID + ".toml");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModNetworkHandler.registerAllMessages();
        LinkData.register();
        ColorCapability.register();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        // Register Entity renderers.
        RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.LINKING_BOOK.get(), LinkingBookRenderer::new);

        // Register TileEntity renderers.
        ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.LINKING_LECTERN.get(), LinkingLecternRenderer::new);

        // Register containers.
        ScreenManager.registerFactory(ModContainerTypes.LINKING_BOOK.get(), LinkingBookScreen::new);

        // Register ItemColors.
        ItemColors itemColors = Minecraft.getInstance().getItemColors();
        itemColors.register((stack, index) -> LinkingBookItem.getColor(stack, index),
                ModItems.BLANK_LINKING_BOOK.get());
        itemColors.register((stack, index) -> LinkingBookItem.getColor(stack, index),
                ModItems.WRITTEN_LINKING_BOOK.get());

    }

}
