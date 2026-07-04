package me.giiena.pigeonchat.data;

import me.giiena.pigeonchat.entity.EntityTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import org.jspecify.annotations.NonNull;

import java.util.stream.Stream;

public class EntityLootSubProvider extends net.minecraft.data.loot.EntityLootSubProvider {
    public EntityLootSubProvider(HolderLookup.Provider registries) {
        super(FeatureFlags.DEFAULT_FLAGS, registries);
    }

    @Override
    @NonNull
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return Stream.of(EntityTypes.PIGEON);
    }

    @Override
    public void generate() {
        PigeonChatEntityLootProvider.generate(this::add);
    }
}
