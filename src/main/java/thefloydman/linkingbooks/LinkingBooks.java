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
package thefloydman.linkingbooks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import thefloydman.linkingbooks.block.ModBlocks;
import thefloydman.linkingbooks.blockentity.ModBlockEntityTypes;
import thefloydman.linkingbooks.client.gui.screen.LinkingBookScreen;
import thefloydman.linkingbooks.client.sound.ModSounds;
import thefloydman.linkingbooks.config.ModConfig;
import thefloydman.linkingbooks.entity.ModEntityTypes;
import thefloydman.linkingbooks.fluid.ModFluids;
import thefloydman.linkingbooks.inventory.container.ModMenuTypes;
import thefloydman.linkingbooks.item.LinkingBookItem;
import thefloydman.linkingbooks.item.ModItems;
import thefloydman.linkingbooks.item.crafting.ModRecipeSerializers;
import thefloydman.linkingbooks.linking.LinkEffectTypes;
import thefloydman.linkingbooks.network.ModNetworkHandler;
import thefloydman.linkingbooks.util.Reference;

@Mod(Reference.MOD_ID)
public class LinkingBooks {

    public static final Logger LOGGER = LogManager.getLogger();

    public LinkingBooks() {

        final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(eventBus);
        ModItems.ITEMS.register(eventBus);
        ModRecipeSerializers.RECIPES.register(eventBus);
        ModFluids.FLUIDS.register(eventBus);
        ModEntityTypes.ENTITIES.register(eventBus);
        ModBlockEntityTypes.TILE_ENTITIES.register(eventBus);
        ModMenuTypes.CONTAINERS.register(eventBus);
        ModSounds.SOUNDS.register(eventBus);
        LinkEffectTypes.LINK_EFFECT_TYPES.register(eventBus);

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
        event.enqueueWork(() -> {
            ModNetworkHandler.registerAllMessages();
        });
    }

    private void clientSetup(final FMLClientSetupEvent event) {

        event.enqueueWork(() -> {
            // Register containers.
            MenuScreens.register(ModMenuTypes.LINKING_BOOK.get(), LinkingBookScreen::new);

            // Register ItemColors.
            ItemColors itemColors = Minecraft.getInstance().getItemColors();
            itemColors.register((stack, index) -> LinkingBookItem.getColor(stack, index),
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

            // Register block layer renderers.
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.LINKING_PORTAL.get(), RenderType.translucent());

        });

    }

}
