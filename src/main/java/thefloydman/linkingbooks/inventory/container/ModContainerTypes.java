package thefloydman.linkingbooks.inventory.container;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import thefloydman.linkingbooks.util.Reference;

public class ModContainerTypes {

    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister
            .create(ForgeRegistries.CONTAINERS, Reference.MOD_ID);

    public static final RegistryObject<ContainerType<TestContainer>> TEST_CONTAINER = CONTAINERS
            .register("test_container", () -> new ContainerType<>(TestContainer::new));

}
