/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2025 Dan Floyd ("TheFloydman").
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

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.neoforged.fml.util.ObfuscationReflectionHelper;

import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ReflectionHelper {

    public static final Function<MinecraftServer, ChunkProgressListenerFactory> CHUNK_PROGRESS
            = getFieldGetter(MinecraftServer.class, "progressListenerFactory");

    public static final Function<MinecraftServer, Executor> EXECUTOR
            = getFieldGetter(MinecraftServer.class, "executor");

    public static final Function<MinecraftServer, LevelStorageSource.LevelStorageAccess> LEVEL_STORAGE
            = getFieldGetter(MinecraftServer.class, "storageSource");

    public static final BiFunction<ChunkMap, Object[], Object> DUMP_CHUNKS_METHOD = getMethod(ChunkMap.class, "dumpChunks", Writer.class);

    public static final BiConsumer<Level, Integer> LEVEL_SKY_DARKEN
            = getFieldSetter(Level.class, "skyDarken");

    @SuppressWarnings("unchecked")
    public static <FIELDHOLDER, FIELDTYPE> Function<FIELDHOLDER, FIELDTYPE> getFieldGetter(
            Class<FIELDHOLDER> fieldHolderClass, String fieldName) {
        Field field = ObfuscationReflectionHelper.findField(fieldHolderClass, fieldName);
        return instance -> {
            try {
                return (FIELDTYPE) (field.get(instance));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <FIELDHOLDER, FIELDVALUE> BiConsumer<FIELDHOLDER, FIELDVALUE> getFieldSetter(
            Class<FIELDHOLDER> fieldHolderClass, String fieldName) {
        Field field = ObfuscationReflectionHelper.findField(fieldHolderClass, fieldName);
        return (instance, value) -> {
            try {
                field.set(instance, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <METHODHOLDER> BiFunction<METHODHOLDER, Object[], Object> getMethod(Class<METHODHOLDER> methodHolderClass, String methodName, Class<?>... parameterTypes) {
        Method method = ObfuscationReflectionHelper.findMethod(methodHolderClass, methodName, parameterTypes);
        return (instance, args) -> {
            try {
                return method.invoke(instance, args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <CONSTRUCTORHOLDER> Function<Object[], CONSTRUCTORHOLDER> getConstructor(final Class<CONSTRUCTORHOLDER> classOne, final Class<?>... parameters) {
        Constructor<CONSTRUCTORHOLDER> constructor = ObfuscationReflectionHelper.findConstructor(classOne, parameters);
        return args -> {
            try {
                return constructor.newInstance(args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
