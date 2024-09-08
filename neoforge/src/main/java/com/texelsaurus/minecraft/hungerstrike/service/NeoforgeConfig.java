package com.texelsaurus.minecraft.hungerstrike.service;

import com.texelsaurus.minecraft.hungerstrike.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class NeoforgeConfig implements CommonConfig
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static ModConfigSpec spec;

    public static void init() {
        ModConfig.init();
        spec = BUILDER.build();
    }

    @Override
    public <T> ConfigEntry<T> define (String name, T defaultValue) {
        return new ForgeConfigEntry<T>(BUILDER).name(name).defaultValue(defaultValue);
    }

    @Override
    public <T extends Comparable<? super T>> ConfigEntry<T> defineInRange (String name, T defaultValue, T min, T max, Class<T> clazz) {
        return new ForgeConfigEntryRange<T>(BUILDER, clazz).name(name).defaultValue(defaultValue).range(min, max);
    }

    @Override
    public <T extends Enum<T>> ConfigEntry<T> defineEnum (String name, T defaultValue) {
        return new ForgeConfigEntryEnum<T>(BUILDER).name(name).defaultValue(defaultValue);
    }

    @Override
    public void pushGroup (String name) {
        BUILDER.push(name);
    }

    @Override
    public void popGroup () {
        BUILDER.pop();
    }

    public class ForgeConfigEntry<T> extends ConfigEntry<T>
    {
        ModConfigSpec.Builder builder;
        ModConfigSpec.ConfigValue<T> value;


        public ForgeConfigEntry(ModConfigSpec.Builder builder) {
            this.builder = builder;
        }

        @Override
        public ConfigEntry<T> build () {
            if (comment != null)
                builder.comment(comment);

            value = define();
            return this;
        }

        @Override
        public T get () {
            if (value == null)
                return defaultValue;

            return value.get();
        }

        @Override
        public void set (T t) {
            if (value != null)
                value.set(t);
        }

        protected ModConfigSpec.ConfigValue<T> define() {
            return builder.define(name, defaultValue);
        }
    }

    public class ForgeConfigEntryRange<T extends Comparable<? super T>> extends ForgeConfigEntry<T>
    {
        Class<T> clazz;

        public ForgeConfigEntryRange (ModConfigSpec.Builder builder, Class<T> clazz) {
            super(builder);
            this.clazz = clazz;
        }

        @Override
        protected ModConfigSpec.ConfigValue<T> define () {
            return builder.defineInRange(name, defaultValue, rangeMin, rangeMax, clazz);
        }
    }

    public class ForgeConfigEntryEnum<T extends Enum<T>> extends ForgeConfigEntry<T>
    {
        public ForgeConfigEntryEnum (ModConfigSpec.Builder builder) {
            super(builder);
        }

        @Override
        protected ModConfigSpec.ConfigValue<T> define () {
            return builder.defineEnum(name, defaultValue);
        }
    }
}
