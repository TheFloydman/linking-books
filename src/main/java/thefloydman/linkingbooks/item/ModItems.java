package thefloydman.linkingbooks.item;

import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import thefloydman.linkingbooks.block.ModBlocks;
import thefloydman.linkingbooks.fluid.ModFluids;
import thefloydman.linkingbooks.util.Reference;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);

    public static final RegistryObject<Item> BLANK_LINKING_BOOK = ITEMS.register(Reference.ItemNames.BLANK_LINKNG_BOOK,
            () -> new BlankLinkingBookItem(DyeColor.BLACK, new Item.Properties().maxStackSize(16)));

    public static final RegistryObject<Item> WRITTEN_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.WRITTEN_LINKNG_BOOK,
            () -> new WrittenLinkingBookItem(DyeColor.BLACK, new Item.Properties().maxStackSize(1)));

    public static final RegistryObject<Item> BOOK_DISPLAY = ITEMS.register(Reference.BlockNames.LINKING_LECTERN,
            () -> new BlockItem(ModBlocks.LINKING_LECTERN.get(),
                    new Item.Properties().group(ModItemGroups.LINKING_BOOKS)));

    public static final RegistryObject<Item> INK_BUCKET = ITEMS.register(Reference.ItemNames.INK_BUCKET,
            () -> new BucketItem(ModFluids.INK, new Item.Properties().group(ModItemGroups.LINKING_BOOKS)));

}
