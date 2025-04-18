/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2025 Dan Floyd ("TheFloydman").
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

package thefloydman.linkingbooks.world.sky;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import thefloydman.linkingbooks.Reference;

import javax.annotation.Nonnull;
import java.util.List;

public record SkyObject(
        ResourceLocation name,
        int providedLight,
        @Nonnull ResourceLocation texture,
        long rotationPeriod,
        float axisTilt,
        long orbitPeriod,
        float orbitTilt,
        float orbitRadius,
        float apparentRadius,
        boolean hasColor,
        int color,
        @Nonnull List<SkyObject> children
) {

    public static final Codec<SkyObject> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("name", ResourceLocation.withDefaultNamespace("none")).forGetter(SkyObject::name),
            Codec.INT.optionalFieldOf("provided_light", 0).forGetter(SkyObject::providedLight),
            ResourceLocation.CODEC.optionalFieldOf("texture", Reference.getAsResourceLocation("none")).forGetter(SkyObject::texture),
            Codec.LONG.optionalFieldOf("rotation_period", 0L).forGetter(SkyObject::rotationPeriod),
            Codec.FLOAT.optionalFieldOf("axis_tilt", 0.0F).forGetter(SkyObject::axisTilt),
            Codec.LONG.optionalFieldOf("orbit_period", 0L).forGetter(SkyObject::orbitPeriod),
            Codec.FLOAT.optionalFieldOf("orbit_tilt", 0.0F).forGetter(SkyObject::orbitTilt),
            Codec.FLOAT.optionalFieldOf("orbit_radius", 1.0F).forGetter(SkyObject::orbitRadius),
            Codec.FLOAT.optionalFieldOf("apparent_radius", 30.0F).forGetter(SkyObject::apparentRadius),
            Codec.BOOL.optionalFieldOf("has_color", false).forGetter(SkyObject::hasColor),
            Codec.INT.optionalFieldOf("color", 0).forGetter(SkyObject::color),
            Codec.list(Codec.lazyInitialized(() -> SkyObject.CODEC)).optionalFieldOf("children", List.of()).forGetter(SkyObject::children)
    ).apply(instance, SkyObject::new));

    public static final SkyObject DUMMY = new SkyObject(Reference.getAsResourceLocation("none"), 0, Reference.getAsResourceLocation("none"), 0L, 0.0F, 0L, 0.0F, 1.0F, 1.0F, false, 0, List.of());

    public static SkyObject self(int providedLight, long rotationPeriod, float axisTilt, long orbitPeriod, float orbitTilt, float orbitRadius, List<SkyObject> children) {
        return new SkyObject(Reference.getAsResourceLocation("self"), providedLight, Reference.getAsResourceLocation("none"), rotationPeriod, axisTilt, orbitPeriod, orbitTilt, orbitRadius, 1.0F, false, 0, children);
    }

}