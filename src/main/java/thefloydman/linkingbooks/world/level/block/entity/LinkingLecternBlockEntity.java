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

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class LinkingLecternBlockEntity extends LinkingBookHolderBlockEntity {

    public LinkingLecternBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.LINKING_LECTERN.get(), pos, state);
    }

}