package thefloydman.linkingbooks.linking;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import thefloydman.linkingbooks.util.Reference;

public class LinkEffectTypes {

    public static final ResourceKey<Registry<LinkEffectType>> REGISTRY_KEY = ResourceKey.createRegistryKey(Reference.getAsResourceLocation(Reference.RegistryKeyNames.LINK_EFFECT_TYPE));
    public static final Registry<LinkEffectType> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY)
            .defaultKey(Reference.getAsResourceLocation(Reference.LinkEffectTypeNames.BASIC))
            .create();
    public static final DeferredRegister<LinkEffectType> LINK_EFFECT_TYPES = DeferredRegister
            .create(REGISTRY, Reference.MODID);

    public static final DeferredHolder<LinkEffectType, BasicLinkEffectType> BASIC = LINK_EFFECT_TYPES
            .register(Reference.LinkEffectTypeNames.BASIC, BasicLinkEffectType::new);

    public static final DeferredHolder<LinkEffectType, MobEffectLinkEffectType> MOB_EFFECT = LINK_EFFECT_TYPES
            .register(Reference.LinkEffectTypeNames.MOB_EFFECT, resourceLocation -> new MobEffectLinkEffectType(resourceLocation, MobEffects.HEAL, 0));

}