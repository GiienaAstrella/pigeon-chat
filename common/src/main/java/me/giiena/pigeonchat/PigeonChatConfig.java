package me.giiena.pigeonchat;

import me.giiena.config.api.Config;
import me.giiena.config.api.ConfigRegistry;

public final class PigeonChatConfig {
    public static final class Key {
        public static final String PEN_FILL = "pen.fill";
        public static final String QUILL_FILL = "quill.fill";
        public static final String INK_BOTTLE_FILL = "ink_bottle.fill";
        public static final String INK_BOTTLE_DYE_REFILL = "ink_bottle.dye_refill";
        public static final String NAME_TAG_VIEWABLE = "name_tag.viewable";
        public static final String NAME_TAG_EDITABLE = "name_tag.editable";
        public static final String NAME_TAG_ANVIL_EDITABLE = "name_tag.anvil_editable";
        public static final String PIGEON_ALLOW_RETURN = "pigeon.allow_return";
        public static final String PIGEON_INVINCIBLE_DELIVERY = "pigeon.invincible_delivery";
    }

    public static final class Default {
        public static final int PEN_FILL = 500;
        public static final int QUILL_FILL = 60;
        public static final int INK_BOTTLE_FILL = 2000;
        public static final int INK_BOTTLE_DYE_REFILL = 2;
        public static final boolean NAME_TAG_VIEWABLE = true;
        public static final boolean NAME_TAG_EDITABLE = true;
        public static final boolean NAME_TAG_ANVIL_EDITABLE = false;
        public static final boolean PIGEON_ALLOW_RETURN = true;
        public static final boolean PIGEON_INVINCIBLE_DELIVERY = false;
    }

    public static Config COMMON;

    public static void init() {
        COMMON = ConfigRegistry.registerSingle(Constants.MOD_ID, Config.Type.COMMON);

        COMMON.section("pen").comment("Pen configuration").close();
        COMMON.section(Key.PEN_FILL).comment("Maximum fill").set(Default.PEN_FILL);

        COMMON.section("quill").comment("Quill configuration").close();
        COMMON.section(Key.QUILL_FILL).comment("Maximum fill").set(Default.QUILL_FILL);

        COMMON.section("ink_bottle").comment("Ink Bottle configuration").close();
        COMMON.section(Key.INK_BOTTLE_FILL).comment("Maximum fill").set(Default.INK_BOTTLE_FILL);
        COMMON.section(Key.INK_BOTTLE_DYE_REFILL)
                .comment("Number of Dyes to combine with a water bottle for max fill")
                .set(Default.INK_BOTTLE_DYE_REFILL);

        COMMON.section("name_tag").comment("Name Tag configuration").close();
        COMMON.section(Key.NAME_TAG_VIEWABLE).comment("Make Name Tags viewable")
                .set(Default.NAME_TAG_VIEWABLE);
        COMMON.section(Key.NAME_TAG_EDITABLE).comment("Make Name Tags editable with utensils")
                .set(Default.NAME_TAG_EDITABLE);
        COMMON.section(Key.NAME_TAG_ANVIL_EDITABLE).comment("Allow naming on Anvil")
                .set(Default.NAME_TAG_ANVIL_EDITABLE);

        COMMON.section("pigeon").comment("Pigeon configuration").close();
        COMMON.section(Key.PIGEON_ALLOW_RETURN)
                .comment("Allow Pigeons to make return delivery.\n" +
                        "If set to \"true\", Pigeons will follow the target post-delivery, and right-clicking Pigeons with a deliverable item begins a return delivery job bound to the original sender")
                .set(Default.PIGEON_ALLOW_RETURN);
        COMMON.section(Key.PIGEON_INVINCIBLE_DELIVERY)
                .comment("Pigeons are invincible during delivery")
                .set(Default.PIGEON_INVINCIBLE_DELIVERY);

        COMMON.load();
        COMMON.save();
    }
}
