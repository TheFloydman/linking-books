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
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import thefloydman.linkingbooks.component.LinkData;
import thefloydman.linkingbooks.Reference;

import javax.annotation.Nonnull;

/**
 * A Link Effect that applies a MobEffect (Potion Effect) for a specified duration.
 */
public record MobEffectLinkEffectType(@Nonnull ResourceLocation typeID, Holder<MobEffect> effect,
                                      int ticks) implements LinkEffectType {

    public static final Codec<MobEffectLinkEffectType> CODEC = RecordCodecBuilder.create(
            codecBuilderInstance -> codecBuilderInstance.group(
                            ResourceLocation.CODEC.fieldOf("type").forGetter(MobEffectLinkEffectType::typeID),
                            Options.CODEC.fieldOf("options").forGetter(mobEffectLinkEffectType -> new Options(mobEffectLinkEffectType.effect(), mobEffectLinkEffectType.ticks()))
                    )
                    .apply(codecBuilderInstance, (resourceLocation, options) -> new MobEffectLinkEffectType(resourceLocation, options.effect(), options.ticks()))
    );

    private record Options(Holder<MobEffect> effect, int ticks) {
        private static final Codec<Options> CODEC = RecordCodecBuilder.create(
                codecBuilderInstance -> codecBuilderInstance.group(
                                MobEffect.CODEC.fieldOf("effect").forGetter(Options::effect),
                                Codec.INT.fieldOf("ticks").forGetter(Options::ticks)
                        )
                        .apply(codecBuilderInstance, Options::new)
        );
    }

    @Override
    public @Nonnull Codec<MobEffectLinkEffectType> codec() {
        return CODEC;
    }

    @Override
    public void onLinkEnd(Entity entity, LinkData linkData) {
        System.out.println(this.effect);
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).addEffect(new MobEffectInstance(this.effect(), this.ticks()));
        }
    }

    @Override
    public @Nonnull ResourceLocation typeID() {
        return Reference.getAsResourceLocation(Reference.LinkEffectTypeNames.MOB_EFFECT);
    }

}