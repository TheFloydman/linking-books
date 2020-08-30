package thefloydman.linkingbooks.entity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.util.Reference.EntityNames;

public class ModEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES,
            Reference.MOD_ID);

    public static final RegistryObject<EntityType<LinkingBookEntity>> LINKING_BOOK = ENTITIES.register(
            EntityNames.LINKING_BOOK,
            () -> EntityType.Builder.<LinkingBookEntity>create(LinkingBookEntity::new, EntityClassification.MISC)
                    .size(0.5F, 0.1F).setTrackingRange(16).build(Reference.MOD_ID + ":" + EntityNames.LINKING_BOOK));

    public static final RegistryObject<EntityType<DescriptiveBookEntity>> DESCRIPTIVE_BOOK = ENTITIES.register(
            EntityNames.DESCRIPTIVE_BOOK,
            () -> EntityType.Builder
                    .<DescriptiveBookEntity>create(DescriptiveBookEntity::new, EntityClassification.MISC)
                    .size(0.5F, 0.1F).setTrackingRange(16)
                    .build(Reference.MOD_ID + ":" + EntityNames.DESCRIPTIVE_BOOK));

}
