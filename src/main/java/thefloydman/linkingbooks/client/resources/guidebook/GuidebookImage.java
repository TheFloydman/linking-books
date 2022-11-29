/*******************************************************************************
 * Linking Books
 * Copyright (C) 2021  TheFloydman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You can reach TheFloydman on Discord at Floydman#7171.
 *******************************************************************************/
package thefloydman.linkingbooks.client.resources.guidebook;

import net.minecraft.resources.ResourceLocation;

public class GuidebookImage {

    public ResourceLocation resourceLocation;
    public float scale;
    public int sourceWidth;
    public int sourceHeight;

    public GuidebookImage(ResourceLocation resourceLocation, float scale, int sourceWidth, int sourceHeight) {
        this.resourceLocation = resourceLocation;
        this.scale = scale;
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
    }

}
