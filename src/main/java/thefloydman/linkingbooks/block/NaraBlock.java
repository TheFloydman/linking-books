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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.integration.ImmersivePortalsIntegration;
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
                            LinkingPortalArea.tryMakeLinkingPortalOnEveryAxis(world, currentPos, linkData, translator);
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
                                LinkingPortalArea.tryMakeLinkingPortalOnEveryAxis(world, currentPos, linkData,
                                        translator);
                            }
                        }
                    }
                }
            }
        }
        super.onReplaced(blockState, world, blockPos, blockState2, bl);
    }

}
