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

package thefloydman.linkingbooks.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ImageUtils {

    public static final Codec<NativeImage> NATIVE_IMAGE_CODEC = RecordCodecBuilder.create(
            codecBuilderInstance -> codecBuilderInstance.group(
                            Codec.INT.fieldOf("width").forGetter(NativeImage::getWidth),
                            Codec.INT.fieldOf("height").forGetter(NativeImage::getHeight),
                            Codec.INT_STREAM.fieldOf("pixels").forGetter(image -> {
                                List<Integer> pixels = new ArrayList<Integer>();
                                for (int y = 0; y < image.getHeight(); y++) {
                                    for (int x = 0; x < image.getWidth(); x++) {
                                        pixels.add(image.getPixelRGBA(x, y));
                                    }
                                }
                                return pixels.stream().mapToInt(Integer::intValue);
                            })
                    )
                    .apply(codecBuilderInstance, (width, height, pixelsIntStream) -> {
                        int[] pixels = pixelsIntStream.toArray();
                        NativeImage image = new NativeImage(width, height, false);
                        int i = 0;
                        for (int y = 0; (y < height) && (i < pixels.length); y++) {
                            for (int x = 0; x < width && i < pixels.length; x++, i++) {
                                image.setPixelRGBA(x, y, pixels[i]);
                            }
                        }
                        return image;
                    })
    );

}