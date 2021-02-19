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
package thefloydman.linkingbooks.api.linking;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import thefloydman.linkingbooks.api.capability.ILinkData;

public abstract class LinkEffect extends ForgeRegistryEntry<LinkEffect> {

    public static final Logger LOGGER = LogManager.getLogger();

    public void onLinkStart(Entity entity, ILinkData linkData) {
    }

    public void onLinkEnd(Entity entity, ILinkData linkData) {
    }

    /**
     * Convenience method that retrieves a LinkEffect from the appropriate registry.
     */
    public static LinkEffect get(ResourceLocation resource) {
        IForgeRegistry<LinkEffect> registry = GameRegistry.findRegistry(LinkEffect.class);
        if (registry == null) {
            LOGGER.info("Cannot find LinkEffect registry. Returning null LinkEffect.");
            return null;
        }
        return registry.getValue(resource);
    }

}
