package com.ghifari160.pigeonchat;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.ghifari160.config.ConfigSpec;
import com.ghifari160.config.ConfigValue;
import com.ghifari160.config.platform.Services;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;

public final class PigeonChatConfig {
    public static final Common COMMON = new Common();

    @Nullable
    public static CommentedFileConfig openFile;

    private PigeonChatConfig() {}

    public static synchronized void load() {
        if (openFile != null) {
            openFile.close();
        }

        Path path = resolveConfigPath();

        openFile = CommentedFileConfig.builder(path)
                .preserveInsertionOrder()
                .build();
        assert openFile != null;

        if (path.toFile().exists()) {
            openFile.load();
        }

        boolean correct = COMMON.spec.correctAll(openFile);
        COMMON.spec.bind(openFile);

        if (correct || !path.toFile().exists()) {
            save();
        }
    }

    public static synchronized void save() {
        if (openFile == null) {
            throw new IllegalStateException("save() called before load().");
        }
        COMMON.spec.applyComments(openFile);
        openFile.save();
    }

    private static Path resolveConfigPath() {
        Path dir = Services.CONFIG.isDedicatedServer() ?
                Services.CONFIG.getDefaultConfigDir() :
                Services.CONFIG.getConfigDir();
        return dir.resolve(Constants.MOD_ID + ".toml");
    }

    public static final class Common {
        public final ConfigSpec spec;

        public final ConfigValue<Integer> penFill;
        public final ConfigValue<Integer> quillFill;

        public final ConfigValue<Integer> inkBottleFill;
        public final ConfigValue<Integer> inkBottleFillFromDye;

        public Common() {
            ConfigSpec.Builder b = new ConfigSpec.Builder();

            b.comment("Pen configuration").push("pen");
            penFill = b.comment("Maximum fill")
                    .defineInRange("max_fill", 500, 0, Integer.MAX_VALUE);
            b.pop();

            b.comment("Quill configuration").push("quill");
            quillFill = b.comment("Maximum fill")
                    .defineInRange("max_fill", 60, 0, Integer.MAX_VALUE);
            b.pop();

            b.comment("Ink Bottle Configuration").push("ink_bottle");
            inkBottleFill = b.comment("Maximum fill")
                    .defineInRange("max_fill", 2000, 0, Integer.MAX_VALUE);
            inkBottleFillFromDye = b.comment("Number of Dyes to combine with a water bottle for max fill")
                    .defineInRange("dye_refill", 2, 0, Integer.MAX_VALUE);
            b.pop();

            spec = b.build();
        }
    }
}
