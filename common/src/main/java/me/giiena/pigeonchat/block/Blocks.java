package me.giiena.pigeonchat.block;

import me.giiena.pigeonchat.component.PigeonChatComponents;
import me.giiena.pigeonchat.item.BirdCageItem;
import me.giiena.pigeonchat.item.CreativeTabs;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("SameParameterValue")
public class Blocks {
    private static final Map<ResourceKey<Block>, Block> BLOCKS = new HashMap<>();
    private static final Map<ResourceKey<Item>, BlockItem> BLOCK_ITEMS = new HashMap<>();

    public static final Block BIRD_CAGE = createInTab(BlockItemIDs.BIRD_CAGE,
            BirdCage::new,
            BlockBehaviour.Properties.of()
                    .sound(SoundType.IRON)
                    .noOcclusion()
                    .strength(2.0f, 1200.0f),
            BirdCageItem::new,
            new Item.Properties().stacksTo(1).component(PigeonChatComponents.UNBREAKABLE, true),
            CreativeTabs.TAB);

    public static void registerBlocks(BiConsumer<Block, ResourceKey<Block>> registry) {
        BLOCKS.forEach((key, block) -> registry.accept(block, key));
    }

    public static void registerItems(BiConsumer<Item, ResourceKey<Item>> registry) {
        BLOCK_ITEMS.forEach((key, blockItem) -> registry.accept(blockItem, key));
    }

    private static Block createInTab(BlockItemId id,
                                     Function<BlockBehaviour.Properties, Block> factory,
                                     BlockBehaviour.Properties properties,
                                     ResourceKey<CreativeModeTab> tab) {
        return createInTab(id, factory, properties, BlockItem::new, new Item.Properties(), tab);
    }

    private static Block createInTab(BlockItemId id,
                                     Function<BlockBehaviour.Properties, Block> blockFactory,
                                     BlockBehaviour.Properties blockProperties,
                                     BiFunction<Block, Item.Properties, BlockItem> itemFactory,
                                     Item.Properties itemProperties,
                                     ResourceKey<CreativeModeTab> tab) {
        Block block = create(id.block(), blockFactory, blockProperties);
        BlockItem item = createItem(id.item(), itemFactory, block, itemProperties);
        CreativeTabs.addItem(tab, item);
        return block;
    }

    private static BlockItem createItem(ResourceKey<Item> id,
                                        BiFunction<Block, Item.Properties, BlockItem> factory,
                                        Block block,
                                        Item.Properties properties) {
        BlockItem item = factory.apply(block, properties.useBlockDescriptionPrefix().setId(id));
        BLOCK_ITEMS.put(id, item);
        return item;
    }

    private static Block create(BlockItemId id,
                                Function<BlockBehaviour.Properties, Block> factory,
                                BlockBehaviour.Properties properties) {
        return create(id, factory, properties, BlockItem::new, new Item.Properties());
    }

    private static Block create(BlockItemId id,
                                Function<BlockBehaviour.Properties, Block> blockFactory,
                                BlockBehaviour.Properties blockProperties,
                                BiFunction<Block, Item.Properties, BlockItem> itemFactory,
                                Item.Properties itemProperties) {
        Block block = create(id.block(), blockFactory, blockProperties);
        createItem(id.item(), itemFactory, block, itemProperties);
        return block;
    }

    private static Block create(ResourceKey<Block> id,
                                Function<BlockBehaviour.Properties, Block> factory,
                                BlockBehaviour.Properties properties) {
        Block block = factory.apply(properties.setId(id));
        BLOCKS.put(id, block);
        return block;
    }
}
