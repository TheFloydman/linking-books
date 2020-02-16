package thefloydman.linkingbooks.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ObjectHolder;
import thefloydman.linkingbooks.item.InkBucketItem;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.util.Reference.ItemNames;

@EventBusSubscriber(modid = Reference.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MOD_ID)
public class LinkingBooksItems {

    @ObjectHolder(ItemNames.INK_BUCKET)
    public static final Item INK_BUCKET = null;

    @SubscribeEvent
    public static void registerBlockItems(RegistryEvent.Register<Item> event) {
        event.getRegistry()
                .registerAll(new InkBucketItem(() -> LinkingBooksFluids.INK,
                        new Item.Properties().group(ItemGroup.MISC).maxStackSize(1)).setRegistryName(Reference.MOD_ID,
                                Reference.ItemNames.INK_BUCKET));
    }

}
