/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2019-2024 Dan Floyd ("TheFloydman").
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package thefloydman.linkingbooks;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import thefloydman.linkingbooks.client.sound.ModSounds;
import thefloydman.linkingbooks.core.component.ModDataComponents;
import thefloydman.linkingbooks.linking.LinkEffectTypes;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.world.entity.ModEntityTypes;
import thefloydman.linkingbooks.world.inventory.ModMenuTypes;
import thefloydman.linkingbooks.world.item.ModCreativeModeTabs;
import thefloydman.linkingbooks.world.item.ModItems;
import thefloydman.linkingbooks.world.item.crafting.ModRecipeSerializers;
import thefloydman.linkingbooks.world.item.crafting.ModRecipeTypes;
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
        ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
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
