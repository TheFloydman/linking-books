package thefloydman.linkingbooks.core.component;

import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thefloydman.linkingbooks.data.LinkData;
import thefloydman.linkingbooks.util.Reference;

public class ModDataComponents {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Reference.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LinkData>> LINK_DATA = DATA_COMPONENTS.registerComponentType(
            Reference.DataComponentNames.LINK_DATA, builder -> builder
                    .persistent(LinkData.CODEC)
                    .networkSynchronized(LinkData.STREAM_CODEC)
    );

}
