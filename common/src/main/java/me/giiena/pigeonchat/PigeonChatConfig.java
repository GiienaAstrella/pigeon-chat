package me.giiena.pigeonchat;

import me.giiena.config.api.Config;
import me.giiena.config.api.ConfigRegistry;

public final class PigeonChatConfig {
    public static final class Common {
        private static final Config CONFIG = ConfigRegistry.registerSingle(Constants.MOD_ID,
                Config.Type.COMMON);

        private static final Config.Builder PEN =
                CONFIG.comment("Pen configuration").push("pen");
        public static final Config.Value<Integer> PEN_FILL =
                PEN.comment("Maximum fill").push("fill").define(500);

        private static final Config.Builder QUILL =
                CONFIG.comment("Quill configuration").push("quill");
        public static final Config.Value<Integer> QUILL_FILL =
                QUILL.comment("Maximum fill").push("fill").define(60);

        private static final Config.Builder INK_BOTTLE =
                CONFIG.comment("Ink Bottle configuration").push("ink_bottle");
        public static final Config.Value<Integer> INK_BOTTLE_FILL =
                INK_BOTTLE.comment("Maximum fill").push("fill").define(2000);
        public static final Config.Value<Integer> INK_BOTTLE_DYE_REFILL =
                INK_BOTTLE.comment("Amount of dye required to fill").push("dye_refill")
                        .define(2);

        private static final Config.Builder NAME_TAG =
                CONFIG.comment("Name Tag configuration").push("name_tag");
        public static final Config.Value<Boolean> NAME_TAG_VIEWABLE =
                NAME_TAG.comment("Make Name Tags viewable").push("viewable").define(true);
        public static final Config.Value<Boolean> NAME_TAG_EDITABLE =
                NAME_TAG.comment("Make Name Tags editable with utensils").push("editable")
                        .define(true);
        public static final Config.Value<Boolean> NAME_TAG_ANVIL_EDITABLE =
                NAME_TAG.comment("Allow naming on Anvil").push("anvil_editable")
                        .define(false);

        private static final Config.Builder PIGEON =
                CONFIG.comment("Pigeon configuration").push("pigeon");
        public static final Config.Value<Boolean> PIGEON_ALLOW_RETURN =
                PIGEON.push("allow_return").define(true);
        public static final Config.Value<Boolean> PIGEON_INVINCIBLE_DELIVERY =
                PIGEON.push("invincible_delivery").define(true);
    }

    public static void init() {
        Common.CONFIG.load();
    }
}
