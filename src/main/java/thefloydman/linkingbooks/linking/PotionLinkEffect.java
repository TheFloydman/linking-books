package thefloydman.linkingbooks.linking;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import thefloydman.linkingbooks.api.linking.LinkEffect;

public class PotionLinkEffect extends LinkEffect {

    private Effect effect;
    private int ticks;

    public PotionLinkEffect(Effect effect, int ticks) {
        this.effect = effect;
        this.ticks = ticks;
    }

    @Override
    public void onLinkEnd(ServerPlayerEntity player) {
        player.addPotionEffect(new EffectInstance(this.effect, ticks));
    }

}