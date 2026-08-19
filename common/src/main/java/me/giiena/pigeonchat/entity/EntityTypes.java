package me.giiena.pigeonchat.entity;

import me.giiena.pigeonchat.PigeonChatCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@SuppressWarnings("SameParameterValue")
public class EntityTypes {
    private static final Map<ResourceKey<EntityType<?>>, EntityType<?>> TYPES = new HashMap<>();

    public static final EntityType<Pigeon> PIGEON = registerType("pigeon",
            EntityType.Builder.<Pigeon>of(Pigeon::new, MobCategory.CREATURE)
                    .sized(0.4f, 0.75f)
                    .eyeHeight(0.64f));

    public static void registerTypes(
            BiConsumer<ResourceKey<EntityType<?>>, EntityType<?>> registry) {
        TYPES.forEach(registry);
    }

    private static <T extends Entity> EntityType<T> registerType(final String id,
                                                                 EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = resourceKey(id);
        EntityType<T> type = builder.build(key);
        TYPES.put(key, type);
        return type;
    }

    public static void registerAttributes(
            BiConsumer<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> consumer) {
        consumer.accept(PIGEON, Pigeon.createAttributes());
    }

    public static void registerSpawnPlacements(SpawnPlacementsRegistrar registrar) {
        registrar.register(
                PIGEON,
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules);
    }

    private static ResourceKey<EntityType<?>> resourceKey(final String id) {
        return PigeonChatCommon.resourceKey(Registries.ENTITY_TYPE, id);
    }

    @FunctionalInterface
    public interface SpawnPlacementsRegistrar {
        <T extends Mob> void register(EntityType<T> type,
                                      SpawnPlacementType placementType,
                                      Heightmap.Types heightMap,
                                      SpawnPlacements.SpawnPredicate<T> spawnPredicate);
    }
}
