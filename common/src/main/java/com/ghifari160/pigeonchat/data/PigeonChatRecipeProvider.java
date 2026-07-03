package com.ghifari160.pigeonchat.data;

import com.ghifari160.pigeonchat.Constants;
import com.ghifari160.pigeonchat.PigeonChatCommon;
import com.ghifari160.pigeonchat.item.Items;
import com.ghifari160.pigeonchat.tag.ItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.crafting.Ingredient;

public class PigeonChatRecipeProvider extends RecipeProvider {
    protected PigeonChatRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    public void buildRecipes() {
        this.shaped(RecipeCategory.TOOLS, Items.PEN)
                .define('N', ItemTags.NIB_MATERIALS)
                .define('B', Ingredient.of(net.minecraft.world.item.Items.IRON_INGOT))
                .pattern(" B ")
                .pattern("N  ")
                .group(PigeonChatCommon.identifier("pen").toString())
                .unlockedBy("has_iron_ingot", has(net.minecraft.world.item.Items.IRON_INGOT))
                .save(this.output);
    }

    public static String name() {
        return Constants.MOD_ID;
    }
}
