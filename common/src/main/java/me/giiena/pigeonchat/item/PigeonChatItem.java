package me.giiena.pigeonchat.item;

import net.minecraft.world.item.ItemStack;

/**
 * A subset of NeoForge's IItemExtension specifically dealing with damages.
 */
public interface PigeonChatItem {
    default boolean pigeonchat$isDamaged(ItemStack stack) {
        return ItemUtils.isDamaged(stack);
    }

    default int pigeonchat$getDamage(ItemStack stack) {
        return ItemUtils.getDamage(stack);
    }

    default void pigeonchat$setDamage(ItemStack stack, int damage) {
        ItemUtils.setDamage(stack, damage);
    }

    default int pigeonchat$getMaxDamage(ItemStack stack) {
        return ItemUtils.getMaxDamage(stack);
    }
}
