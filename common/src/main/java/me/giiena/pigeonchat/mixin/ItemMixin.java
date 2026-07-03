package me.giiena.pigeonchat.mixin;

import me.giiena.pigeonchat.Constants;
import me.giiena.pigeonchat.PigeonChatConfig;
import me.giiena.pigeonchat.component.Converted;
import me.giiena.pigeonchat.component.InkContainer;
import me.giiena.pigeonchat.component.PigeonChatComponents;
import me.giiena.pigeonchat.item.InkContainerItem;
import me.giiena.pigeonchat.item.Items;
import me.giiena.pigeonchat.tag.ItemTags;
import me.giiena.pigeonchat.util.ContainerUtils;
import me.giiena.pigeonchat.util.InteractionUtils;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Inject(
            method = "use",
            at = @At("HEAD"),
            cancellable = true
    )
    public void pigeonchat$use(
            @NonNull Level level,
            Player player,
            @NonNull InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack heldItem = player.getItemInHand(hand);
        InteractionResult res = null;

        // PotionItem does not implement #use.
        // So, we inject into Item and guard for PotionItem.
        if((Item)(Object) this instanceof PotionItem) {
            res = PotionItem$use(level, player, hand);
        } else if (heldItem.is(ItemTags.QUILL_MATERIALS)) {
            res = QuillMaterials$use(level, player, hand);
        } else if (heldItem.is(ItemTags.WRITABLE_NAME_TAGS) &&
                PigeonChatConfig.COMMON.getOrDefault(
                        PigeonChatConfig.Key.NAME_TAG_VIEWABLE,
                        PigeonChatConfig.Default.NAME_TAG_VIEWABLE)) {
            res = NameTagItem$use(level, player, hand);
        }

        if (res != null) cir.setReturnValue(res);
    }

    @Unique
    @SuppressWarnings("unused")
    private InteractionResult PotionItem$use(
            @NonNull Level level,
            Player player,
            @NonNull InteractionHand hand) {
        ItemStack container = player.getItemInHand(hand);
        ItemStack material = player.getItemInHand(InteractionUtils.otherHand(hand));
        if (!material.is(net.minecraft.tags.ItemTags.DYES)) {
            return null;
        }

        Optional<DyeColor> color = ContainerUtils.dyeFromStack(material);

        int consumeAmount = PigeonChatConfig.COMMON.getOrDefault(
                PigeonChatConfig.Key.INK_BOTTLE_DYE_REFILL,
                PigeonChatConfig.Default.INK_BOTTLE_DYE_REFILL);

        if (material.count() < consumeAmount) return null;
        material.consume(consumeAmount, player);

        ItemStack newContainer = new ItemStack(Items.INK_BOTTLE);
        newContainer.set(PigeonChatComponents.INK_CONTAINER, InkContainerItem.component());
        color.ifPresentOrElse(
                c -> newContainer.set(PigeonChatComponents.INK_COLOR, c),
                () -> {
                    newContainer.set(PigeonChatComponents.INK_COLOR, DyeColor.BLACK);
                    Constants.LOG.error("{} not in dye tag! Falling back to {}",
                            material.getItemName(),
                            DyeColor.BLACK.getName());
                });
        newContainer.set(PigeonChatComponents.CONVERTED,
                new Converted(new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE)));

        container.consume(1, player);
        if (container.isEmpty()) {
            container = newContainer;
        } else {
            if (!player.getInventory().add(newContainer)) {
                player.drop(newContainer, false);
            }
        }
        return InteractionResult.SUCCESS.heldItemTransformedTo(container);
    }

    @Unique
    @SuppressWarnings("unused")
    private InteractionResult QuillMaterials$use(
            @NonNull Level level,
            Player player,
            @NonNull InteractionHand hand) {
        ItemStack material = player.getItemInHand(hand);
        InteractionHand otherHand = InteractionUtils.otherHand(hand);
        ItemStack other = player.getItemInHand(otherHand);

        if (!ContainerUtils.isRefillable(other) ||
                !ContainerUtils.isDrainable(other) ||
                ContainerUtils.remainingFill(other) < 1) {
            return null;
        }

        int remainingFill = ContainerUtils.remainingFill(other);
        DyeColor color = ContainerUtils.inkColor(other);
        if (color == null) {
            Constants.LOG.error("{} has no color! Falling back to {}",
                    other.getItemName(),
                    DyeColor.BLACK.getName());
            color = DyeColor.BLACK;
        }

        ItemStack utensil = new ItemStack(Items.QUILL);
        utensil.set(PigeonChatComponents.INK_CONTAINER, InkContainer.withRefillable(true));
        utensil.set(PigeonChatComponents.UTENSIL, Unit.INSTANCE);
        utensil.set(PigeonChatComponents.INK_COLOR, color);
        utensil.set(PigeonChatComponents.CONVERTED,
                new Converted(material));

        int maxDamage = utensil.getMaxDamage();
        if (remainingFill < maxDamage) {
            utensil.setDamageValue(maxDamage - remainingFill);
        }
        ContainerUtils.drain(other, maxDamage, player, otherHand);

        material.consume(1, player);
        if (material.isEmpty()) {
            material = utensil;
        } else {
            if (!player.getInventory().add(utensil)) {
                player.drop(utensil, false);
            }
        }
        return InteractionResult.SUCCESS.heldItemTransformedTo(material);
    }

    @SuppressWarnings("unused")
    @Unique
    private InteractionResult NameTagItem$use(
            @NonNull Level level,
            Player player,
            @NonNull InteractionHand hand) {
        ItemStack other = player.getItemInHand(InteractionUtils.otherHand(hand));
        if (ContainerUtils.isUtensil(other)) return InteractionResult.PASS;

        player.openItemGui(player.getItemInHand(hand), hand);
        return InteractionResult.SUCCESS;
    }
}
