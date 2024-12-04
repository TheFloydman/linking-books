/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2024 Dan Floyd ("TheFloydman").
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
import net.minecraft.resources.ResourceLocation;
import thefloydman.linkingbooks.Reference;

import javax.annotation.Nonnull;

/**
 * A Link Effect only containing a name.
 */
public record BasicLinkEffectType(@Nonnull ResourceLocation typeID) implements LinkEffectType {

    @Override
    public Codec<BasicLinkEffectType> codec() {
        return RecordCodecBuilder.create(
                codecBuilderInstance -> codecBuilderInstance.group(
                                ResourceLocation.CODEC.fieldOf("type").forGetter(BasicLinkEffectType::typeID)
                        )
                        .apply(codecBuilderInstance, BasicLinkEffectType::new)
        );
    }

    @Override
    public @Nonnull ResourceLocation typeID() {
        return Reference.getAsResourceLocation(Reference.LinkEffectTypeNames.BASIC);
    }

}