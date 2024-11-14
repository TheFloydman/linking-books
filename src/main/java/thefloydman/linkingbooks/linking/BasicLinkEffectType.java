package thefloydman.linkingbooks.linking;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import thefloydman.linkingbooks.util.Reference;

import javax.annotation.Nonnull;

/**
 * A Link Effect only containing a name.
 */
public record BasicLinkEffectType(@Nonnull ResourceLocation typeID) implements LinkEffectType {

    @Override
    public @Nonnull Codec<BasicLinkEffectType> codec() {
        return RecordCodecBuilder.create(
                codecBuilderInstance -> codecBuilderInstance.group(
                                ResourceLocation.CODEC.fieldOf("type").forGetter(BasicLinkEffectType::typeID)
                        )
                        .apply(codecBuilderInstance, BasicLinkEffectType::new)
        );
    }

    @Override
    public @Nonnull ResourceLocation typeID() {
        return Reference.getAsResourceLocation(Reference.LinkEffectTypeNames.BASIC);
    }

}