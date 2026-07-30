package me.giiena.pigeonchat.data;

import net.minecraft.world.level.block.Block;

public interface IBlockLootSubProvider {
    void dropSelf(Block block);
}
