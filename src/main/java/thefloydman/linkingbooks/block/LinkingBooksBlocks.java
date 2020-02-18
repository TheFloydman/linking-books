package thefloydman.linkingbooks.block;

import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ObjectHolder;
import thefloydman.linkingbooks.block.material.LinkingBooksMaterials;
import thefloydman.linkingbooks.fluid.LinkingBooksFluids;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.util.Reference.BlockNames;

@EventBusSubscriber(modid = Reference.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MOD_ID)
public class LinkingBooksBlocks {

    @ObjectHolder(BlockNames.INK_MIXER)
    public static final Block INK_MIXER = null;

    @ObjectHolder(BlockNames.INK)
    public static final FlowingFluidBlock INK = null;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                new InkMixerBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(5).harvestLevel(2)
                        .harvestTool(ToolType.PICKAXE)).setRegistryName(Reference.MOD_ID, BlockNames.INK_MIXER),
                new FlowingFluidBlock(() -> LinkingBooksFluids.PLAIN_INK,
                        Block.Properties.create(LinkingBooksMaterials.INK).doesNotBlockMovement()
                                .hardnessAndResistance(100.0F).noDrops()).setRegistryName(Reference.MOD_ID,
                                        BlockNames.INK));
    }

    @SubscribeEvent
    public static void registerBlockItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(new BlockItem(INK_MIXER, new Item.Properties().group(ItemGroup.MISC))
                .setRegistryName(INK_MIXER.getRegistryName()));
    }

}
