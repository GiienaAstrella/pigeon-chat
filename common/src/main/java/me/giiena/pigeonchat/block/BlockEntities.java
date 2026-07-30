package me.giiena.pigeonchat.block;

import me.giiena.pigeonchat.PigeonChatCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

@SuppressWarnings("SameParameterValue")
public class BlockEntities {
    private static final Map<ResourceKey<BlockEntityType<?>>, BlockEntityType<?>> TYPES =
            new HashMap<>();

    public static final BlockEntityType<BirdCageEntity> BIRD_CAGE = create("bird_cage",
            BirdCageEntity::new,
            Blocks.BIRD_CAGE);

    public static void register(
            BiConsumer<ResourceKey<BlockEntityType<?>>, BlockEntityType<?>> registry) {
        TYPES.forEach(registry);
    }

    private static <T extends BlockEntity> BlockEntityType<T> create(
            String name,
            BlockEntityType.BlockEntitySupplier<T> factory,
            Block... blocks) {
        Identifier id = PigeonChatCommon.identifier(name);
        ResourceKey<BlockEntityType<?>> key = PigeonChatCommon.resourceKey(Registries.BLOCK_ENTITY_TYPE, id);
        BlockEntityType<T> type = new BlockEntityType<>(factory, Set.of(blocks));
        TYPES.put(key, type);
        return type;
    }
}
