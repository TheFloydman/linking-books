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

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import thefloydman.linkingbooks.block.material.ModMaterials;
import thefloydman.linkingbooks.fluid.ModFluids;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.util.Reference.BlockNames;

public final class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            Reference.MOD_ID);

    public static final RegistryObject<Block> LINKING_LECTERN = BLOCKS.register(BlockNames.LINKING_LECTERN,
            () -> new LinkingLecternBlock(
                    BlockBehaviour.Properties.of(Material.WOOD).strength(5).sound(SoundType.WOOD)));

    public static final RegistryObject<Block> NARA = BLOCKS.register(BlockNames.NARA,
            () -> new NaraBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BROWN)
                    .strength(100.0F, 2400.0F).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> LINK_TRANSLATOR = BLOCKS.register(BlockNames.LINK_TRANSLATOR,
            () -> new LinkTranslatorBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BROWN)
                    .strength(25.0F, 600.0F).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> LINKING_PORTAL = BLOCKS.register(BlockNames.LINKING_PORTAL,
            () -> new LinkingPortalBlock(BlockBehaviour.Properties.of(Material.PORTAL).noCollission()
                    .strength(-1.0F, -1.0F).sound(SoundType.GLASS).noOcclusion().lightLevel((state) -> {
                        return 11;
                    })));

    public static final RegistryObject<Block> BOOKSHELF_STAIRS = BLOCKS.register(BlockNames.BOOKSHELF_STAIRS,
            () -> new StairBlock(() -> Blocks.SPRUCE_PLANKS.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS)));

    public static final RegistryObject<Block> MARKER_SWITCH = BLOCKS.register(BlockNames.MARKER_SWITCH,
            () -> new MarkerSwitchBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(5)));

    public static final RegistryObject<LiquidBlock> INK = BLOCKS.register(BlockNames.INK,
            () -> new LiquidBlock(ModFluids.INK,
                    BlockBehaviour.Properties.of(ModMaterials.INK).noCollission().strength(100.0F).noLootTable()));

}
