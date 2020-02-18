package thefloydman.linkingbooks.event;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import thefloydman.linkingbooks.util.Reference;

@EventBusSubscriber(modid = Reference.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class LinkingBooksEventHandler {

    @SubscribeEvent
    public static void createRegistries(RegistryEvent.NewRegistry event) {

    }

}
