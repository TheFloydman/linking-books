package thefloydman.linkingbooks.init;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ObjectHolder;
import thefloydman.linkingbooks.blocks.InkMixerBlock;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.util.Reference.BlockNames;

@EventBusSubscriber(modid = Reference.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MOD_ID)
public class LinkingBooksBlocks {

    @ObjectHolder(BlockNames.INK_MIXER)
    public static final Block INK_MIXER_BLOCK = null;

    @ObjectHolder(BlockNames.INK_MIXER)
    public static final Item INK_MIXER_ITEM = null;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry()
                .registerAll(new InkMixerBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(5)
                        .harvestLevel(2).harvestTool(ToolType.PICKAXE)).setRegistryName(Reference.MOD_ID,
                                BlockNames.INK_MIXER));
    }

    @SubscribeEvent
    public static void registerBlockItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(new BlockItem(INK_MIXER_BLOCK, new Item.Properties().group(ItemGroup.MISC)));
    }

}
