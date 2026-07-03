package com.ghifari160.pigeonchat.util;

import com.ghifari160.pigeonchat.PigeonChatCommon;
import com.ghifari160.pigeonchat.component.Converted;
import com.ghifari160.pigeonchat.component.InkContainer;
import com.ghifari160.pigeonchat.component.PigeonChatComponents;
import com.ghifari160.pigeonchat.tag.ItemTags;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class ContainerUtils {
    /**
     * Checks whether {@code stack} is a Container.
     */
    public static boolean isContainer(ItemStack stack) {
        InkContainer container = stack.get(PigeonChatComponents.INK_CONTAINER);
        return container != null;
    }

    /**
     * Checks whether {@code stack} is a Refillable Container.
     */
    public static boolean isRefillable(ItemStack stack) {
        InkContainer container = stack.get(PigeonChatComponents.INK_CONTAINER);
        return container != null && container.isRefillable();
    }

    /**
     * Checks whether {@code stack} is a Drainable Container.
     */
    public static boolean isDrainable(ItemStack stack) {
        InkContainer container = stack.get(PigeonChatComponents.INK_CONTAINER);
        return container != null && container.isDrainable();
    }

    /**
     * Checks whether {@code stack} is a writing utensil.
     */
    public static boolean isUtensil(ItemStack stack) {
        return isContainer(stack) && stack.has(PigeonChatComponents.UTENSIL);
    }

    /**
     * Checks whether {@code stack} is Unbreakable.
     */
    public static boolean isUnbreakable(ItemStack stack) {
        return stack.getOrDefault(PigeonChatComponents.UNBREAKABLE, false);
    }

    /**
     * Returns the remaining fill in a Container {@code stack}.
     */
    public static int remainingFill(ItemStack stack) {
        return isContainer(stack) ? stack.getMaxDamage() - stack.getDamageValue() : 0;
    }

    /**
     * Returns the color of the ink in a Container {@code stack}.
     */
    @Nullable
    public static DyeColor inkColor(ItemStack stack) {
        if (!isContainer(stack)) return null;
        return stack.get(PigeonChatComponents.INK_COLOR);
    }

    /**
     * Refills a Container {@code stack} by {@code amount}.
     * @return Amount consumed from {@code amount}.
     */
    public static int refill(ItemStack stack, final int amount) {
        if (!isRefillable(stack)) return 0;

        int damage = stack.getDamageValue();
        int consumed = Math.min(amount, damage);
        stack.setDamageValue(damage - consumed);
        return consumed;
    }

    /**
     * Drains the fill of a Container {@code stack} by {@code amount}.
     * Containers will be drained only up to their maximum capacity.
     * Containers are broken when they are empty unless they are Unbreakable.
     * If {@code stack} was converted from a base material, the conversion will be reversed instead
     * of the Container breaking.
     */
    public static void drain(
            ItemStack stack,
            final int amount,
            Player player,
            InteractionHand hand) {
        drain(stack, amount, player, hand.asEquipmentSlot());
    }

    /**
     * Drains the fill of a Container {@code stack} by {@code amount}.
     * Containers will be drained only up to their maximum capacity.
     * Containers are broken when they are empty unless they are Unbreakable.
     * If {@code stack} was converted from a base material, the conversion will be reversed instead
     * of the Container breaking.
     */
    public static void drain(
            ItemStack stack,
            final int amount,
            Player player,
            EquipmentSlot slot) {
        if (!isDrainable(stack)) return;

        int nextDamage = stack.getDamageValue() + amount;
        int maxDamage = stack.getMaxDamage();
        stack.setDamageValue(nextDamage);

        if (nextDamage >= maxDamage && !isUnbreakable(stack)) {
            Converted converted = stack.get(PigeonChatComponents.CONVERTED);
            stack.shrink(1);
            if (converted != null) {
                player.setItemSlot(slot, converted.baseItem());
            }
        }
    }

    /**
     * Drains the fill of a Container {@code stack} that is also a writing utensil by
     * {@code amount}.
     * Containers will be drained only up to their maximum capacity.
     * Containers are broken when they are empty unless they are Unbreakable.
     * If {@code stack} was converted from a base material, the conversion will be reversed instead
     * of the Container breaking.
     */
    public static void write(
            ItemStack stack,
            final int amount,
            Player player,
            InteractionHand hand) {
        write(stack, amount, player, hand.asEquipmentSlot());
    }

    /**
     * Drains the fill of a Container {@code stack} that is also a writing utensil by
     * {@code amount}.
     * Containers will be drained only up to their maximum capacity.
     * Containers are broken when they are empty unless they are Unbreakable.
     * If {@code stack} was converted from a base material, the conversion will be reversed instead
     * of the Container breaking.
     */
    public static void write (
            ItemStack stack,
            final int amount,
            Player player,
            EquipmentSlot slot) {
        if (!isUtensil(stack)) return;

        int nextDamage = stack.getDamageValue() + amount;
        int maxDamage = stack.getMaxDamage();
        stack.setDamageValue(nextDamage);

        if (nextDamage >= maxDamage && !isUnbreakable(stack)) {
            Converted converted = stack.get(PigeonChatComponents.CONVERTED);
            stack.shrink(1);
            if (converted != null) {
                player.setItemSlot(slot, converted.baseItem());
            }
        }
    }

    /**
     * Returns {@code DyeColor} from {@code stack} by checking the conventional tags.
     */
    public static Optional<DyeColor> dyeFromStack(ItemStack stack) {
        for (DyeColor color : DyeColor.values()) {
            TagKey<Item> tag = ItemTags.create("c", "dyes/" + color.getName());
            if (stack.is(tag)) {
                return Optional.of(color);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns a proper container name for {@code stack}.
     * If {@code stack} contains an ink, it will be suffixed with {@code (INK_NAME)}.
     * Otherwise, the name is returned unmodified.
     */
    public static Component containerName(ItemStack stack, Function<ItemStack, Component> provider) {
        DyeColor color = inkColor(stack);
        if (color == null) {
            return Component.translatable(
                    PigeonChatCommon.langKey("ink_container", "item", "empty"),
                    provider.apply(stack));
        }

        return Component.translatable(
                PigeonChatCommon.langKey("ink_container", "item", "filled"),
                provider.apply(stack),
                Component.translatable("color.minecraft." + color.getName())
                        .withColor(color.getTextColor()));
    }
}
