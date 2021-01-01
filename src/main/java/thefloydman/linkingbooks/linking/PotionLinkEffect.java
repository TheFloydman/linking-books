package thefloydman.linkingbooks.linking;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.api.linking.LinkEffect;

public class PotionLinkEffect extends LinkEffect {

    private Effect effect;
    private int ticks;

    public PotionLinkEffect(Effect effect, int ticks) {
        this.effect = effect;
        this.ticks = ticks;
    }

    @Override
    public void onLinkEnd(Entity entity, ILinkData linkData) {
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).addPotionEffect(new EffectInstance(this.effect, ticks));
        }
    }

}