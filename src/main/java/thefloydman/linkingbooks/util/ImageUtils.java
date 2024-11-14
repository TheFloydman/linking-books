package thefloydman.linkingbooks.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

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

    public static final StreamCodec<ByteBuf, NativeImage> NATIVE_IMAGE_STREAM_CODEC = ByteBufCodecs.fromCodec(NATIVE_IMAGE_CODEC);

}