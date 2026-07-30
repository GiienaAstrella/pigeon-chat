package me.giiena.pigeonchat.tag;

import me.giiena.pigeonchat.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class BlockTags {
    public static final TagKey<Block> MINEABLE_PICKAXE = create("minecraft", "mineable/pickaxe");

    private static TagKey<Block> create(final String id) {
        return create(Constants.MOD_ID, id);
    }

    private static TagKey<Block> create(final String namespace, final String id) {
        return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(namespace, id));
    }
}
