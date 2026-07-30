package me.giiena.pigeonchat.block;

import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("SameParameterValue")
public class Blocks {
    private static final Map<ResourceKey<Block>, Block> BLOCKS = new HashMap<>();

    public static final Block BIRD_CAGE = register(BlockItemIDs.BIRD_CAGE,
            BirdCage::new,
            BlockBehaviour.Properties.of()
                    .sound(SoundType.IRON)
                    .noOcclusion()
                    .strength(2.0f, 1200.0f));

    public static void registerAll(BiConsumer<ResourceKey<Block>, Block> registry) {
        BLOCKS.forEach(registry);
    }

    private static Block register(BlockItemId id,
                                  Function<BlockBehaviour.Properties, Block> factory,
                                  BlockBehaviour.Properties properties) {
        return register(id.block(), factory, properties);
    }

    private static Block register(ResourceKey<Block> id,
                                  Function<BlockBehaviour.Properties, Block> factory,
                                  BlockBehaviour.Properties properties) {
        Block block = factory.apply(properties.setId(id));
        BLOCKS.put(id, block);
        return block;
    }
}
