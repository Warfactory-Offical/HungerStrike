package com.jaquadro.minecraft.hungerstrike.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A minimal reader/writer for the one flat TOML table this mod stores.
 *
 * <p>NeoForge gets this from {@code ModConfigSpec}, which is not available under Fabric. Rather
 * than bundle NightConfig for five scalars, the file is parsed line by line and rewritten from the
 * spec — the same regenerate-on-write behaviour {@code ModConfigSpec} has, and the same on-disk
 * shape, so a {@code hungerstrike-common.toml} written by any Forge or NeoForge build loads here
 * unchanged.
 *
 * <p>Only what the format actually needs is supported: {@code [Section]} headers, {@code key =
 * value} pairs, {@code #} comment lines, basic strings, booleans and numbers. Unknown keys and
 * sections are read and discarded; the file is rewritten from the declared entries.
 */
public final class TomlConfigFile {
    /** Values keyed by {@code Section.key}, exactly as the spec paths are written. */
    private final Map<String, String> values = new LinkedHashMap<>();

    private TomlConfigFile() {}

    public static TomlConfigFile read(Path path) throws IOException {
        TomlConfigFile config = new TomlConfigFile();
        if (!Files.isRegularFile(path)) {
            return config;
        }

        String section = "";
        for (String rawLine : Files.readAllLines(path, StandardCharsets.UTF_8)) {
            String line = rawLine.strip();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            if (line.startsWith("[") && line.endsWith("]")) {
                section = line.substring(1, line.length() - 1).strip();
                continue;
            }

            int split = line.indexOf('=');
            if (split <= 0) {
                continue;
            }

            String key = line.substring(0, split).strip();
            String value = stripValue(line.substring(split + 1));
            config.values.put(section.isEmpty() ? key : section + "." + key, value);
        }
        return config;
    }

    /**
     * Trims a raw value: unwraps a quoted string, or drops a trailing {@code #} comment from a bare
     * one. Nothing this mod writes needs escape handling, and a value that fails to parse falls back
     * to its default at the call site.
     */
    private static String stripValue(String raw) {
        String value = raw.strip();
        if (value.length() >= 2 && value.charAt(0) == '"' && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        int comment = value.indexOf('#');
        return comment >= 0 ? value.substring(0, comment).strip() : value;
    }

    public String getString(String path, String fallback) {
        String value = this.values.get(path);
        return value == null || value.isEmpty() ? fallback : value;
    }

    public <E extends Enum<E>> E getEnum(String path, Class<E> type, E fallback) {
        String value = this.values.get(path);
        if (value == null) {
            return fallback;
        }
        try {
            return Enum.valueOf(type, value.strip().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    public boolean getBoolean(String path, boolean fallback) {
        String value = this.values.get(path);
        if (value == null) {
            return fallback;
        }
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        return "false".equalsIgnoreCase(value) ? false : fallback;
    }

    public int getInt(String path, int fallback, int min, int max) {
        String value = this.values.get(path);
        if (value == null) {
            return fallback;
        }
        try {
            return clamp(Integer.parseInt(value), min, max);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public double getDouble(String path, double fallback, double min, double max) {
        String value = this.values.get(path);
        if (value == null) {
            return fallback;
        }
        try {
            double parsed = Double.parseDouble(value);
            return Double.isFinite(parsed) ? Math.max(min, Math.min(max, parsed)) : fallback;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /** One entry of the file being written: a value plus the comment lines above it. */
    public record Entry(String section, String key, String value, List<String> comments) {
        public static Entry of(String section, String key, String value, String... comments) {
            return new Entry(section, key, value, List.of(comments));
        }

        public static String quote(String value) {
            return '"' + value + '"';
        }
    }

    /**
     * Writes the given entries, grouped by section in the order supplied. The layout mirrors what
     * NightConfig produces for NeoForge: a blank line before each table header, and tab-indented
     * comments above each key.
     */
    public static void write(Path path, List<Entry> entries) throws IOException {
        List<String> lines = new ArrayList<>();
        String currentSection = null;

        for (Entry entry : entries) {
            if (!entry.section().equals(currentSection)) {
                currentSection = entry.section();
                if (!lines.isEmpty()) {
                    lines.add("");
                }
                lines.add("[" + currentSection + "]");
            }
            for (String comment : entry.comments()) {
                lines.add("\t#" + comment);
            }
            lines.add("\t" + entry.key() + " = " + entry.value());
        }

        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }
}
