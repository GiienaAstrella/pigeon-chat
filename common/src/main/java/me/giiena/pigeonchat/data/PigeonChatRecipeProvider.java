package me.giiena.pigeonchat.data;

import me.giiena.pigeonchat.Constants;
import me.giiena.pigeonchat.PigeonChatCommon;
import me.giiena.pigeonchat.item.Items;
import me.giiena.pigeonchat.tag.ItemTags;
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
