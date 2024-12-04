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

package thefloydman.linkingbooks.crafting;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;
import thefloydman.linkingbooks.Reference;

import java.util.function.Supplier;

public class ModRecipeTypes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Reference.MODID);

    public static final Supplier<RecipeType<LinkEffectRecipe>> RIGHT_CLICK_BLOCK =
            RECIPE_TYPES.register(
                    Reference.RecipeSerializerNames.LINK_EFFECT,
                    () -> RecipeType.<LinkEffectRecipe>simple(Reference.getAsResourceLocation(Reference.RecipeSerializerNames.LINK_EFFECT))
            );

}