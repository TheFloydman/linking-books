/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2024 Dan Floyd ("TheFloydman").
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

package thefloydman.linkingbooks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Function;

public class Reference {

    public static final String MODID = "linkingbooks";
    public static MinecraftServer server = null;

    /**
     * Convenience method to make a ResourceLocation under this mod's domain.
     *
     * @param path The path of the ResourceLocation.
     * @return A ResourceLocation with this mod's ID as the domain and the given
     * parameter as the path.
     */
    public static ResourceLocation getAsResourceLocation(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static boolean isImmersivePortalsLoaded() {
        return isModLoaded("immersive_portals_core");
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
        public static final String GUIDEBOOK = "guidebook";
        public static final String BLANK_LINKING_BOOK = "blank_linking_book";
        public static final String WRITTEN_LINKING_BOOK = "written_linking_book";
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

    public static class FluidTypeNames {
        public static final String INK = "ink";
    }

    public static class ContainerNames {
        public static final String LINKING_BOOK = "linking_book";
        public static final String GUIDEBOOK = "guidebook";
    }

    public static class CapabilityNames {
        public static final ResourceLocation LINK_DATA = getAsResourceLocation("link_data");
    }

    public static class DataComponentNames {
        public static final String LINK_DATA = "link_data";
    }

    public static class RecipeSerializerNames {
        public static final String LINK_EFFECT = "link_effect";
    }

    public static class LinkEffectTypeNames {
        public static final String MOB_EFFECT = "mob_effect";
        public static final String BASIC = "basic";
    }

    public static class SoundNames {
        public static final String PAGEFLIP_FORWARD = "pageflip_forward";
        public static final String PAGEFLIP_BACK = "pageflip_back";
        public static final String BOOK_OPEN = "book_open";
        public static final String BOOK_CLOSE = "book_close";
        public static final String LINK = "link";
    }

    public static class RegistryKeyNames {
        public static final String LINK_EFFECT_TYPE = "linkeffecttype";
        public static final String LINK_EFFECT = "linkeffect";
    }

    public static class RegistryNames {
        public static final ResourceLocation LINK_EFFECT_TYPE = getAsResourceLocation("link_effect_type");
    }

    public static class CreativeModeTabNames {
        public static final ResourceLocation LINKING_BOOKS = getAsResourceLocation("main");
    }

    public static class Resources {
        public static final ResourceLocation INK_TEXTURE = getAsResourceLocation("block/ink_still");
        public static final ResourceLocation FLOWING_INK_TEXTURE = getAsResourceLocation("block/ink_flow");
        public static final ResourceLocation LINKING_BOOK_TEXTURE = getAsResourceLocation(
                "textures/entity/linking_book.png");
    }

    // Helper for making the private field getters via reflection
    // Also throws ClassCastException if the types are wrong
    @SuppressWarnings("unchecked")
    public static <FIELDHOLDER, FIELDTYPE> Function<FIELDHOLDER, FIELDTYPE> getField(
            Class<FIELDHOLDER> fieldHolderClass, String fieldName) {
        // Forge's ORH is needed to reflect into vanilla Minecraft Java
        Field field = ObfuscationReflectionHelper.findField(fieldHolderClass, fieldName);
        return instance -> {
            try {
                return (FIELDTYPE) (field.get(instance));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static Method getMethod(Class<?> methodHolderClass, String methodName, Class<?>... parameterTypes) {
        return ObfuscationReflectionHelper.findMethod(methodHolderClass, methodName, parameterTypes);
    }

    public static <T> Constructor<T> getConstructor(final Class<T> classOne, final Class<?>... parameters) {
        return ObfuscationReflectionHelper.findConstructor(classOne, parameters);
    }

}