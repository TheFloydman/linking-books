/*******************************************************************************
 * Linking Books
 * Copyright (C) 2021  TheFloydman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You can reach TheFloydman on Discord at Floydman#7171.
 *******************************************************************************/
package thefloydman.linkingbooks.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.api.linking.LinkEffect;
import thefloydman.linkingbooks.blockentity.ModBlockEntityTypes;
import thefloydman.linkingbooks.client.gui.book.GuiBookManager;
import thefloydman.linkingbooks.client.renderer.entity.LinkingBookRenderer;
import thefloydman.linkingbooks.client.renderer.entity.model.LinkingBookCoverModel;
import thefloydman.linkingbooks.client.renderer.entity.model.LinkingBookPagesModel;
import thefloydman.linkingbooks.client.renderer.entity.model.ModModelLayers;
import thefloydman.linkingbooks.client.renderer.tileentity.LinkTranslatorRenderer;
import thefloydman.linkingbooks.client.renderer.tileentity.LinkingLecternRenderer;
import thefloydman.linkingbooks.client.renderer.tileentity.MarkerSwitchRenderer;
import thefloydman.linkingbooks.entity.ModEntityTypes;
import thefloydman.linkingbooks.item.LinkingBookItem;
import thefloydman.linkingbooks.item.ModItems;
import thefloydman.linkingbooks.util.Reference;

@EventBusSubscriber(modid = Reference.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventHandler {

    /**
     * Use to create new registries.
     */
    @SubscribeEvent
    public static void createNewRegistries(NewRegistryEvent event) {
        RegistryBuilder<LinkEffect.Type> linkEffectTypeRegistryBuilder = new RegistryBuilder<LinkEffect.Type>();
        linkEffectTypeRegistryBuilder.setName(Reference.RegistryNames.LINK_EFFECT_TYPES);
        event.create(linkEffectTypeRegistryBuilder, (registry) -> {
            LinkEffect.Type.registry = registry;
        });
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
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ILinkData.class);
    }

    @SubscribeEvent
    public static void register(RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.PAGES, LinkingBookPagesModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.COVER, LinkingBookCoverModel::createBodyLayer);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, index) -> LinkingBookItem.getColor(stack, index),
                ModItems.BLACK_BLANK_LINKING_BOOK.get(), ModItems.BLUE_BLANK_LINKING_BOOK.get(),
                ModItems.BROWN_BLANK_LINKING_BOOK.get(), ModItems.CYAN_BLANK_LINKING_BOOK.get(),
                ModItems.GRAY_BLANK_LINKING_BOOK.get(), ModItems.GREEN_BLANK_LINKING_BOOK.get(),
                ModItems.LIGHT_BLUE_BLANK_LINKING_BOOK.get(), ModItems.LIGHT_GRAY_BLANK_LINKING_BOOK.get(),
                ModItems.LIME_BLANK_LINKING_BOOK.get(), ModItems.MAGENTA_BLANK_LINKING_BOOK.get(),
                ModItems.ORANGE_BLANK_LINKING_BOOK.get(), ModItems.PINK_BLANK_LINKING_BOOK.get(),
                ModItems.PURPLE_BLANK_LINKING_BOOK.get(), ModItems.RED_BLANK_LINKING_BOOK.get(),
                ModItems.WHITE_BLANK_LINKING_BOOK.get(), ModItems.YELLOW_BLANK_LINKING_BOOK.get(),
                ModItems.BLACK_WRITTEN_LINKING_BOOK.get(), ModItems.BLUE_WRITTEN_LINKING_BOOK.get(),
                ModItems.BROWN_WRITTEN_LINKING_BOOK.get(), ModItems.CYAN_WRITTEN_LINKING_BOOK.get(),
                ModItems.GRAY_WRITTEN_LINKING_BOOK.get(), ModItems.GREEN_WRITTEN_LINKING_BOOK.get(),
                ModItems.LIGHT_BLUE_WRITTEN_LINKING_BOOK.get(), ModItems.LIGHT_GRAY_WRITTEN_LINKING_BOOK.get(),
                ModItems.LIME_WRITTEN_LINKING_BOOK.get(), ModItems.MAGENTA_WRITTEN_LINKING_BOOK.get(),
                ModItems.ORANGE_WRITTEN_LINKING_BOOK.get(), ModItems.PINK_WRITTEN_LINKING_BOOK.get(),
                ModItems.PURPLE_WRITTEN_LINKING_BOOK.get(), ModItems.RED_WRITTEN_LINKING_BOOK.get(),
                ModItems.WHITE_WRITTEN_LINKING_BOOK.get(), ModItems.YELLOW_WRITTEN_LINKING_BOOK.get());
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
