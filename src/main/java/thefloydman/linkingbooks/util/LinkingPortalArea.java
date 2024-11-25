/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2019-2024 Dan Floyd ("TheFloydman").
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

package thefloydman.linkingbooks.util;

import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import thefloydman.linkingbooks.data.LinkData;
import thefloydman.linkingbooks.integration.ImmersivePortalsIntegration;
import thefloydman.linkingbooks.world.level.block.LinkingPortalBlock;
import thefloydman.linkingbooks.world.level.block.ModBlocks;
import thefloydman.linkingbooks.world.level.block.entity.LinkTranslatorBlockEntity;
import thefloydman.linkingbooks.world.storage.LinkingBooksSavedData;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LinkingPortalArea {

    public static Set<BlockPos> getPortalArea(@Nonnull Level level, @Nonnull BlockPos startFillerPos, @Nonnull Direction.Axis constantAxis,
                                              @Nonnull Set<BlockState> validFrameStates, @Nonnull Set<BlockState> validFillerStates, int minArea, int maxArea) {
        if (!validFillerStates.contains(level.getBlockState(startFillerPos))) {
            return new HashSet<>();
        }
        List<BlockPos> unvisited = new ArrayList<>();
        unvisited.add(startFillerPos);
        Set<BlockPos> frame = new HashSet<>();
        Set<BlockPos> filler = new HashSet<>();
        while (!unvisited.isEmpty()) {
            BlockPos currentPos = unvisited.getFirst();
            unvisited.removeFirst();
            if (validFrameStates.contains(level.getBlockState(currentPos))) {
                frame.add(currentPos);
            } else if (validFillerStates.contains(level.getBlockState(currentPos))) {
                filler.add(currentPos);
                switch (constantAxis) {
                    case Y:
                        addPosIfAbsent(unvisited, currentPos.north(), frame, filler);
                        addPosIfAbsent(unvisited, currentPos.east(), frame, filler);
                        addPosIfAbsent(unvisited, currentPos.south(), frame, filler);
                        addPosIfAbsent(unvisited, currentPos.west(), frame, filler);
                        break;
                    case Z:
                        addPosIfAbsent(unvisited, currentPos.above(), frame, filler);
                        addPosIfAbsent(unvisited, currentPos.below(), frame, filler);
                        addPosIfAbsent(unvisited, currentPos.north(), frame, filler);
                        addPosIfAbsent(unvisited, currentPos.south(), frame, filler);
                        break;
                    default:
                        addPosIfAbsent(unvisited, currentPos.above(), frame, filler);
                        addPosIfAbsent(unvisited, currentPos.below(), frame, filler);
                        addPosIfAbsent(unvisited, currentPos.east(), frame, filler);
                        addPosIfAbsent(unvisited, currentPos.west(), frame, filler);
                        break;
                }
            } else {
                return new HashSet<>();
            }
            if (filler.size() > maxArea) {
                return new HashSet<>();
            }
        }
        return filler.size() < minArea ? new HashSet<>() : filler;
    }

    private static void addPosIfAbsent(List<BlockPos> unvisited, BlockPos blockPos, Set<BlockPos> frame,
                                       Set<BlockPos> filler) {
        if (!unvisited.contains(blockPos) && !frame.contains(blockPos) && !filler.contains(blockPos)) {
            unvisited.add(blockPos);
        }
    }

    public static void createPortal(Level level, Set<BlockPos> positions, BlockState portalState, LinkData linkData) {
        for (BlockPos pos : positions) {
            level.setBlock(pos, portalState, 18);
            if (level instanceof ServerLevel && linkData != null) {
                LinkingBooksSavedData savedData = ((ServerLevel) level).getDataStorage()
                        .computeIfAbsent(LinkingBooksSavedData.factory(), Reference.MODID);
                savedData.addLinkingPortalData(pos, linkData);
            }
        }
    }

    public static void erasePortal(Level level, Set<BlockPos> positions) {
        BlockState blockState = Blocks.AIR.defaultBlockState();
        for (BlockPos pos : positions) {
            level.setBlock(pos, blockState, 18);
            level.setBlock(pos, blockState, 18);
            if (level instanceof ServerLevel) {
                LinkingBooksSavedData savedData = ((ServerLevel) level).getDataStorage()
                        .computeIfAbsent(LinkingBooksSavedData.factory(), Reference.MODID);
                savedData.removeLinkingPortalData(pos);
            }
        }
    }

    public static double[] getPortalPositionAndWidthAndHeight(Set<BlockPos> positions) {
        double xMin = Collections.min(positions.stream().map(Vec3i::getX).collect(Collectors.toSet()));
        double xMax = Collections.max(positions.stream().map(Vec3i::getX).collect(Collectors.toSet()));
        double posX = (xMin + xMax) / 2.0D;
        double deltaX = xMax - xMin;
        double yMin = Collections.min(positions.stream().map(Vec3i::getY).collect(Collectors.toSet()));
        double yMax = Collections.max(positions.stream().map(Vec3i::getY).collect(Collectors.toSet()));
        double posY = (yMin + yMax) / 2.0D;
        double deltaY = yMax - yMin;
        double zMin = Collections.min(positions.stream().map(Vec3i::getZ).collect(Collectors.toSet()));
        double zMax = Collections.max(positions.stream().map(Vec3i::getZ).collect(Collectors.toSet()));
        double posZ = (zMin + zMax) / 2.0D;
        double deltaZ = zMax - zMin;
        double width = 0;
        double height = 0;
        if (deltaY != 0) {
            width = deltaX != 0 ? deltaX : deltaZ;
            height = deltaY;
        } else {
            width = deltaZ;
            height = deltaX;
        }
        return new double[]{posX, posY, posZ, width, height};
    }

    public static void tryMakeLinkingPortalOnEveryAxis(Level level, BlockPos blockPos, LinkData linkData,
                                                       LinkTranslatorBlockEntity blockEntity) {
        tryMakeLinkingPortalWithConstantAxis(level, blockPos.north(), Direction.Axis.X, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(level, blockPos.north(), Direction.Axis.Y, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(level, blockPos.north(), Direction.Axis.Z, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(level, blockPos.south(), Direction.Axis.X, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(level, blockPos.south(), Direction.Axis.Y, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(level, blockPos.south(), Direction.Axis.Z, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(level, blockPos.east(), Direction.Axis.X, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(level, blockPos.east(), Direction.Axis.Y, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(level, blockPos.east(), Direction.Axis.Z, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(level, blockPos.west(), Direction.Axis.X, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(level, blockPos.west(), Direction.Axis.Y, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(level, blockPos.west(), Direction.Axis.Z, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(level, blockPos.above(), Direction.Axis.X, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(level, blockPos.above(), Direction.Axis.Y, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(level, blockPos.above(), Direction.Axis.Z, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(level, blockPos.below(), Direction.Axis.X, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(level, blockPos.below(), Direction.Axis.Y, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(level, blockPos.below(), Direction.Axis.Z, linkData, blockEntity);
    }

    public static void tryMakeLinkingPortalWithConstantAxis(Level world, BlockPos pos, Direction.Axis constantAxis,
                                                            LinkData linkData, LinkTranslatorBlockEntity blockEntity) {
        if (world.dimension().location().equals(linkData.dimension())
                && !linkData.linkEffects().contains(Reference.getAsResourceLocation("intraage_linking"))) {
            return;
        }
        Set<BlockPos> portalPositions = LinkingPortalArea
                .getPortalArea(
                        world, pos, constantAxis, Sets
                                .newHashSet(Stream
                                        .concat(ModBlocks.NARA.get().getStateDefinition().getPossibleStates().stream(),
                                                ModBlocks.LINK_TRANSLATOR.get().getStateDefinition().getPossibleStates()
                                                        .stream())
                                        .toList().toArray(new BlockState[]{})),
                        Sets.newHashSet(
                                Blocks.AIR.getStateDefinition().getPossibleStates().toArray(new BlockState[]{})),
                        1, 32 * 32);
        if (!portalPositions.isEmpty()) {
            if (Reference.isImmersivePortalsLoaded()) {
                double[] posAndDimensions = LinkingPortalArea.getPortalPositionAndWidthAndHeight(portalPositions);
                ImmersivePortalsIntegration.addImmersivePortal(world,
                        new double[]{posAndDimensions[0], posAndDimensions[1], posAndDimensions[2]},
                        posAndDimensions[3], posAndDimensions[4], portalPositions, constantAxis, linkData, blockEntity);
            } else {
                LinkingPortalArea.createPortal(world, portalPositions,
                        ModBlocks.LINKING_PORTAL.get().defaultBlockState().setValue(LinkingPortalBlock.AXIS, constantAxis),
                        linkData);
            }
        }
    }

    public static void tryEraseLinkingPortalOnEveryAxis(Level level, BlockPos blockPos) {
        tryEraseLinkingPortalWithConstantAxis(level, blockPos.north(), Direction.Axis.X);
        tryEraseLinkingPortalWithConstantAxis(level, blockPos.east(), Direction.Axis.X);
        tryEraseLinkingPortalWithConstantAxis(level, blockPos.west(), Direction.Axis.X);
        tryEraseLinkingPortalWithConstantAxis(level, blockPos.south(), Direction.Axis.X);
        tryEraseLinkingPortalWithConstantAxis(level, blockPos.above(), Direction.Axis.X);
        tryEraseLinkingPortalWithConstantAxis(level, blockPos.below(), Direction.Axis.X);
        tryEraseLinkingPortalWithConstantAxis(level, blockPos.north(), Direction.Axis.Y);
        tryEraseLinkingPortalWithConstantAxis(level, blockPos.east(), Direction.Axis.Y);
        tryEraseLinkingPortalWithConstantAxis(level, blockPos.west(), Direction.Axis.Y);
        tryEraseLinkingPortalWithConstantAxis(level, blockPos.south(), Direction.Axis.Y);
        tryEraseLinkingPortalWithConstantAxis(level, blockPos.above(), Direction.Axis.Y);
        tryEraseLinkingPortalWithConstantAxis(level, blockPos.below(), Direction.Axis.Y);
        tryEraseLinkingPortalWithConstantAxis(level, blockPos.north(), Direction.Axis.Z);
        tryEraseLinkingPortalWithConstantAxis(level, blockPos.east(), Direction.Axis.Z);
        tryEraseLinkingPortalWithConstantAxis(level, blockPos.west(), Direction.Axis.Z);
        tryEraseLinkingPortalWithConstantAxis(level, blockPos.south(), Direction.Axis.Z);
        tryEraseLinkingPortalWithConstantAxis(level, blockPos.above(), Direction.Axis.Z);
        tryEraseLinkingPortalWithConstantAxis(level, blockPos.below(), Direction.Axis.Z);
    }

    public static void tryEraseLinkingPortalWithConstantAxis(Level level, BlockPos blockPos, Direction.Axis constantAxis) {
        Set<BlockPos> portalPositions = LinkingPortalArea
                .getPortalArea(
                        level, blockPos, constantAxis, Sets
                                .newHashSet(Stream
                                        .concat(ModBlocks.NARA.get().getStateDefinition().getPossibleStates().stream(),
                                                ModBlocks.LINK_TRANSLATOR.get().getStateDefinition().getPossibleStates()
                                                        .stream())
                                        .toList().toArray(new BlockState[]{})),
                        Sets.newHashSet(ModBlocks.LINKING_PORTAL.get().getStateDefinition().getPossibleStates()
                                .toArray(new BlockState[]{})),
                        1, 32 * 32);
        if (!portalPositions.isEmpty()) {
            LinkingPortalArea.erasePortal(level, portalPositions);
        }
    }

}