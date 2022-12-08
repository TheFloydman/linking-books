/*******************************************************************************
 * Copyright 2019-2022 Dan Floyd ("TheFloydman")
 *
 * This file is part of Linking Books.
 *
 * Linking Books is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Linking Books is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Linking Books. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package thefloydman.linkingbooks.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.util.Reference.EntityNames;

public class ModEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,
            Reference.MOD_ID);

    public static final RegistryObject<EntityType<LinkingBookEntity>> LINKING_BOOK = ENTITIES
            .register(EntityNames.LINKING_BOOK,
                    () -> EntityType.Builder.<LinkingBookEntity>of(LinkingBookEntity::new, MobCategory.MISC)
                            .sized(0.3F, 0.1F).setTrackingRange(16)
                            .build(Reference.MOD_ID + ":" + EntityNames.LINKING_BOOK));

}
