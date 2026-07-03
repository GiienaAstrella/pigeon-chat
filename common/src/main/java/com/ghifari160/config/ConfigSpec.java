package com.ghifari160.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class ConfigSpec {
    private final List<ConfigValue.Definition<?>> definitions;
    private final Map<String, String> comments;
    private final List<ConfigValue<?>> handles;

    private CommentedConfig bound;

    private ConfigSpec(
            List<ConfigValue.Definition<?>> definitions,
            Map<String, String> comments,
            List<ConfigValue<?>> handles) {
        this.definitions = Collections.unmodifiableList(definitions);
        this.comments = Collections.unmodifiableMap(comments);
        this.handles = Collections.unmodifiableList(handles);
    }

    public void bind(CommentedConfig config) {
        this.bound = config;
        for (ConfigValue<?> handle : handles) {
            handle.bind(config);
        }
    }

    public boolean correctAll(CommentedConfig config) {
        boolean changed = false;
        for (ConfigValue.Definition<?> def : definitions) {
            Object current = config.get(def.getPath());
            if (!def.isValid(current)) {
                config.set(def.getPath(), def.getDefaultValue());
                changed = true;
            }
        }
        return changed;
    }

    public void applyComments(CommentedFileConfig file) {
        comments.forEach((path, comment) -> {
            if (!file.contains(path)) {
                file.set(path, CommentedConfig.inMemory());
            }
            file.setComment(path, comment);
        });
        for (ConfigValue.Definition<?> def : definitions) {
            if (def.getComment() != null) {
                file.setComment(def.getPath(), " " + def.getComment());
            }
        }
    }

    public List<ConfigValue.Definition<?>> getDefinitions() {
        return this.definitions;
    }

    public List<ConfigValue<?>> getHandles() {
        return this.handles;
    }

    public static final class Builder {
        private final List<ConfigValue.Definition<?>> definitions = new ArrayList<>();
        private final Map<String, String> comments = new LinkedHashMap<>();
        private final List<ConfigValue<?>> handles = new ArrayList<>();

        private final Deque<String> stack = new ArrayDeque<>();
        @Nullable
        private String pendingComment = null;

        public Builder comment(@NonNull String comment) {
            this.pendingComment = comment;
            return this;
        }

        public void push(String name) {
            stack.push(name);
            if (pendingComment != null) {
                comments.put(currentSection(), " " + pendingComment);
                pendingComment = null;
            }
        }

        public void pop() {
            if (stack.isEmpty()) {
                throw new IllegalStateException("pop() with no matching push()");
            }
            stack.pop();
        }

        public <T> ConfigValue<T> define(String key, T defaultValue) {
            return define(key, defaultValue, null);
        }

        public <T> ConfigValue<T> define(String key, T defaultValue, @Nullable Predicate<T> validator) {
            ConfigValue.Definition<T> def = new ConfigValue.Definition<>(
                    fullPath(key),
                    defaultValue,
                    validator,
                    consumePendingComment());
            return this.register(def);
        }

        public ConfigValue<Integer> defineInRange(String key, int defaultValue, int min, int max) {
            ConfigValue.Definition<Integer> def = new ConfigValue.Definition<>(
                    fullPath(key),
                    defaultValue,
                    v -> v >= min && v <= max,
                    consumePendingComment());
            return register(def);
        }

        public ConfigValue<Double> defineInRange(String key, double defaultValue, double min, double max) {
            ConfigValue.Definition<Double> def = new ConfigValue.Definition<>(
                    fullPath(key),
                    defaultValue,
                    v -> v >= min && v <= max,
                    consumePendingComment());
            return register(def);
        }

        public ConfigValue<Long> defineInRange(String key, long defaultValue, long min, long max) {
            ConfigValue.Definition<Long> def = new ConfigValue.Definition<>(
                    fullPath(key),
                    defaultValue,
                    v -> v >= min && v <= max,
                    consumePendingComment());
            return register(def);
        }

        public <E extends Enum<E>> ConfigValue<E> defineEnum(String key, E defaultValue) {
            EnumSet<E> validator = EnumSet.allOf(defaultValue.getDeclaringClass());
            ConfigValue.Definition<E> def = new ConfigValue.Definition<>(
                    fullPath(key),
                    defaultValue,
                    validator::contains,
                    consumePendingComment());
            return register(def);
        }

        public ConfigSpec build() {
            if (!stack.isEmpty()) {
                throw new IllegalStateException("Unclosed sections: " + stack);
            }
            return new ConfigSpec(
                    new ArrayList<>(definitions),
                    new LinkedHashMap<>(comments),
                    new ArrayList<>(handles));
        }

        private <T> ConfigValue<T> register(ConfigValue.Definition<T> def) {
            definitions.add(def);
            ConfigValue<T> handle = new ConfigValue<>(def);
            handles.add(handle);
            return handle;
        }

        private String currentSection() {
            List<String> parts = new ArrayList<>(stack);
            Collections.reverse(parts);
            return String.join(".", parts);
        }

        private String fullPath(String key) {
            String section = currentSection();
            return section.isEmpty() ? key : section + "." + key;
        }

        private String consumePendingComment() {
            String comment = this.pendingComment;
            this.pendingComment = null;
            return comment;
        }
    }
}
