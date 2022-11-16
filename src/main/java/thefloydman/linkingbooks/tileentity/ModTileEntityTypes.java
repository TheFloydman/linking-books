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
package thefloydman.linkingbooks.tileentity;

import com.google.common.collect.Sets;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import thefloydman.linkingbooks.block.ModBlocks;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.util.Reference.TileEntityNames;

public class ModTileEntityTypes {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister
            .create(ForgeRegistries.TILE_ENTITIES, Reference.MOD_ID);

    public static final RegistryObject<TileEntityType<LinkingLecternTileEntity>> LINKING_LECTERN = TILE_ENTITIES
            .register(TileEntityNames.LINKING_LECTERN, () -> new TileEntityType<>(LinkingLecternTileEntity::new,
                    Sets.newHashSet(ModBlocks.LINKING_LECTERN.get()), null));

    public static final RegistryObject<TileEntityType<LinkTranslatorTileEntity>> LINK_TRANSLATOR = TILE_ENTITIES
            .register(TileEntityNames.LINK_TRANSLATOR, () -> new TileEntityType<>(LinkTranslatorTileEntity::new,
                    Sets.newHashSet(ModBlocks.LINK_TRANSLATOR.get()), null));

    public static final RegistryObject<TileEntityType<MarkerSwitchTileEntity>> MARKER_SWITCH = TILE_ENTITIES
            .register(TileEntityNames.MARKER_SWITCH, () -> new TileEntityType<>(MarkerSwitchTileEntity::new,
                    Sets.newHashSet(ModBlocks.MARKER_SWITCH.get()), null));

}
