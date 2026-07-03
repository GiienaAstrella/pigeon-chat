package com.ghifari160.pigeonchat.item;

import com.ghifari160.pigeonchat.PigeonChatCommon;
import com.ghifari160.pigeonchat.PigeonChatConfig;
import com.ghifari160.pigeonchat.component.Converted;
import com.ghifari160.pigeonchat.component.InkContainer;
import com.ghifari160.pigeonchat.component.PigeonChatComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Items {
    public static Item PEN;
    public static Item QUILL;
    public static Item INK_BOTTLE;

    public static final List<Supplier<ItemStack>> TAB_ITEMS = new ArrayList<>();

    public static void register(BiConsumer<Item, Identifier> consumer) {
        PEN = createInTab(
                ItemIDs.PEN,
                WritingUtensilItem::new,
                new Item.Properties()
                        .durability(PigeonChatConfig.COMMON.getOrDefault(
                                PigeonChatConfig.Key.PEN_FILL,
                                PigeonChatConfig.Default.PEN_FILL))
                        .component(DataComponents.DAMAGE, PigeonChatConfig.COMMON.getOrDefault(
                                PigeonChatConfig.Key.PEN_FILL,
                                PigeonChatConfig.Default.PEN_FILL))
                        .component(PigeonChatComponents.INK_CONTAINER,
                                InkContainer.withRefillable(true))
                        .component(PigeonChatComponents.UTENSIL, Unit.INSTANCE)
                        .component(PigeonChatComponents.UNBREAKABLE, true),
                stack -> {
                    stack.set(PigeonChatComponents.INK_CONTAINER,
                            InkContainer.withRefillable(true));
                    stack.set(PigeonChatComponents.UTENSIL, Unit.INSTANCE);
                    stack.set(PigeonChatComponents.INK_COLOR, DyeColor.BLACK);
                    stack.set(PigeonChatComponents.UNBREAKABLE, true);
                    stack.setDamageValue(0);
                });
        QUILL = createInTab(
                ItemIDs.QUILL,
                WritingUtensilItem::new,
                new Item.Properties()
                        .durability(PigeonChatConfig.COMMON.getOrDefault(
                                PigeonChatConfig.Key.QUILL_FILL,
                                PigeonChatConfig.Default.QUILL_FILL))
                        .component(PigeonChatComponents.INK_CONTAINER,
                                InkContainer.withRefillable(true))
                        .component(PigeonChatComponents.UTENSIL, Unit.INSTANCE),
                stack -> {
                    stack.set(PigeonChatComponents.INK_CONTAINER,
                            InkContainer.withRefillable(true));
                    stack.set(PigeonChatComponents.UTENSIL, Unit.INSTANCE);
                    stack.set(PigeonChatComponents.INK_COLOR, DyeColor.BLACK);
                    stack.set(PigeonChatComponents.CONVERTED,
                            new Converted(new ItemStack(net.minecraft.world.item.Items.FEATHER)));
                });
        INK_BOTTLE = createInTab(
                ItemIDs.INK_BOTTLE,
                InkContainerItem::new,
                new Item.Properties()
                        .durability(PigeonChatConfig.COMMON.getOrDefault(
                                PigeonChatConfig.Key.INK_BOTTLE_FILL,
                                PigeonChatConfig.Default.INK_BOTTLE_FILL))
                        .component(PigeonChatComponents.INK_CONTAINER,
                                InkContainerItem.component()),
                stack -> {
                    stack.set(PigeonChatComponents.INK_CONTAINER, InkContainerItem.component());
                    stack.set(PigeonChatComponents.INK_COLOR, DyeColor.BLACK);
                    stack.set(PigeonChatComponents.CONVERTED, new Converted(
                            new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE)));
                });

        consumer.accept(PEN, ItemIDs.PEN);
        consumer.accept(QUILL, ItemIDs.QUILL);
        consumer.accept(INK_BOTTLE, ItemIDs.INK_BOTTLE);
    }

    private static Item createInTab(final Identifier id) {
        return createInTab(id, Item::new, new Item.Properties(), stack -> {});
    }

    private static Item createInTab(
            final Identifier id,
            Function<Item.Properties, Item> factory,
            Item.Properties properties) {
        return createInTab(id, factory, properties, stack -> {});
    }

    private static Item createInTab(
            final Identifier id,
            Function<Item.Properties, Item> factory,
            Item.Properties properties,
            Consumer<ItemStack> stackConfig) {
        Item item = create(id, factory, properties);
        TAB_ITEMS.add(() -> {
            ItemStack stack = new ItemStack(item);
            stackConfig.accept(stack);
            return stack;
        });
        return item;
    }

    private static Item create(final Identifier id) {
        return create(id, Item::new, new Item.Properties());
    }

    private static Item create(final Identifier id, Function<Item.Properties, Item> factory,
                               Item.Properties properties) {
        return factory.apply(properties.setId(PigeonChatCommon.resourceKey(Registries.ITEM, id)));
    }
}
