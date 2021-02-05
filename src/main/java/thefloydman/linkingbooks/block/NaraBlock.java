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

import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.config.ModConfig;
import thefloydman.linkingbooks.integration.ImmersivePortalsIntegration;
import thefloydman.linkingbooks.linking.LinkEffects;
import thefloydman.linkingbooks.tileentity.LinkTranslatorTileEntity;
import thefloydman.linkingbooks.util.LinkingPortalUtils;
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
                            tryMakePortalInDirection(world, currentPos, Direction.NORTH, linkData, translator);
                            tryMakePortalInDirection(world, currentPos, Direction.EAST, linkData, translator);
                            tryMakePortalInDirection(world, currentPos, Direction.SOUTH, linkData, translator);
                            tryMakePortalInDirection(world, currentPos, Direction.WEST, linkData, translator);
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
                            boolean deletePortal = !this.canPortalExistInDirection(world, currentPos, Direction.NORTH)
                                    && !this.canPortalExistInDirection(world, currentPos, Direction.EAST)
                                    && !this.canPortalExistInDirection(world, currentPos, Direction.SOUTH)
                                    && !this.canPortalExistInDirection(world, currentPos, Direction.WEST);
                            if (deletePortal) {
                                if (Reference.isImmersivePortalsLoaded()) {
                                    ImmersivePortalsIntegration.deleteLinkingPortals(translator);
                                }
                            }
                        }
                    }
                }
            }
        }
        super.onReplaced(blockState, world, blockPos, blockState2, bl);
    }

    private boolean canPortalExistInDirection(World world, BlockPos pos, Direction direction) {
        Optional<LinkingPortalUtils> optional = LinkingPortalUtils.canMakePortal(world, pos.offset(direction), Axis.X);
        return optional.isPresent();
    }

    private void tryMakePortalInDirection(World world, BlockPos pos, Direction direction, ILinkData linkData,
            LinkTranslatorTileEntity blockEntity) {
        if (world.getDimensionKey().getLocation().equals(linkData.getDimension())
                && !linkData.getLinkEffects().contains(LinkEffects.INTRAAGE_LINKING.get())) {
            return;
        }
        Optional<LinkingPortalUtils> optional = LinkingPortalUtils.canMakePortal(world, pos.offset(direction), Axis.X);
        if (optional.isPresent()) {
            LinkingPortalUtils util = optional.get();
            if (Reference.isImmersivePortalsLoaded()
                    && ModConfig.COMMON.useImmersivePortalsForLinkingPortals.get() == true) {
                double x = util.axis == Axis.X ? util.lowerCorner.getX() + (util.width / 2.0D) - (util.width - 1.0D)
                        : util.lowerCorner.getX() + 0.5D;
                double y = util.lowerCorner.getY() + (util.height / 2.0D);
                double z = util.axis == Axis.X ? util.lowerCorner.getZ() + 0.5D
                        : util.lowerCorner.getZ() + (util.width / 2.0D);
                ImmersivePortalsIntegration.addImmersivePortal(world, new double[] { x, y, z }, util.width, util.height,
                        util.axis, linkData, blockEntity);
            } else {
                util.createPortal(linkData);
            }
        }
    }

}
