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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.world.storage.LinkingBooksSavedData;

public class LinkingPortalArea {

    public static Set<BlockPos> getPortalArea(World world, BlockPos startFillerPos, Axis constantAxis,
            Set<BlockState> validFrameStates, Set<BlockState> validFillerStates, int minArea, int maxArea) {
        if (world == null || startFillerPos == null || validFrameStates == null || validFillerStates == null
                || !validFillerStates.contains(world.getBlockState(startFillerPos))) {
            return new HashSet<BlockPos>();
        }
        List<BlockPos> unvisited = new ArrayList<BlockPos>();
        unvisited.add(startFillerPos);
        Set<BlockPos> frame = new HashSet<BlockPos>();
        Set<BlockPos> filler = new HashSet<BlockPos>();
        while (unvisited.size() > 0) {
            BlockPos currentPos = unvisited.get(0);
            unvisited.remove(0);
            if (validFrameStates.contains(world.getBlockState(currentPos))) {
                frame.add(currentPos);
            } else if (validFillerStates.contains(world.getBlockState(currentPos))) {
                filler.add(currentPos);
                switch (constantAxis) {
                    case Y:
                        addPosIfAbsent(unvisited, currentPos.north(), frame, filler);
                        addPosIfAbsent(unvisited, currentPos.east(), frame, filler);
                        addPosIfAbsent(unvisited, currentPos.south(), frame, filler);
                        addPosIfAbsent(unvisited, currentPos.west(), frame, filler);
                        break;
                    case Z:
                        addPosIfAbsent(unvisited, currentPos.up(), frame, filler);
                        addPosIfAbsent(unvisited, currentPos.down(), frame, filler);
                        addPosIfAbsent(unvisited, currentPos.north(), frame, filler);
                        addPosIfAbsent(unvisited, currentPos.south(), frame, filler);
                        break;
                    default:
                        addPosIfAbsent(unvisited, currentPos.up(), frame, filler);
                        addPosIfAbsent(unvisited, currentPos.down(), frame, filler);
                        addPosIfAbsent(unvisited, currentPos.east(), frame, filler);
                        addPosIfAbsent(unvisited, currentPos.west(), frame, filler);
                        break;
                }
            } else {
                return new HashSet<BlockPos>();
            }
            if (filler.size() > maxArea) {
                return new HashSet<BlockPos>();
            }
        }
        return filler.size() < minArea ? new HashSet<BlockPos>() : filler;
    }

    private static void addPosIfAbsent(List<BlockPos> unvisited, BlockPos pos, Set<BlockPos> frame,
            Set<BlockPos> filler) {
        if (!unvisited.contains(pos) && !frame.contains(pos) && !filler.contains(pos)) {
            unvisited.add(pos);
        }
    }

    public static void createPortal(World world, Set<BlockPos> positions, BlockState portalState, ILinkData linkData) {
        for (BlockPos pos : positions) {
            world.setBlockState(pos, portalState, 18);
            if (world instanceof ServerWorld && linkData != null) {
                LinkingBooksSavedData savedData = ((ServerWorld) world).getSavedData()
                        .getOrCreate(LinkingBooksSavedData::new, Reference.MOD_ID);
                savedData.addLinkingPortalData(pos, linkData);
            }
        }
    }

    public static void erasePortal(World world, Set<BlockPos> positions) {
        BlockState blockState = Blocks.AIR.getDefaultState();
        for (BlockPos pos : positions) {
            world.setBlockState(pos, blockState, 18);
            world.setBlockState(pos, blockState, 18);
            if (world instanceof ServerWorld) {
                LinkingBooksSavedData savedData = ((ServerWorld) world).getSavedData()
                        .getOrCreate(LinkingBooksSavedData::new, Reference.MOD_ID);
                savedData.removeLinkingPortalData(pos);
            }
        }
        ;
    }

    public static double[] getPortalPositionAndWidthAndHeight(Set<BlockPos> positions) {
        double xMin = Collections.min(positions.stream().map(pos -> pos.getX()).collect(Collectors.toSet()));
        double xMax = Collections.max(positions.stream().map(pos -> pos.getX()).collect(Collectors.toSet()));
        double posX = (xMin + xMax) / 2.0D;
        double deltaX = xMax - xMin;
        double yMin = Collections.min(positions.stream().map(pos -> pos.getY()).collect(Collectors.toSet()));
        double yMax = Collections.max(positions.stream().map(pos -> pos.getY()).collect(Collectors.toSet()));
        double posY = (yMin + yMax) / 2.0D;
        double deltaY = yMax - yMin;
        double zMin = Collections.min(positions.stream().map(pos -> pos.getZ()).collect(Collectors.toSet()));
        double zMax = Collections.max(positions.stream().map(pos -> pos.getZ()).collect(Collectors.toSet()));
        double posZ = (zMin + zMax) / 2.0D;
        double deltaZ = zMax - zMin;
        double width = 0;
        double height = 0;
        if (deltaY != 0) {
            width = deltaY;
            height = deltaX != 0 ? deltaX : deltaZ;
        } else {
            width = deltaZ;
            height = deltaX;
        }
        return new double[] { posX, posY, posZ, width, height };
    }

}
