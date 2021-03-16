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
package thefloydman.linkingbooks.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import thefloydman.linkingbooks.block.material.ModMaterials;
import thefloydman.linkingbooks.fluid.ModFluids;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.util.Reference.BlockNames;

public final class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            Reference.MOD_ID);

    public static final RegistryObject<Block> LINKING_LECTERN = BLOCKS.register(BlockNames.LINKING_LECTERN,
            () -> new LinkingLecternBlock(AbstractBlock.Properties.of(Material.WOOD).strength(5)
                    .harvestLevel(2).harvestTool(ToolType.AXE)));

    public static final RegistryObject<Block> NARA = BLOCKS.register(BlockNames.NARA,
            () -> new NaraBlock(AbstractBlock.Properties.of(Material.METAL, MaterialColor.COLOR_BROWN)
                    .strength(100.0F, 2400.0F).harvestTool(ToolType.PICKAXE)));

    public static final RegistryObject<Block> LINK_TRANSLATOR = BLOCKS.register(BlockNames.LINK_TRANSLATOR,
            () -> new LinkTranslatorBlock(AbstractBlock.Properties.of(Material.METAL, MaterialColor.COLOR_BROWN)
                    .strength(25.0F, 600.0F).harvestTool(ToolType.PICKAXE)));

    public static final RegistryObject<Block> LINKING_PORTAL = BLOCKS.register(BlockNames.LINKING_PORTAL,
            () -> new LinkingPortalBlock(AbstractBlock.Properties.of(Material.PORTAL).noCollission()
                    .strength(-1.0F, -1.0F).sound(SoundType.GLASS).noOcclusion().lightLevel((state) -> {
                        return 11;
                    })));

    public static final RegistryObject<Block> BOOKSHELF_STAIRS = BLOCKS.register(BlockNames.BOOKSHELF_STAIRS,
            () -> new StairsBlock(() -> Blocks.SPRUCE_PLANKS.defaultBlockState(),
                    AbstractBlock.Properties.copy(Blocks.SPRUCE_PLANKS)));

    public static final RegistryObject<Block> MARKER_SWITCH = BLOCKS.register(BlockNames.MARKER_SWITCH,
            () -> new MarkerSwitchBlock(AbstractBlock.Properties.of(Material.WOOD).strength(5)
                    .harvestLevel(2).harvestTool(ToolType.AXE)));

    public static final RegistryObject<FlowingFluidBlock> INK = BLOCKS.register(BlockNames.INK,
            () -> new FlowingFluidBlock(ModFluids.INK, AbstractBlock.Properties.of(ModMaterials.INK)
                    .noCollission().strength(100.0F).noDrops()));

}
