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
    }

}
