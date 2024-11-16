/*
 * Copyright (c) 2019-2024 Dan Floyd ("TheFloydman").
 *
 * This file is part of Linking Books.
 *
 * Linking Books is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Linking Books is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with Linking Books. If not, see <https://www.gnu.org/licenses/>.
 */

package thefloydman.linkingbooks.core.component;

import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thefloydman.linkingbooks.data.LinkData;
import thefloydman.linkingbooks.util.Reference;

public class ModDataComponents {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Reference.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LinkData>> LINK_DATA = DATA_COMPONENTS.registerComponentType(
            Reference.DataComponentNames.LINK_DATA, builder -> builder
                    .persistent(LinkData.CODEC)
                    .networkSynchronized(LinkData.STREAM_CODEC)
    );

}
