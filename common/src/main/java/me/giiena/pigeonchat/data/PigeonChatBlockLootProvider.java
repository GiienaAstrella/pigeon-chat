package me.giiena.pigeonchat.data;

import me.giiena.pigeonchat.block.Blocks;

public class PigeonChatBlockLootProvider {
    protected static void generate(IBlockLootSubProvider sub) {
        sub.dropSelf(Blocks.BIRD_CAGE);
    }
}
