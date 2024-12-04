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

package thefloydman.linkingbooks.linking;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import thefloydman.linkingbooks.component.LinkData;

import javax.annotation.Nonnull;

public interface LinkEffectType {

    <T extends LinkEffectType> Codec<T> codec();

    @Nonnull
    ResourceLocation typeID();

    /**
     * Fires before entity changes dimensions and before onLinkStart has been called
     * for any LinkEffect.
     *
     * @param entity   The Entity that is linking.
     * @param linkData The LinkData for the link.
     * @return Whether the link should proceed. If false, entity will not link.
     */
    default boolean canStartLink(Entity entity, LinkData linkData) {
        return true;
    }

    /**
     * Fires after entity changes dimensions and onLinkStart has been called for
     * every LinkEffect but before onLinkEnd has been called for any LinkEffect.
     *
     * @param entity   The Entity that is linking.
     * @param linkData The LinkData for the link.
     * @return Whether the link should proceed successfully. If false, entity will
     * be returned to origin.
     */
    default boolean canFinishLink(Entity entity, LinkData linkData) {
        return true;
    }

    /**
     * Fires before entity changes dimensions.
     *
     * @param entity   The Entity that is linking.
     * @param linkData The LinkData for the link.
     */
    default void onLinkStart(Entity entity, LinkData linkData) {
    }

    /**
     * Fires after entity changes dimensions.
     *
     * @param entity   The Entity that is linking.
     * @param linkData The LinkData for the link.
     */
    default void onLinkEnd(Entity entity, LinkData linkData) {
    }

}