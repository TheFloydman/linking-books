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

package thefloydman.linkingbooks.menutype;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import thefloydman.linkingbooks.Reference;

import java.util.function.Supplier;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU,
            Reference.MODID);

    public static final Supplier<MenuType<LinkingBookMenuType>> LINKING_BOOK = MENU_TYPES
            .register(Reference.ContainerNames.LINKING_BOOK, () -> IMenuTypeExtension.create(LinkingBookMenuType::new));

    public static final Supplier<MenuType<ReltoBookMenuType>> RELTO_BOOK = MENU_TYPES
            .register(Reference.ContainerNames.RELTO_BOOK, () -> IMenuTypeExtension.create(ReltoBookMenuType::new));

    public static final Supplier<MenuType<GuidebookMenuType>> GUIDEBOOK = MENU_TYPES
            .register(Reference.ContainerNames.GUIDEBOOK, () -> new MenuType<>(GuidebookMenuType::new, FeatureFlags.DEFAULT_FLAGS));

}