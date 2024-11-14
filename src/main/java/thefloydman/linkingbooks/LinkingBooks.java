package thefloydman.linkingbooks;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import thefloydman.linkingbooks.client.sound.ModSounds;
import thefloydman.linkingbooks.core.component.ModDataComponents;
import thefloydman.linkingbooks.linking.LinkEffectTypes;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.world.entity.ModEntityTypes;
import thefloydman.linkingbooks.world.inventory.ModMenuTypes;
import thefloydman.linkingbooks.world.item.ModCreativeModeTabs;
import thefloydman.linkingbooks.world.item.ModItems;
import thefloydman.linkingbooks.world.level.block.ModBlocks;
import thefloydman.linkingbooks.world.level.block.entity.ModBlockEntityTypes;

@Mod(Reference.MODID)
public class LinkingBooks {
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public LinkingBooks(IEventBus modEventBus, ModContainer modContainer) {

        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        LinkEffectTypes.LINK_EFFECT_TYPES.register(modEventBus);
        ModCreativeModeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        ModEntityTypes.ENTITIES.register(modEventBus);
        ModBlockEntityTypes.TILE_ENTITIES.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::commonSetup);

        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON, ModConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

}
