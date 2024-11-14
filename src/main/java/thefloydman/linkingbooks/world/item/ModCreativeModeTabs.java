package thefloydman.linkingbooks.world.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thefloydman.linkingbooks.util.Reference;

public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, Reference.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS
            .register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.linkingbooks"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> ModItems.BOOKSHELF_STAIRS.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.GUIDEBOOK.get());
                        output.accept(ModItems.LINKING_LECTERN.get());
                        output.accept(ModItems.NARA.get());
                        output.accept(ModItems.LINK_TRANSLATOR.get());
                        output.accept(ModItems.MARKER_SWITCH.get());
                        output.accept(ModItems.LINKING_PANEL.get());
                        output.accept(ModItems.BLANK_LINKING_BOOK.get());
                        output.accept(ModItems.BOOKSHELF_STAIRS.get());
                    }).build());

}
