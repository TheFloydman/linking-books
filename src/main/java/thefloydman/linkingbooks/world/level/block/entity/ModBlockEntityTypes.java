/*
 * Copyright (c) 2019-2024 Dan Floyd ("TheFloydman").
 *
 * This file is part of Linking Books.
 *
 * Linking Books is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Linking Books is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with Linking Books. If not, see <https://www.gnu.org/licenses/>.
 */

package thefloydman.linkingbooks.world.level.block.entity;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.world.level.block.ModBlocks;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ModBlockEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister
            .create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Reference.MODID);

    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> make(
            BlockEntityType.BlockEntitySupplier<T> create, Supplier<? extends Block> valid) {
        return makeMultipleBlocks(create, ImmutableSet.of(valid));
    }    public static final Supplier<BlockEntityType<LinkingLecternBlockEntity>> LINKING_LECTERN = TILE_ENTITIES
            .register(Reference.TileEntityNames.LINKING_LECTERN,
                    make(LinkingLecternBlockEntity::new, ModBlocks.LINKING_LECTERN));

    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> makeMultipleBlocks(
            BlockEntityType.BlockEntitySupplier<T> create, Collection<? extends Supplier<? extends Block>> valid) {
        return () -> new BlockEntityType<>(create,
                ImmutableSet.copyOf(valid.stream().map(Supplier::get).collect(Collectors.toList())), null);
    }    public static final Supplier<BlockEntityType<LinkTranslatorBlockEntity>> LINK_TRANSLATOR = TILE_ENTITIES
            .register(Reference.TileEntityNames.LINK_TRANSLATOR,
                    make(LinkTranslatorBlockEntity::new, ModBlocks.LINK_TRANSLATOR));

    public static final Supplier<BlockEntityType<MarkerSwitchBlockEntity>> MARKER_SWITCH = TILE_ENTITIES
            .register(Reference.TileEntityNames.MARKER_SWITCH, make(MarkerSwitchBlockEntity::new, ModBlocks.MARKER_SWITCH));





}