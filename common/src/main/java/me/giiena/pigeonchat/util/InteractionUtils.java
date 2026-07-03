package me.giiena.pigeonchat.util;

import net.minecraft.world.InteractionHand;

public class InteractionUtils {
    public static InteractionHand otherHand(InteractionHand hand) {
        return (hand == InteractionHand.MAIN_HAND) ?
                InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }
}
