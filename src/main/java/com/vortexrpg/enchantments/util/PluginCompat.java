package com.vortexrpg.enchantments.util;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Small compatibility shim for plugin metadata access.
 *
 * <p>Paper exposes {@code JavaPlugin#getPluginMeta()}, but that method is not part of the
 * stock Bukkit/Spigot API and may be missing on some Bukkit-on-Fabric implementations
 * (e.g. Cardboard). We try the modern Paper accessor first and fall back to the legacy
 * {@code getDescription()} accessor so the plugin boots cleanly on Paper 1.21.11, Paper
 * 26.1.2, Folia, and Fabric-via-Cardboard alike.
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
