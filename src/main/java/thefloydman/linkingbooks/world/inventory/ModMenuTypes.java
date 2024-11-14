package thefloydman.linkingbooks.world.inventory;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import thefloydman.linkingbooks.util.Reference;

import java.util.function.Supplier;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU,
            Reference.MODID);

    public static final Supplier<MenuType<LinkingBookMenuType>> LINKING_BOOK = MENU_TYPES
            .register(Reference.ContainerNames.LINKING_BOOK, () -> IMenuTypeExtension.create(LinkingBookMenuType::new));

    public static final Supplier<MenuType<GuidebookMenuType>> GUIDEBOOK = MENU_TYPES
            .register(Reference.ContainerNames.GUIDEBOOK, () -> new MenuType<>(GuidebookMenuType::new, FeatureFlags.DEFAULT_FLAGS));

}