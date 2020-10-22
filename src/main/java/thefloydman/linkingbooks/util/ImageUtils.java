package thefloydman.linkingbooks.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants.NBT;

public class ImageUtils {

    public static CompoundNBT imageToNBT(NativeImage image) {
        CompoundNBT compound = new CompoundNBT();
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
    public static NativeImage imageFromNBT(CompoundNBT compound) {
        if (compound != null) {
            if (compound.contains("height", NBT.TAG_INT)) {
                if (compound.contains("width", NBT.TAG_INT)) {
                    if (compound.contains("pixels", NBT.TAG_INT_ARRAY)) {
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
