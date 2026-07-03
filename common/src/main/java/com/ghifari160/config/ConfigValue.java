package com.ghifari160.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

public final class ConfigValue<T> {
    private final Definition<T> definition;
    @Nullable
    private CommentedConfig backing;

    ConfigValue(Definition<T> definition) {
        this.definition = definition;
    }

    void bind(@NonNull CommentedConfig config) {
        this.backing = config;
    }

    public T get() {
        if (backing == null) return definition.getDefaultValue();
        T value = backing.getOrElse(definition.getPath(), definition.getDefaultValue());
        return definition.isValid(value) ? value : definition.getDefaultValue();
    }

    public void set(T value) {
        if (backing == null) {
            throw new IllegalStateException("ConfigValue " + definition.getPath()
                    + " is not yet bound to a config.");
        } else if (!definition.isValid(value)) {
            throw new IllegalArgumentException("Value " + value + " is invalid for "
                    + definition.getPath());
        }
        backing.set(definition.getPath(), value);
    }

    public Definition<T> getDefinition() {
        return this.definition;
    }

    public static final class Definition<T> {
        final String path;
        final T defaultValue;
        @Nullable
        final Predicate<T> validator;
        @Nullable
        final String comment;

        public Definition(String path, T defaultValue,
                           @Nullable Predicate<T> validator, @Nullable String comment) {
            this.path = path;
            this.defaultValue = defaultValue;
            this.validator = validator;
            this.comment = comment;
        }

        public String getPath() {
            return this.path;
        }

        public T getDefaultValue() {
            return this.defaultValue;
        }

        @Nullable
        public String getComment() {
            return this.comment;
        }

        public boolean isValid(@Nullable Object value) {
            if (value == null) return false;
            try {
                @SuppressWarnings("unchecked")
                T cast = (T) value;
                return validator == null || validator.test(cast);
            } catch (ClassCastException e) {
                return false;
            }
        }
    }
}
