package thefloydman.linkingbooks.world.level.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import thefloydman.linkingbooks.util.Reference;

public final class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Reference.MODID);

    public static final DeferredBlock<Block> BOOKSHELF_STAIRS = BLOCKS.register(
            Reference.BlockNames.BOOKSHELF_STAIRS,
            () -> new StairBlock(Blocks.SPRUCE_PLANKS.defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_PLANKS)));

    public static final DeferredBlock<Block> LINKING_LECTERN = BLOCKS.register(Reference.BlockNames.LINKING_LECTERN,
            () -> new LinkingLecternBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(5).sound(SoundType.WOOD)));

    public static final DeferredBlock<Block> NARA = BLOCKS.register(Reference.BlockNames.NARA,
            () -> new NaraBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                    .strength(100.0F, 2400.0F).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> LINK_TRANSLATOR = BLOCKS.register(Reference.BlockNames.LINK_TRANSLATOR,
            () -> new LinkTranslatorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                    .strength(25.0F, 600.0F).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> LINKING_PORTAL = BLOCKS.register(Reference.BlockNames.LINKING_PORTAL,
            () -> new LinkingPortalBlock(BlockBehaviour.Properties.of().noCollission()
                    .strength(-1.0F, -1.0F).sound(SoundType.GLASS).noOcclusion().lightLevel((state) -> {
                        return 11;
                    })));

    public static final DeferredBlock<Block> MARKER_SWITCH = BLOCKS.register(Reference.BlockNames.MARKER_SWITCH,
            () -> new MarkerSwitchBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(5)));

}