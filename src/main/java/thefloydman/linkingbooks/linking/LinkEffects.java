package thefloydman.linkingbooks.linking;

import net.minecraft.potion.Effects;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import thefloydman.linkingbooks.api.linking.LinkEffect;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.util.Reference.LinkEffectNames;

public class LinkEffects {

    public static final DeferredRegister<LinkEffect> LINK_EFFECTS = DeferredRegister.create(LinkEffect.class,
            Reference.MOD_ID);

    public static final RegistryObject<PotionLinkEffect> POISON_EFFECT = LINK_EFFECTS
            .register(LinkEffectNames.POISON_EFFECT, () -> new PotionLinkEffect(Effects.POISON, 20 * 10));

}
