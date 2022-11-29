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
package thefloydman.linkingbooks.capability;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.api.linking.LinkEffect;
import thefloydman.linkingbooks.linking.LinkEffectManager;
import thefloydman.linkingbooks.util.Reference;

public class LinkData implements ILinkData, INBTSerializable<CompoundTag> {

    private ResourceLocation dimension = new ResourceLocation("minecraft:overworld");
    private BlockPos position = Reference.server == null ? BlockPos.ZERO
            : Reference.server.overworld().getSharedSpawnPos();
    private float rotation = 0.0F;
    private UUID uuid = UUID.randomUUID();
    private Set<ResourceLocation> linkEffects = new HashSet<ResourceLocation>();

    @Override
    public void setDimension(ResourceLocation dimension) {
        this.dimension = dimension;
    }

    @Override
    public ResourceLocation getDimension() {
        return this.dimension;
    }

    @Override
    public void setPosition(BlockPos position) {
        this.position = position;
    }

    @Override
    public BlockPos getPosition() {
        return this.position;
    }

    @Override
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    @Override
    public float getRotation() {
        return this.rotation;
    }

    @Override
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public void setLinkEffectsRL(Set<ResourceLocation> effects) {
        this.linkEffects = effects;
    }

    @Override
    public void setLinkEffectsLE(Set<LinkEffect> effects) {
        this.setLinkEffectsRL(
                effects.stream().map(effect -> LinkEffectManager.getKey(effect)).collect(Collectors.toSet()));
    }

    @Override
    public Set<ResourceLocation> getLinkEffectsAsRL() {
        return this.linkEffects;
    }

    @Override
    public Set<LinkEffect> getLinkEffectsAsLE() {
        return this.linkEffects.stream().map(resource -> LinkEffectManager.get(resource)).collect(Collectors.toSet());
    }

    @Override
    public boolean addLinkEffect(ResourceLocation effect) {
        return this.linkEffects.add(effect);
    }

    @Override
    public boolean addLinkEffect(LinkEffect effect) {
        return this.linkEffects.add(LinkEffectManager.getKey(effect));
    }

    @Override
    public boolean removeLinkEffect(ResourceLocation effect) {
        return this.linkEffects.remove(effect);
    }

    @Override
    public FriendlyByteBuf write(FriendlyByteBuf buffer) {
        CompoundTag compound = this.serializeNBT();
        buffer.writeNbt(compound);
        return buffer;
    }

    @Override
    public void read(FriendlyByteBuf buffer) {
        this.deserializeNBT(buffer.readNbt());
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("dimension",
                this.getDimension() == null ? "minecraft:overworld" : this.getDimension().toString());
        nbt.put("position",
                NbtUtils.writeBlockPos(this.getPosition() == null ? new BlockPos(0, 0, 0) : this.getPosition()));
        nbt.putFloat("rotation", this.getRotation());
        ListTag effectsList = new ListTag();
        for (ResourceLocation effect : this.getLinkEffectsAsRL()) {
            effectsList.add(StringTag.valueOf(effect.toString()));
        }
        nbt.putUUID("uuid", this.getUUID());
        nbt.put("effects", effectsList);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("dimension", Tag.TAG_STRING)) {
            this.setDimension(new ResourceLocation(nbt.getString("dimension")));
        }
        if (nbt.contains("position", Tag.TAG_COMPOUND)) {
            this.setPosition(NbtUtils.readBlockPos(nbt.getCompound("position")));
        }
        if (nbt.contains("rotation", Tag.TAG_FLOAT)) {
            this.setRotation(nbt.getFloat("rotation"));
        }
        if (nbt.contains("uuid", Tag.TAG_INT_ARRAY)) {
            this.setUUID(nbt.getUUID("uuid"));
        }
        if (nbt.contains("effects", Tag.TAG_LIST)) {
            for (Tag item : nbt.getList("effects", Tag.TAG_STRING)) {
                this.addLinkEffect(new ResourceLocation(((StringTag) item).getAsString()));
            }
        }
    }

    @Override
    public CompoundTag writeToShareTag(CompoundTag nbt) {
        CompoundTag tag = new CompoundTag();
        if (nbt != null) {
            tag = nbt.copy();
        }
        tag.put("link_data", this.serializeNBT());
        return tag;
    }

    @Override
    public void readFromShareTag(CompoundTag nbt) {
        if (nbt != null && nbt.contains("link_data", Tag.TAG_COMPOUND)) {
            this.deserializeNBT(nbt.getCompound("link_data"));
        }
    }

}
