package thefloydman.linkingbooks.linking;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import thefloydman.linkingbooks.util.Reference;

public record LinkEffect(LinkEffectType type) {

    public static final ResourceKey<Registry<LinkEffect>> REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Reference.MODID, Reference.RegistryKeyNames.LINK_EFFECT));

    public static final Codec<LinkEffect> CODEC = RecordCodecBuilder.create(
            codecBuilderInstance -> codecBuilderInstance.group(
                            ResourceLocation.CODEC.fieldOf("type")
                                    .xmap(resourceLocation -> LinkEffectTypes.REGISTRY.get(Reference.getAsResourceLocation(Reference.LinkEffectTypeNames.BASIC)), LinkEffectType::typeID)
                                    .forGetter(LinkEffect::type)
                    )
                    .apply(codecBuilderInstance, LinkEffect::new)
    );

}