package thefloydman.linkingbooks.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
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

    public static final RegistryObject<Block> BOOK_DISPLAY = BLOCKS.register(BlockNames.BOOK_DISPLAY,
            () -> new BookDisplayBlock(AbstractBlock.Properties.create(Material.WOOD).hardnessAndResistance(5)
                    .harvestLevel(2).harvestTool(ToolType.AXE)));

    public static final RegistryObject<FlowingFluidBlock> INK = BLOCKS.register(BlockNames.INK,
            () -> new FlowingFluidBlock(ModFluids.INK, AbstractBlock.Properties.create(ModMaterials.INK)
                    .doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));

}
