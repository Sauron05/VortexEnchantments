package com.vortexrpg.enchantments.util;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Small compatibility shim for plugin metadata access.
 *
 * <p>Paper exposes {@code JavaPlugin#getPluginMeta()}, but older Bukkit-derived APIs may
 * not provide that method. We try the modern Paper accessor first and fall back to the
 * legacy {@code getDescription()} accessor so metadata reads stay compatible across the
 * supported Paper/Folia server range.
 */
public final class PluginCompat {

    private PluginCompat() {}

    /** Plugin version string, resilient to the absence of Paper's {@code getPluginMeta()}. */
    @SuppressWarnings("deprecation")
    public static String version(JavaPlugin plugin) {
        try {
            return plugin.getPluginMeta().getVersion();
        } catch (Throwable ignored) {
            return plugin.getDescription().getVersion();
        }
    }

    /** Plugin authors, resilient to the absence of Paper's {@code getPluginMeta()}. */
    @SuppressWarnings("deprecation")
    public static List<String> authors(JavaPlugin plugin) {
        try {
            return plugin.getPluginMeta().getAuthors();
        } catch (Throwable ignored) {
            return plugin.getDescription().getAuthors();
        }
    }
}
