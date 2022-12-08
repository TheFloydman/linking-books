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
package thefloydman.linkingbooks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.blockentity.LinkTranslatorBlockEntity;
import thefloydman.linkingbooks.capability.ModCapabilities;
import thefloydman.linkingbooks.util.LinkingPortalArea;

@SuppressWarnings("deprecation")
public class NaraBlock extends Block {

    public NaraBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity,
            ItemStack itemStack) {
        super.setPlacedBy(world, blockPos, blockState, livingEntity, itemStack);
        for (int x = blockPos.getX() - 32; x < blockPos.getX() + 32; x++) {
            for (int y = blockPos.getY() - 32; y < blockPos.getY() + 32; y++) {
                for (int z = blockPos.getZ() - 32; z < blockPos.getZ() + 32; z++) {
                    BlockPos currentPos = new BlockPos(x, y, z);
                    BlockEntity blockEntity = world.getBlockEntity(currentPos);
                    if (blockEntity != null && blockEntity instanceof LinkTranslatorBlockEntity) {
                        LinkTranslatorBlockEntity translator = (LinkTranslatorBlockEntity) blockEntity;
                        if (translator.hasBook()) {
                            ILinkData linkData = translator.getBook().getCapability(ModCapabilities.LINK_DATA)
                                    .orElse(null);
                            LinkingPortalArea.tryMakeLinkingPortalOnEveryAxis(world, currentPos, linkData, translator);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onRemove(BlockState blockState, Level world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (!blockState.is(blockState2.getBlock())) {
            for (int x = blockPos.getX() - 32; x < blockPos.getX() + 32; x++) {
                for (int y = blockPos.getY() - 32; y < blockPos.getY() + 32; y++) {
                    for (int z = blockPos.getZ() - 32; z < blockPos.getZ() + 32; z++) {
                        BlockPos currentPos = new BlockPos(x, y, z);
                        BlockEntity blockEntity = world.getBlockEntity(currentPos);
                        if (blockEntity != null && blockEntity instanceof LinkTranslatorBlockEntity) {
                            LinkTranslatorBlockEntity translator = (LinkTranslatorBlockEntity) blockEntity;
                            if (translator.hasBook()) {
                                ILinkData linkData = translator.getBook().getCapability(ModCapabilities.LINK_DATA)
                                        .orElse(null);
                                LinkingPortalArea.tryMakeLinkingPortalOnEveryAxis(world, currentPos, linkData,
                                        translator);
                            }
                        }
                    }
                }
            }
        }
        super.onRemove(blockState, world, blockPos, blockState2, bl);
    }

}
