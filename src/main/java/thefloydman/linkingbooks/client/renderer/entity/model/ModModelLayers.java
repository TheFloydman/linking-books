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

package thefloydman.linkingbooks.client.renderer.entity.model;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import thefloydman.linkingbooks.util.Reference;

public class ModModelLayers {

    public static final ModelLayerLocation COVER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "main"),
            "cover");
    public static final ModelLayerLocation PAGES = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "main"),
            "pages");

}