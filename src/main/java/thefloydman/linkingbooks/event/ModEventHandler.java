package thefloydman.linkingbooks.event;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.RegistryBuilder;
import thefloydman.linkingbooks.api.linking.LinkEffect;
import thefloydman.linkingbooks.util.Reference;

@EventBusSubscriber(modid = Reference.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventHandler {

    /**
     * Use to create new registries.
     */
    @SubscribeEvent
    public static void createNewRegistries(RegistryEvent.NewRegistry event) {
        RegistryBuilder<LinkEffect> linkEffectRegistryBuilder = new RegistryBuilder<LinkEffect>();
        linkEffectRegistryBuilder.setName(Reference.getAsResourceLocation("link_effect"));
        linkEffectRegistryBuilder.setType(LinkEffect.class);
        linkEffectRegistryBuilder.create();
    }

}
