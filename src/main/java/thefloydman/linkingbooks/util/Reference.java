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
package thefloydman.linkingbooks.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;

public class Reference {

    public static final String MOD_ID = "linkingbooks";
    public static MinecraftServer server = null;

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

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static boolean isImmersivePortalsLoaded() {
        return isModLoaded("immersive_portals");
    }

    public static class BlockNames {
        public static final String INK = "ink";
        public static final String LINKING_LECTERN = "linking_lectern";
        public static final String MARKER_SWITCH = "marker_switch";
        public static final String NARA = "nara";
        public static final String LINK_TRANSLATOR = "link_translator";
        public static final String LINKING_PORTAL = "linking_portal";
        public static final String BOOKSHELF_STAIRS = "bookshelf_stairs";
    }

    public static class ItemNames {
        public static final String INK_BUCKET = "ink_bucket";
        public static final String BLANK_LINKNG_BOOK = "blank_linking_book";
        public static final String WRITTEN_LINKNG_BOOK = "written_linking_book";
        public static final String LINKING_PANEL = "linking_panel";
    }

    public static class EntityNames {
        public static final String LINKING_BOOK = "linking_book";
        public static final String LINKING_PORTAL = "linking_portal";
    }

    public static class TileEntityNames {
        public static final String LINKING_LECTERN = "linking_lectern";
        public static final String LINK_TRANSLATOR = "link_translator";
        public static final String MARKER_SWITCH = "marker_switch";
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

    public static class RecipeSerializerNames {
        public static final String BLANK_LINKING_BOOK = "blank_linking_book";
        public static final String LINK_EFFECT = "link_effect";
    }

    public static class LinkEffectNames {
        public static final String POISON_EFFECT = "poison_effect";
        public static final String INTRAAGE_LINKING = "intraage_linking";
        public static final String TETHERED = "tethered";
    }

    public static class Resources {
        public static final ResourceLocation INK_TEXTURE = getAsResourceLocation("block/ink_still");
        public static final ResourceLocation FLOWING_INK_TEXTURE = getAsResourceLocation("block/ink_flow");
        public static final ResourceLocation LINKING_BOOK_TEXTURE = getAsResourceLocation(
                "textures/entity/linking_book.png");
    }

}
