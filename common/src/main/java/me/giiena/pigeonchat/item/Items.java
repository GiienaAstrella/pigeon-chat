package me.giiena.pigeonchat.item;

import me.giiena.pigeonchat.PigeonChatConfig;
import me.giiena.pigeonchat.block.BlockItemIDs;
import me.giiena.pigeonchat.block.Blocks;
import me.giiena.pigeonchat.component.Consumables;
import me.giiena.pigeonchat.component.Converted;
import me.giiena.pigeonchat.component.InkContainer;
import me.giiena.pigeonchat.component.PigeonChatComponents;
import me.giiena.pigeonchat.entity.EntityTypes;
import me.giiena.pigeonchat.item.food.Foods;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings({"unused", "SameParameterValue"})
public class Items {
    private static final Map<ResourceKey<Item>, Item> ITEMS = new HashMap<>();

    public static final Item PEN = registerInTab(ItemIDs.PEN,
            WritingUtensilItem::new,
            new Item.Properties()
                    .durability(PigeonChatConfig.Common.PEN_FILL.get())
                    .component(DataComponents.DAMAGE, PigeonChatConfig.Common.PEN_FILL.get())
                    .component(PigeonChatComponents.INK_CONTAINER,
                            InkContainer.withRefillable(true))
                    .component(PigeonChatComponents.UTENSIL, Unit.INSTANCE)
                    .component(PigeonChatComponents.UNBREAKABLE, true),
            item -> new ItemStackTemplate(item, DataComponentPatch.builder()
                    .set(PigeonChatComponents.INK_CONTAINER, InkContainer.withRefillable(true))
                    .set(PigeonChatComponents.UTENSIL, Unit.INSTANCE)
                    .set(PigeonChatComponents.INK_COLOR, DyeColor.BLACK)
                    .set(PigeonChatComponents.UNBREAKABLE, true)
                    .set(DataComponents.DAMAGE, 0)
                    .build()));
    public static final Item QUILL = registerInTab(ItemIDs.QUILL,
            WritingUtensilItem::new,
            new Item.Properties()
                    .durability(PigeonChatConfig.Common.QUILL_FILL.get())
                    .component(PigeonChatComponents.INK_CONTAINER,
                            InkContainer.withRefillable(true))
                    .component(PigeonChatComponents.UTENSIL, Unit.INSTANCE),
            item -> new ItemStackTemplate(item, DataComponentPatch.builder()
                    .set(PigeonChatComponents.INK_CONTAINER, InkContainer.withRefillable(true))
                    .set(PigeonChatComponents.UTENSIL, Unit.INSTANCE)
                    .set(PigeonChatComponents.INK_COLOR, DyeColor.BLACK)
                    .set(PigeonChatComponents.CONVERTED,
                            new Converted(new ItemStack(net.minecraft.world.item.Items.FEATHER)))
                    .build()));
    public static final Item INK_BOTTLE = registerInTab(ItemIDs.INK_BOTTLE,
            InkContainerItem::new,
            new Item.Properties()
                    .durability(PigeonChatConfig.Common.INK_BOTTLE_FILL.get())
                    .component(PigeonChatComponents.INK_CONTAINER,
                            InkContainerItem.component()),
            item -> new ItemStackTemplate(item, DataComponentPatch.builder()
                    .set(PigeonChatComponents.INK_CONTAINER, InkContainerItem.component())
                    .set(PigeonChatComponents.INK_COLOR, DyeColor.BLACK)
                    .set(PigeonChatComponents.CONVERTED, new Converted(
                            new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE)))
                    .build()));
    public static final Item LETTER = registerInTab(ItemIDs.LETTER,
            LetterItem::new,
            new Item.Properties(),
            item -> new ItemStackTemplate(item, DataComponentPatch.builder()
                    .set(PigeonChatComponents.CONVERTED,
                            new Converted(new ItemStack(net.minecraft.world.item.Items.PAPER)))
                    .build()));
    public static final Item PIGEON_SPAWN_EGG = registerInTab(ItemIDs.PIGEON_SPAWN_EGG,
            SpawnEggItem::new,
            new Item.Properties().spawnEgg(EntityTypes.PIGEON));
    public static final Item PIGEON = registerInTab(ItemIDs.PIGEON,
            Item::new,
            new Item.Properties().food(Foods.PIGEON, Consumables.PIGEON));
    public static final Item COOKED_PIGEON = registerInTab(ItemIDs.COOKED_PIGEON,
            Item::new,
            new Item.Properties().food(Foods.COOKED_PIGEON));
    public static final Item BIRD_CAGE = registerBlockInTab(BlockItemIDs.BIRD_CAGE,
            BirdCageItem::new,
            Blocks.BIRD_CAGE,
            new Item.Properties(),
            ItemStackTemplate::new);

    public static void registerAll(BiConsumer<ResourceKey<Item>, Item> registry) {
        ITEMS.forEach(registry);

        CreativeTabs.addItem(CreativeModeTabs.FOOD_AND_DRINKS, PIGEON);
        CreativeTabs.addItem(CreativeModeTabs.FOOD_AND_DRINKS, COOKED_PIGEON);

        CreativeTabs.addItem(CreativeModeTabs.SPAWN_EGGS, PIGEON_SPAWN_EGG);
    }

    private static BlockItem registerBlockInTab(BlockItemId id,
                                           Block block,
                                           Item.Properties properties) {
        return registerBlockInTab(id, BlockItem::new, block, properties, ItemStackTemplate::new);
    }

    private static BlockItem registerBlockInTab(BlockItemId id,
                                           BiFunction<Block, Item.Properties, BlockItem> factory,
                                           Block block,
                                           Item.Properties properties,
                                           Function<BlockItem, ItemStackTemplate> templater) {
        BlockItem item = registerBlock(id, factory, block, properties);
        CreativeTabs.addItem(CreativeTabs.TAB, () -> templater.apply(item));
        return item;
    }

    private static BlockItem registerBlock(BlockItemId id,
                                           Block block,
                                           Item.Properties properties) {
        return registerBlock(id, BlockItem::new, block, properties);
    }

    private static BlockItem registerBlock(BlockItemId id,
                                           BiFunction<Block, Item.Properties, BlockItem> factory,
                                           Block block,
                                           Item.Properties properties) {
        BlockItem item = factory.apply(block,
                properties.useBlockDescriptionPrefix().setId(id.item()));
        ITEMS.put(id.item(), item);
        return item;
    }

    private static Item registerInTab(ResourceKey<Item> id) {
        return registerInTab(id, Item::new, new Item.Properties(), ItemStackTemplate::new);
    }

    private static Item registerInTab(ResourceKey<Item> id,
                                      Function<Item.Properties, Item> factory,
                                      Item.Properties properties) {
        return registerInTab(id, factory, properties, ItemStackTemplate::new);
    }

    private static Item registerInTab(ResourceKey<Item> id,
                                      Function<Item.Properties, Item> factory,
                                      Item.Properties properties,
                                      Function<Item, ItemStackTemplate> templater) {
        Item item = register(id, factory, properties);
        CreativeTabs.addItem(CreativeTabs.TAB, () -> templater.apply(item));
        return item;
    }

    private static Item register(ResourceKey<Item> id) {
        return register(id, Item::new, new Item.Properties());
    }

    private static Item register(ResourceKey<Item> id,
                                 Function<Item.Properties, Item> factory,
                                 Item.Properties properties) {
        Item item = factory.apply(properties.setId(id));
        ITEMS.put(id, item);
        return item;
    }
}
