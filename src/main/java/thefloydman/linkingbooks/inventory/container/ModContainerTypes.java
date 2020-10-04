package thefloydman.linkingbooks.inventory.container;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.util.Reference.ContainerNames;

public class ModContainerTypes {

    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister
            .create(ForgeRegistries.CONTAINERS, Reference.MOD_ID);

    public static final RegistryObject<ContainerType<LinkingBookContainer>> LINKING_BOOK = CONTAINERS
            .register(ContainerNames.LINKING_BOOK, () -> IForgeContainerType.create(LinkingBookContainer::new));

}
