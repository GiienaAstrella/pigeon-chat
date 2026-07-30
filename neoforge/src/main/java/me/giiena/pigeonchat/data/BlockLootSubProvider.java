package me.giiena.pigeonchat.data;

import me.giiena.pigeonchat.block.Blocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Set;

public class BlockLootSubProvider extends net.minecraft.data.loot.BlockLootSubProvider
        implements IBlockLootSubProvider {
    public BlockLootSubProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, registries);
    }

    @Override
    protected void generate() {
        PigeonChatBlockLootProvider.generate(this);
    }

    @Override
    @NonNull
    protected Iterable<Block> getKnownBlocks() {
        return List.of(Blocks.BIRD_CAGE);
    }

    @Override
    public void dropSelf(@NonNull Block block) {
        super.dropSelf(block);
    }
}
