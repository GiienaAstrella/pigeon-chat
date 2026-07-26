package me.giiena.pigeonchat.block;

import me.giiena.pigeonchat.PigeonChatCommon;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;

public class BlockItemIDs {
    public static final BlockItemId BIRD_CAGE = create("bird_cage");

    public static BlockItemId create(String name) {
        Identifier id = PigeonChatCommon.identifier(name);
        return BlockItemId.create(id, id);
    }
}
