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

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import thefloydman.linkingbooks.util.Reference;

public class LinkEffectTypes {

    public static final ResourceKey<Registry<LinkEffectType>> REGISTRY_KEY = ResourceKey.createRegistryKey(Reference.getAsResourceLocation(Reference.RegistryKeyNames.LINK_EFFECT_TYPE));
    public static final Registry<LinkEffectType> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY)
            .defaultKey(Reference.getAsResourceLocation(Reference.LinkEffectTypeNames.BASIC))
            .create();
    public static final DeferredRegister<LinkEffectType> LINK_EFFECT_TYPES = DeferredRegister
            .create(REGISTRY, Reference.MODID);

    public static final DeferredHolder<LinkEffectType, BasicLinkEffectType> BASIC = LINK_EFFECT_TYPES
            .register(Reference.LinkEffectTypeNames.BASIC, BasicLinkEffectType::new);

    public static final DeferredHolder<LinkEffectType, MobEffectLinkEffectType> MOB_EFFECT = LINK_EFFECT_TYPES
            .register(Reference.LinkEffectTypeNames.MOB_EFFECT, resourceLocation -> new MobEffectLinkEffectType(resourceLocation, MobEffects.HEAL, 0));

}