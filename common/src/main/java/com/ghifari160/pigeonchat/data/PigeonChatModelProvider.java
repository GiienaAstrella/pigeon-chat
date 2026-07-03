package com.ghifari160.pigeonchat.data;

import com.ghifari160.pigeonchat.PigeonChatCommon;
import com.ghifari160.pigeonchat.client.color.item.InkContainer;
import com.ghifari160.pigeonchat.item.Items;
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PigeonChatModelProvider {
    private PigeonChatModelProvider() {}

    public static void generateItemModels(ItemModelGenerators itemModelGenerators) {
        generatePenModel(itemModelGenerators, Items.PEN, ModelTemplates.FLAT_HANDHELD_ITEM);
        generateInkContainerModel(
                itemModelGenerators,
                Items.QUILL,
                Identifier.withDefaultNamespace("item/feather"),
                PigeonChatCommon.identifier("item/quill_fill"),
                1);
        generateInkContainerModel(
                itemModelGenerators,
                Items.INK_BOTTLE,
                PigeonChatCommon.identifier("item/ink_bottle_fill"),
                Identifier.withDefaultNamespace("item/glass_bottle"),
                0);
    }

    @SuppressWarnings("SameParameterValue")
    private static void generatePenModel(ItemModelGenerators gen, Item item, ModelTemplate template) {
        ItemModel.Unbaked barrel = ItemModelUtils.plainModel(
                gen.createFlatItemModel(item, "_barrel", template));
        ItemModel.Unbaked nib = ItemModelUtils.plainModel(
                gen.createFlatItemModel(item, "_nib", template));
        ItemModel.Unbaked fill = ItemModelUtils.tintedModel(
                gen.createFlatItemModel(item, "_fill", template),
                new InkContainer());
        gen.itemModelOutput.accept(item, ItemModelUtils.composite(barrel, nib, fill));
    }

    private static void generateInkContainerModel(
            ItemModelGenerators gen,
            Item item,
            Identifier layer0,
            Identifier layer1,
            int tintIndex) {
        List<ItemTintSource> tints = new ArrayList<>();
        if (tintIndex == 1) {
            tints.add(new Constant(-1));
        }
        tints.add(new InkContainer());

        Identifier model = gen.generateLayeredItem(item, new Material(layer0), new Material(layer1));
        gen.itemModelOutput.accept(item, new CuboidItemModelWrapper.Unbaked(
                model,
                Optional.empty(),
                tints));
    }

    public static String getName() {
        return PigeonChatCommon.identifier("models").toString();
    }
}
