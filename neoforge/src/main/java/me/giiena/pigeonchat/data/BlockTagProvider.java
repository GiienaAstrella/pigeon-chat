package me.giiena.pigeonchat.data;

import me.giiena.pigeonchat.Constants;
import me.giiena.pigeonchat.block.BlockItemIDs;
import me.giiena.pigeonchat.tag.BlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider {
    public BlockTagProvider(PackOutput output,
                            CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Constants.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {
        this.tag(BlockTags.MINEABLE_PICKAXE)
                .add(BlockItemIDs.BIRD_CAGE.block());
    }
}
