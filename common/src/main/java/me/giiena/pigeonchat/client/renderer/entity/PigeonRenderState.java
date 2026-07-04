package me.giiena.pigeonchat.client.renderer.entity;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemStack;

public class PigeonRenderState extends LivingEntityRenderState {
    public ItemStack carrying = ItemStack.EMPTY;
    public final ItemStackRenderState carryingState = new ItemStackRenderState();
}
