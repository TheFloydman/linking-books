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

package thefloydman.linkingbooks.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import thefloydman.linkingbooks.Reference;

import java.util.function.Supplier;

public class ModEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE,
            Reference.MODID);

    public static final Supplier<EntityType<LinkingBookEntity>> LINKING_BOOK = ENTITIES
            .register(Reference.EntityNames.LINKING_BOOK,
                    () -> EntityType.Builder.<LinkingBookEntity>of(LinkingBookEntity::new, MobCategory.MISC)
                            .sized(0.3F, 0.1F).setTrackingRange(16)
                            .build(Reference.MODID + ":" + Reference.EntityNames.LINKING_BOOK));

}