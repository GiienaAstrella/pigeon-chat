package me.giiena.pigeonchat.item.food;

import net.minecraft.world.food.FoodProperties;

public class Foods {
    public static final FoodProperties PIGEON = new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.3f)
            .build();
    public static final FoodProperties COOKED_PIGEON = new FoodProperties.Builder()
            .nutrition(6)
            .saturationModifier(0.6f)
            .build();
}
