package thefloydman.linkingbooks.world.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import thefloydman.linkingbooks.util.Reference;

import java.util.function.Supplier;

public class ModEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE,
            Reference.MODID);

    public static final Supplier<EntityType<LinkingBookEntity>> LINKING_BOOK = ENTITIES
            .register(Reference.EntityNames.LINKING_BOOK,
                    () -> EntityType.Builder.<LinkingBookEntity>of(LinkingBookEntity::new, MobCategory.MISC)
                            .sized(0.3F, 0.1F).setTrackingRange(16)
                            .build(Reference.MODID + ":" + Reference.EntityNames.LINKING_BOOK));

}