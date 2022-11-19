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

import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.registries.IForgeRegistry;
import thefloydman.linkingbooks.api.capability.ILinkData;

public abstract class LinkEffect {

    public static final String TAG_TYPE = "type";

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

    public static abstract class Type {

        public static final Logger LOGGER = LogManager.getLogger();
        public static IForgeRegistry<LinkEffect.Type> registry;

        /**
         * Convenience method that retrieves a LinkEffect.Type from the appropriate
         * registry.
         */
        public static LinkEffect.Type get(ResourceLocation resource) {
            if (registry == null) {
                LOGGER.info("Cannot find Link Effect Type registry. Returning null Link Effect Type.");
                return null;
            }
            return registry.getValue(resource);
        }

        /**
         * Deserializer to translate a Link Effect JSON file into a Link Effect object.
         * 
         * @param json The JSON object from the file in the datapack.
         * @return A Link Effect of this type.
         */
        public abstract LinkEffect fromJson(JsonObject json);

    }

}
