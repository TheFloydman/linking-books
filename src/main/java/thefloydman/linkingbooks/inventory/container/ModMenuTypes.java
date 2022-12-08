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
package thefloydman.linkingbooks.inventory.container;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.util.Reference.ContainerNames;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS,
            Reference.MOD_ID);

    public static final RegistryObject<MenuType<LinkingBookContainer>> LINKING_BOOK = CONTAINERS
            .register(ContainerNames.LINKING_BOOK, () -> IForgeMenuType.create(LinkingBookContainer::new));

    public static final RegistryObject<MenuType<GuidebookContainer>> GUIDEBOOK = CONTAINERS
            .register(ContainerNames.GUIDEBOOK, () -> IForgeMenuType.create(GuidebookContainer::new));

}
