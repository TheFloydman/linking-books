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
package thefloydman.linkingbooks.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class ImageUtils {

    public static CompoundTag imageToNBT(NativeImage image) {
        CompoundTag compound = new CompoundTag();
        if (image != null) {
            compound.putInt("height", image.getHeight());
            compound.putInt("width", image.getWidth());
            List<Integer> pixels = new ArrayList<Integer>();
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    pixels.add(image.getPixelRGBA(x, y));
                }
            }
            compound.putIntArray("pixels", pixels);
        }
        return compound;
    }

    @Nullable
    public static NativeImage imageFromNBT(CompoundTag compound) {
        if (compound != null) {
            if (compound.contains("height", Tag.TAG_INT)) {
                if (compound.contains("width", Tag.TAG_INT)) {
                    if (compound.contains("pixels", Tag.TAG_INT_ARRAY)) {
                        int width = compound.getInt("width");
                        int height = compound.getInt("height");
                        NativeImage image = new NativeImage(width, height, false);
                        int[] pixels = compound.getIntArray("pixels");
                        int i = 0;
                        for (int y = 0; y < height && i < pixels.length; y++) {
                            for (int x = 0; x < width && i < pixels.length; x++) {
                                image.setPixelRGBA(x, y, pixels[i++]);
                            }
                        }
                        return image;
                    }
                }
            }
        }
        return null;
    }

}
