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

    /**
     * Fires before entity changes dimensions and before onLinkStart has been called
     * for any LinkEffect.
     * 
     * @param entity The Entity that is linking.
     * @param linkData The LinkDataComponent for the link.
     * @return Whether the link should proceed. If false, entity will not link.
     */
    public boolean canStartLink(Entity entity, ILinkData linkData) {
        return true;
    }

    /**
     * Fires after entity changes dimensions and onLinkStart has been called for
     * every LinkEffect but before onLinkEnd has been called for any LinkEffect.
     * 
     * @param entity The Entity that is linking.
     * @param linkData The LinkDataComponent for the link.
     * @return Whether the link should proceed successfully. If false, entity will
     *         be returned to origin.
     */
    public boolean canFinishLink(Entity entity, ILinkData linkData) {
        return true;
    }

    /**
     * Fires before entity changes dimensions.
     * 
     * @param entity The Entity that is linking.
     * @param linkData The LinkDataComponent for the link.
     */
    public void onLinkStart(Entity entity, ILinkData linkData) {
    }

    /**
     * Fires after entity changes dimensions.
     * 
     * @param entity The Entity that is linking.
     * @param linkData The LinkDataComponent for the link.
     */
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
