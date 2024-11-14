package thefloydman.linkingbooks.client.sound;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import thefloydman.linkingbooks.util.Reference;

import java.util.function.Supplier;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT,
            Reference.MODID);

    public static final Supplier<SoundEvent> PAGEFLIP_FORWARD = SOUNDS
            .register(Reference.SoundNames.PAGEFLIP_FORWARD, () -> SoundEvent
                    .createVariableRangeEvent(Reference.getAsResourceLocation(Reference.SoundNames.PAGEFLIP_FORWARD)));

    public static final Supplier<SoundEvent> PAGEFLIP_BACK = SOUNDS.register(Reference.SoundNames.PAGEFLIP_BACK,
            () -> SoundEvent
                    .createVariableRangeEvent(Reference.getAsResourceLocation(Reference.SoundNames.PAGEFLIP_BACK)));

    public static final Supplier<SoundEvent> BOOK_CLOSE = SOUNDS.register(Reference.SoundNames.BOOK_CLOSE,
            () -> SoundEvent
                    .createVariableRangeEvent(Reference.getAsResourceLocation(Reference.SoundNames.BOOK_CLOSE)));

}