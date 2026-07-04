package me.giiena.pigeonchat.data;

import me.giiena.pigeonchat.PigeonChatCommon;
import me.giiena.pigeonchat.entity.EntityTypes;
import me.giiena.pigeonchat.entity.Pigeon;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class BiomeModifiers {
    public static final ResourceKey<BiomeModifier> PIGEON_SPAWN =
            PigeonChatCommon.resourceKey(NeoForgeRegistries.Keys.BIOME_MODIFIERS, "pigeon_spawn");

    public static void bootstrap(BootstrapContext<BiomeModifier> bootstrap) {
        HolderGetter<Biome> biomes = bootstrap.lookup(Registries.BIOME);

        bootstrap.register(PIGEON_SPAWN,
                net.neoforged.neoforge.common.world.BiomeModifiers.AddSpawnsBiomeModifier
                        .singleSpawn(biomes.getOrThrow(BiomeTags.IS_OVERWORLD), new Weighted<>(
                                new MobSpawnSettings.SpawnerData(EntityTypes.PIGEON,
                                        Pigeon.MIN_SPAWN_COUNT,
                                        Pigeon.MAX_SPAWN_COUNT), Pigeon.SPAWN_WEIGHT)));
    }
}
