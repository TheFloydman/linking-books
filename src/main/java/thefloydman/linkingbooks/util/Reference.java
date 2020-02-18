package thefloydman.linkingbooks.util;

import net.minecraft.util.ResourceLocation;

public class Reference {

    public static final String MOD_ID = "linkingbooks";

    /**
     * Convenience method to make a ResourceLocation under this mod's domain.
     * 
     * @param path The path of the ResourceLocation.
     * @return A ResourceLocation with this mod's ID as the domain and the given
     *         parameter as the path.
     */
    public static ResourceLocation getAsResourceLocation(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static class BlockNames {
        public static final String INK_MIXER = "ink_mixer";
        public static final String INK = "ink_fluid_block";
    }

    public static class ItemNames {
        public static final String PLAIN_INK_BUCKET = "plain_ink_bucket";
    }

    public static class FluidNames {
        public static final String PLAIN_INK = "plain_ink";
        public static final String FLOWING_PLAIN_INK = "flowing_plain_ink";
    }

    public static class Resources {
        public static final ResourceLocation INK_TEXTURE = new ResourceLocation(MOD_ID, "block/ink_still");
        public static final ResourceLocation FLOWING_INK_TEXTURE = new ResourceLocation(MOD_ID, "block/ink_flow");
    }

}
