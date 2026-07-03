package com.ghifari160.pigeonchat;

import com.ghifari160.config.api.Config;
import com.ghifari160.config.api.ConfigRegistry;

public final class PigeonChatConfig {
    public static final class Key {
        public static final String PEN_FILL = "pen.fill";
        public static final String QUILL_FILL = "quill.fill";
        public static final String INK_BOTTLE_FILL = "ink_bottle.fill";
        public static final String INK_BOTTLE_DYE_REFILL = "ink_bottle.dye_refill";
        public static final String NAME_TAG_VIEWABLE = "name_tag.viewable";
        public static final String NAME_TAG_EDITABLE = "name_tag.editable";
    }

    public static final class Default {
        public static final int PEN_FILL = 500;
        public static final int QUILL_FILL = 60;
        public static final int INK_BOTTLE_FILL = 2000;
        public static final int INK_BOTTLE_DYE_REFILL = 2;
        public static final boolean NAME_TAG_VIEWABLE = true;
        public static final boolean NAME_TAG_EDITABLE = true;
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

        COMMON.load();
        COMMON.save();
    }
}
