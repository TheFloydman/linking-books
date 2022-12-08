/*******************************************************************************
 * Copyright 2019-2022 Dan Floyd ("TheFloydman")
 *
 * This file is part of Linking Books.
 *
 * Linking Books is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Linking Books is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Linking Books. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package thefloydman.linkingbooks.api.capability;

import java.util.Set;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import thefloydman.linkingbooks.api.linking.LinkEffect;

public interface ILinkData {

    public void setDimension(ResourceLocation dimension);

    public ResourceLocation getDimension();

    public void setPosition(BlockPos position);

    public BlockPos getPosition();

    public void setRotation(float rotation);

    public float getRotation();

    public void setUUID(UUID uuid);

    public UUID getUUID();

    public void setLinkEffectsRL(Set<ResourceLocation> effects);

    public void setLinkEffectsLE(Set<LinkEffect> effects);

    public Set<ResourceLocation> getLinkEffectsAsRL();

    public Set<LinkEffect> getLinkEffectsAsLE();

    public boolean addLinkEffect(ResourceLocation effect);

    public boolean addLinkEffect(LinkEffect effect);

    public boolean removeLinkEffect(ResourceLocation effect);

    public FriendlyByteBuf write(FriendlyByteBuf buffer);

    public void read(FriendlyByteBuf buffer);

    public CompoundTag writeToShareTag(CompoundTag nbt);

    public void readFromShareTag(CompoundTag nbt);

}
