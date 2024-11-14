package thefloydman.linkingbooks.linking;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import thefloydman.linkingbooks.data.LinkData;
import thefloydman.linkingbooks.util.Reference;

import javax.annotation.Nonnull;

/**
 * A Link Effect only containing a name.
 */
public record MobEffectLinkEffectType(@Nonnull ResourceLocation typeID, Holder<MobEffect> effect,
                                      int ticks) implements LinkEffectType {

    @Override
    public @Nonnull Codec<MobEffectLinkEffectType> codec() {
        return RecordCodecBuilder.create(
                codecBuilderInstance -> codecBuilderInstance.group(
                                ResourceLocation.CODEC.fieldOf("type").forGetter(MobEffectLinkEffectType::typeID),
                                MobEffect.CODEC.fieldOf("effect").forGetter(MobEffectLinkEffectType::effect),
                                Codec.INT.fieldOf("ticks").forGetter(MobEffectLinkEffectType::ticks)
                        )
                        .apply(codecBuilderInstance, MobEffectLinkEffectType::new)
        );
    }

    @Override
    public void onLinkEnd(Entity entity, LinkData linkData) {
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).addEffect(new MobEffectInstance(this.effect(), this.ticks()));
        }
    }

    @Override
    public @Nonnull ResourceLocation typeID() {
        return Reference.getAsResourceLocation(Reference.LinkEffectTypeNames.MOB_EFFECT);
    }

}