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
import thefloydman.linkingbooks.world.level.block.LinkingPortalBlock;
import thefloydman.linkingbooks.world.level.block.ModBlocks;
import thefloydman.linkingbooks.world.level.block.entity.LinkTranslatorBlockEntity;
import thefloydman.linkingbooks.world.storage.LinkingBooksSavedData;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LinkingPortalArea {

    public static Set<BlockPos> getPortalArea(Level world, BlockPos startFillerPos, Direction.Axis constantAxis,
                                              Set<BlockState> validFrameStates, Set<BlockState> validFillerStates, int minArea, int maxArea) {
        if (world == null || startFillerPos == null || validFrameStates == null || validFillerStates == null
                || !validFillerStates.contains(world.getBlockState(startFillerPos))) {
            return new HashSet<BlockPos>();
        }
        List<BlockPos> unvisited = new ArrayList<BlockPos>();
        unvisited.add(startFillerPos);
        Set<BlockPos> frame = new HashSet<BlockPos>();
        Set<BlockPos> filler = new HashSet<BlockPos>();
        while (!unvisited.isEmpty()) {
            BlockPos currentPos = unvisited.getFirst();
            unvisited.removeFirst();
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

    public static void createPortal(Level world, Set<BlockPos> positions, BlockState portalState, LinkData linkData) {
        for (BlockPos pos : positions) {
            world.setBlock(pos, portalState, 18);
            if (world instanceof ServerLevel && linkData != null) {
                LinkingBooksSavedData savedData = ((ServerLevel) world).getDataStorage()
                        .computeIfAbsent(LinkingBooksSavedData.factory(), Reference.MODID);
                savedData.addLinkingPortalData(pos, linkData);
            }
        }
    }

    public static void erasePortal(Level world, Set<BlockPos> positions) {
        BlockState blockState = Blocks.AIR.defaultBlockState();
        for (BlockPos pos : positions) {
            world.setBlock(pos, blockState, 18);
            world.setBlock(pos, blockState, 18);
            if (world instanceof ServerLevel) {
                LinkingBooksSavedData savedData = ((ServerLevel) world).getDataStorage()
                        .computeIfAbsent(LinkingBooksSavedData.factory(), Reference.MODID);
                savedData.removeLinkingPortalData(pos);
            }
        }
        ;
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

    public static void tryMakeLinkingPortalOnEveryAxis(Level world, BlockPos pos, LinkData linkData,
                                                       LinkTranslatorBlockEntity blockEntity) {
        tryMakeLinkingPortalWithConstantAxis(world, pos.north(), Direction.Axis.X, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(world, pos.north(), Direction.Axis.Y, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(world, pos.north(), Direction.Axis.Z, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(world, pos.south(), Direction.Axis.X, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(world, pos.south(), Direction.Axis.Y, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(world, pos.south(), Direction.Axis.Z, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(world, pos.east(), Direction.Axis.X, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(world, pos.east(), Direction.Axis.Y, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(world, pos.east(), Direction.Axis.Z, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(world, pos.west(), Direction.Axis.X, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(world, pos.west(), Direction.Axis.Y, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(world, pos.west(), Direction.Axis.Z, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(world, pos.above(), Direction.Axis.X, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(world, pos.above(), Direction.Axis.Y, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(world, pos.above(), Direction.Axis.Z, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(world, pos.below(), Direction.Axis.X, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(world, pos.below(), Direction.Axis.Y, linkData, blockEntity);
        tryMakeLinkingPortalWithConstantAxis(world, pos.below(), Direction.Axis.Z, linkData, blockEntity);
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
            LinkingPortalArea.createPortal(world, portalPositions,
                    ModBlocks.LINKING_PORTAL.get().defaultBlockState().setValue(LinkingPortalBlock.AXIS, constantAxis),
                    linkData);
        }
    }

    public static void tryEraseLinkingPortalOnEveryAxis(Level world, BlockPos pos) {
        tryEraseLinkingPortalWithConstantAxis(world, pos.north(), Direction.Axis.X);
        tryEraseLinkingPortalWithConstantAxis(world, pos.east(), Direction.Axis.X);
        tryEraseLinkingPortalWithConstantAxis(world, pos.west(), Direction.Axis.X);
        tryEraseLinkingPortalWithConstantAxis(world, pos.south(), Direction.Axis.X);
        tryEraseLinkingPortalWithConstantAxis(world, pos.above(), Direction.Axis.X);
        tryEraseLinkingPortalWithConstantAxis(world, pos.below(), Direction.Axis.X);
        tryEraseLinkingPortalWithConstantAxis(world, pos.north(), Direction.Axis.Y);
        tryEraseLinkingPortalWithConstantAxis(world, pos.east(), Direction.Axis.Y);
        tryEraseLinkingPortalWithConstantAxis(world, pos.west(), Direction.Axis.Y);
        tryEraseLinkingPortalWithConstantAxis(world, pos.south(), Direction.Axis.Y);
        tryEraseLinkingPortalWithConstantAxis(world, pos.above(), Direction.Axis.Y);
        tryEraseLinkingPortalWithConstantAxis(world, pos.below(), Direction.Axis.Y);
        tryEraseLinkingPortalWithConstantAxis(world, pos.north(), Direction.Axis.Z);
        tryEraseLinkingPortalWithConstantAxis(world, pos.east(), Direction.Axis.Z);
        tryEraseLinkingPortalWithConstantAxis(world, pos.west(), Direction.Axis.Z);
        tryEraseLinkingPortalWithConstantAxis(world, pos.south(), Direction.Axis.Z);
        tryEraseLinkingPortalWithConstantAxis(world, pos.above(), Direction.Axis.Z);
        tryEraseLinkingPortalWithConstantAxis(world, pos.below(), Direction.Axis.Z);
    }

    public static void tryEraseLinkingPortalWithConstantAxis(Level world, BlockPos pos, Direction.Axis constantAxis) {
        Set<BlockPos> portalPositions = LinkingPortalArea
                .getPortalArea(
                        world, pos, constantAxis, Sets
                                .newHashSet(Stream
                                        .concat(ModBlocks.NARA.get().getStateDefinition().getPossibleStates().stream(),
                                                ModBlocks.LINK_TRANSLATOR.get().getStateDefinition().getPossibleStates()
                                                        .stream())
                                        .toList().toArray(new BlockState[]{})),
                        Sets.newHashSet(ModBlocks.LINKING_PORTAL.get().getStateDefinition().getPossibleStates()
                                .toArray(new BlockState[]{})),
                        1, 32 * 32);
        if (!portalPositions.isEmpty()) {
            LinkingPortalArea.erasePortal(world, portalPositions);
        }
    }

}