package com.ghifari160.pigeonchat.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.IdentityHashMap;
import java.util.Map;

public class ItemScreen {
    private static final Map<Item, Factory> REGISTRY = new IdentityHashMap<>();

    public static void init() {}

    public static void register(Item item, Factory factory) {
        REGISTRY.put(item, factory);
    }

    public static boolean open(Player owner, ItemStack stack, InteractionHand hand) {
        Factory factory = REGISTRY.get(stack.getItem());
        if (factory == null) return false;
        Minecraft.getInstance().gui.setScreen(factory.apply(owner, stack, hand));
        return true;
    }

    public interface Factory {
        Screen apply(Player owner, ItemStack stack, InteractionHand hand);
    }
}
