package thefloydman.linkingbooks.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import thefloydman.linkingbooks.client.gui.book.GuiBookManager;
import thefloydman.linkingbooks.client.renderer.entity.model.LinkingBookCoverModel;
import thefloydman.linkingbooks.client.renderer.entity.model.LinkingBookPagesModel;
import thefloydman.linkingbooks.client.renderer.entity.model.ModModelLayers;
import thefloydman.linkingbooks.linking.LinkEffect;
import thefloydman.linkingbooks.linking.LinkEffectTypes;
import thefloydman.linkingbooks.network.LinkMessage;
import thefloydman.linkingbooks.network.SaveLinkingPanelImageMessage;
import thefloydman.linkingbooks.network.TakeScreenshotForLinkingBookMessage;
import thefloydman.linkingbooks.util.LinkingUtils;
import thefloydman.linkingbooks.world.entity.ModEntityTypes;
import thefloydman.linkingbooks.world.inventory.ModMenuTypes;
import thefloydman.linkingbooks.world.item.ModItems;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.client.renderer.entity.LinkingBookRenderer;
import thefloydman.linkingbooks.client.gui.screen.LinkingBookScreen;
import thefloydman.linkingbooks.client.gui.screen.GuidebookScreen;
import thefloydman.linkingbooks.world.level.block.entity.ModBlockEntityTypes;
import thefloydman.linkingbooks.client.renderer.blockentity.LinkingLecternRenderer;
import thefloydman.linkingbooks.client.renderer.blockentity.LinkTranslatorRenderer;
import thefloydman.linkingbooks.client.renderer.blockentity.MarkerSwitchRenderer;

@EventBusSubscriber(modid = Reference.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventHandler {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(LinkingUtils::getLinkingBookColor, ModItems.BLANK_LINKING_BOOK, ModItems.WRITTEN_LINKING_BOOK);
    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                TakeScreenshotForLinkingBookMessage.TYPE,
                TakeScreenshotForLinkingBookMessage.STREAM_CODEC,
                TakeScreenshotForLinkingBookMessage::handle
        );
        registrar.playToServer(
                SaveLinkingPanelImageMessage.TYPE,
                SaveLinkingPanelImageMessage.STREAM_CODEC,
                SaveLinkingPanelImageMessage::handle
        );
        registrar.playToServer(
                LinkMessage.TYPE,
                LinkMessage.STREAM_CODEC,
                LinkMessage::handle
        );
    }

    @SubscribeEvent
    static void registerRegistries(NewRegistryEvent event) {
        event.register(LinkEffectTypes.REGISTRY);
    }

    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(LinkEffect.REGISTRY_KEY, LinkEffect.CODEC, LinkEffect.CODEC);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {

        // Entities
        event.registerEntityRenderer(ModEntityTypes.LINKING_BOOK.get(), LinkingBookRenderer::new);

        // Block entities
        event.registerBlockEntityRenderer(ModBlockEntityTypes.LINKING_LECTERN.get(), LinkingLecternRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.LINK_TRANSLATOR.get(), LinkTranslatorRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.MARKER_SWITCH.get(), MarkerSwitchRenderer::new);

    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.LINKING_BOOK.get(), LinkingBookScreen::new);
        event.register(ModMenuTypes.GUIDEBOOK.get(), GuidebookScreen::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.PAGES, LinkingBookPagesModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.COVER, LinkingBookCoverModel::createBodyLayer);
    }

    /**
     * For loading/unloading assets.
     */
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void addReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new GuiBookManager());
    }

}