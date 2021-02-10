/*******************************************************************************
 * Linking Books - Fabric
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
package thefloydman.linkingbooks.block;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.config.ModConfig;
import thefloydman.linkingbooks.integration.ImmersivePortalsIntegration;
import thefloydman.linkingbooks.linking.LinkEffects;
import thefloydman.linkingbooks.tileentity.LinkTranslatorTileEntity;
import thefloydman.linkingbooks.util.LinkingPortalArea;
import thefloydman.linkingbooks.util.Reference;

public class NaraBlock extends Block {

    public NaraBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity,
            ItemStack itemStack) {
        super.onBlockPlacedBy(world, blockPos, blockState, livingEntity, itemStack);
        for (int x = blockPos.getX() - 32; x < blockPos.getX() + 32; x++) {
            for (int y = blockPos.getY() - 32; y < blockPos.getY() + 32; y++) {
                for (int z = blockPos.getZ() - 32; z < blockPos.getZ() + 32; z++) {
                    BlockPos currentPos = new BlockPos(x, y, z);
                    TileEntity blockEntity = world.getTileEntity(currentPos);
                    if (blockEntity != null && blockEntity instanceof LinkTranslatorTileEntity) {
                        LinkTranslatorTileEntity translator = (LinkTranslatorTileEntity) blockEntity;
                        if (translator.hasBook()) {
                            ILinkData linkData = translator.getBook().getCapability(LinkData.LINK_DATA).orElse(null);
                            tryMakePortalWithConstantAxis(world, currentPos.north(), Axis.X, linkData, translator);
                            tryMakePortalWithConstantAxis(world, currentPos.north(), Axis.Y, linkData, translator);
                            tryMakePortalWithConstantAxis(world, currentPos.north(), Axis.Z, linkData, translator);
                            tryMakePortalWithConstantAxis(world, currentPos.south(), Axis.X, linkData, translator);
                            tryMakePortalWithConstantAxis(world, currentPos.south(), Axis.Y, linkData, translator);
                            tryMakePortalWithConstantAxis(world, currentPos.south(), Axis.Z, linkData, translator);
                            tryMakePortalWithConstantAxis(world, currentPos.east(), Axis.X, linkData, translator);
                            tryMakePortalWithConstantAxis(world, currentPos.east(), Axis.Y, linkData, translator);
                            tryMakePortalWithConstantAxis(world, currentPos.east(), Axis.Z, linkData, translator);
                            tryMakePortalWithConstantAxis(world, currentPos.west(), Axis.X, linkData, translator);
                            tryMakePortalWithConstantAxis(world, currentPos.west(), Axis.Y, linkData, translator);
                            tryMakePortalWithConstantAxis(world, currentPos.west(), Axis.Z, linkData, translator);
                            tryMakePortalWithConstantAxis(world, currentPos.up(), Axis.X, linkData, translator);
                            tryMakePortalWithConstantAxis(world, currentPos.up(), Axis.Y, linkData, translator);
                            tryMakePortalWithConstantAxis(world, currentPos.up(), Axis.Z, linkData, translator);
                            tryMakePortalWithConstantAxis(world, currentPos.down(), Axis.X, linkData, translator);
                            tryMakePortalWithConstantAxis(world, currentPos.down(), Axis.Y, linkData, translator);
                            tryMakePortalWithConstantAxis(world, currentPos.down(), Axis.Z, linkData, translator);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onReplaced(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (!blockState.isIn(blockState2.getBlock())) {
            for (int x = blockPos.getX() - 32; x < blockPos.getX() + 32; x++) {
                for (int y = blockPos.getY() - 32; y < blockPos.getY() + 32; y++) {
                    for (int z = blockPos.getZ() - 32; z < blockPos.getZ() + 32; z++) {
                        BlockPos currentPos = new BlockPos(x, y, z);
                        TileEntity blockEntity = world.getTileEntity(currentPos);
                        if (blockEntity != null && blockEntity instanceof LinkTranslatorTileEntity) {
                            LinkTranslatorTileEntity translator = (LinkTranslatorTileEntity) blockEntity;
                            if (Reference.isImmersivePortalsLoaded()) {
                                ImmersivePortalsIntegration.deleteLinkingPortals(translator);
                            }
                            if (translator.hasBook()) {
                                ILinkData linkData = translator.getBook().getCapability(LinkData.LINK_DATA)
                                        .orElse(null);
                                tryMakePortalWithConstantAxis(world, currentPos.north(), Axis.X, linkData, translator);
                                tryMakePortalWithConstantAxis(world, currentPos.north(), Axis.Y, linkData, translator);
                                tryMakePortalWithConstantAxis(world, currentPos.north(), Axis.Z, linkData, translator);
                                tryMakePortalWithConstantAxis(world, currentPos.south(), Axis.X, linkData, translator);
                                tryMakePortalWithConstantAxis(world, currentPos.south(), Axis.Y, linkData, translator);
                                tryMakePortalWithConstantAxis(world, currentPos.south(), Axis.Z, linkData, translator);
                                tryMakePortalWithConstantAxis(world, currentPos.east(), Axis.X, linkData, translator);
                                tryMakePortalWithConstantAxis(world, currentPos.east(), Axis.Y, linkData, translator);
                                tryMakePortalWithConstantAxis(world, currentPos.east(), Axis.Z, linkData, translator);
                                tryMakePortalWithConstantAxis(world, currentPos.west(), Axis.X, linkData, translator);
                                tryMakePortalWithConstantAxis(world, currentPos.west(), Axis.Y, linkData, translator);
                                tryMakePortalWithConstantAxis(world, currentPos.west(), Axis.Z, linkData, translator);
                                tryMakePortalWithConstantAxis(world, currentPos.up(), Axis.X, linkData, translator);
                                tryMakePortalWithConstantAxis(world, currentPos.up(), Axis.Y, linkData, translator);
                                tryMakePortalWithConstantAxis(world, currentPos.up(), Axis.Z, linkData, translator);
                                tryMakePortalWithConstantAxis(world, currentPos.down(), Axis.X, linkData, translator);
                                tryMakePortalWithConstantAxis(world, currentPos.down(), Axis.Y, linkData, translator);
                                tryMakePortalWithConstantAxis(world, currentPos.down(), Axis.Z, linkData, translator);
                            }
                        }
                    }
                }
            }
        }
        super.onReplaced(blockState, world, blockPos, blockState2, bl);
    }

    private static void tryMakePortalWithConstantAxis(World world, BlockPos pos, Axis constantAxis, ILinkData linkData,
            LinkTranslatorTileEntity blockEntity) {
        if (world.getDimensionKey().getLocation().equals(linkData.getDimension())
                && !linkData.getLinkEffects().contains(LinkEffects.INTRAAGE_LINKING.get())) {
            return;
        }
        Set<BlockPos> portalPositions = LinkingPortalArea
                .getPortalArea(
                        world, pos, constantAxis, Sets
                                .newHashSet(Stream
                                        .concat(ModBlocks.NARA.get().getStateContainer().getValidStates().stream(),
                                                ModBlocks.LINK_TRANSLATOR.get().getStateContainer().getValidStates()
                                                        .stream())
                                        .collect(Collectors.toList()).toArray(new BlockState[] {})),
                        Sets.newHashSet(Blocks.AIR.getStateContainer().getValidStates().toArray(new BlockState[] {})),
                        1, 32 * 32);
        if (!portalPositions.isEmpty()) {
            if (Reference.isImmersivePortalsLoaded()
                    && ModConfig.COMMON.useImmersivePortalsForLinkingPortals.get() == true) {
                double[] posAndDimensions = LinkingPortalArea.getPortalPositionAndWidthAndHeight(portalPositions);
                ImmersivePortalsIntegration.addImmersivePortal(world,
                        new double[] { posAndDimensions[0], posAndDimensions[1], posAndDimensions[2] },
                        posAndDimensions[3], posAndDimensions[4], portalPositions, constantAxis, linkData, blockEntity);
            } else {
                LinkingPortalArea.createPortal(world, portalPositions,
                        ModBlocks.LINKING_PORTAL.get().getDefaultState().with(LinkingPortalBlock.AXIS, constantAxis),
                        linkData);
            }
        }
    }

}
