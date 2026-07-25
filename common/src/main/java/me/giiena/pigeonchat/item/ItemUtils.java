package me.giiena.pigeonchat.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public final class ItemUtils {
    public static boolean isDamaged(ItemStack stack) {
        return stack.getDamageValue() > 0;
    }

    public static int getDamage(ItemStack stack) {
        return Mth.clamp(stack.getOrDefault(DataComponents.DAMAGE, 0), 0, stack.getMaxDamage());
    }

    public static void setDamage(ItemStack stack, int damage) {
        stack.set(DataComponents.DAMAGE, Mth.clamp(damage, 0, stack.getMaxDamage()));
    }

    public static int getMaxDamage(ItemStack stack) {
        return stack.getOrDefault(DataComponents.MAX_DAMAGE, 0);
    }
}
