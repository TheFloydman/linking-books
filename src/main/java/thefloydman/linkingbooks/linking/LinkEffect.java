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
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import thefloydman.linkingbooks.util.Reference;

public record LinkEffect(LinkEffectType type) {

    public static final ResourceKey<Registry<LinkEffect>> REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Reference.MODID, Reference.RegistryKeyNames.LINK_EFFECT));

    public static final Codec<LinkEffect> CODEC = RecordCodecBuilder.create(
            codecBuilderInstance -> codecBuilderInstance.group(
                            ResourceLocation.CODEC.fieldOf("type")
                                    .xmap(resourceLocation -> LinkEffectTypes.REGISTRY.get(Reference.getAsResourceLocation(Reference.LinkEffectTypeNames.BASIC)), LinkEffectType::typeID)
                                    .forGetter(LinkEffect::type)
                    )
                    .apply(codecBuilderInstance, LinkEffect::new)
    );

}