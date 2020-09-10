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
        public static final String INK = "ink";
    }

    public static class ItemNames {
        public static final String INK_BUCKET = "ink_bucket";
        public static final String BLACK_BLANK_LINKNG_BOOK = "black_blank_linking_book";
        public static final String BLACK_WRITTEN_LINKNG_BOOK = "black_written_linking_book";
    }

    public static class EntityNames {
        public static final String LINKING_BOOK = "linking_book";
        public static final String DESCRIPTIVE_BOOK = "descriptive_book";
    }

    public static class TileEntityNames {
        public static final String INK_MIXER = "ink_mixer";
    }

    public static class FluidNames {
        public static final String INK = "ink";
        public static final String FLOWING_INK = "flowing_ink";
    }

    public static class ContainerNames {
        public static final String LINKING_BOOK = "linking_book";
    }

    public static class CapabilityNames {
        public static final ResourceLocation LINK_DATA = getAsResourceLocation("link_data");
    }

    public static class LinkEffectNames {
        public static final String POISON_EFFECT = "poison_effect";
    }

    public static class Resources {
        public static final ResourceLocation INK_TEXTURE = getAsResourceLocation("block/ink_still");
        public static final ResourceLocation FLOWING_INK_TEXTURE = getAsResourceLocation("block/ink_flow");
        public static final ResourceLocation LINKING_BOOK_TEXTURE = getAsResourceLocation(
                "textures/entity/linking_book.png");
    }

}
