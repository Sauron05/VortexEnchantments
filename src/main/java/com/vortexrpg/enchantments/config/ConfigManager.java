package com.vortexrpg.enchantments.config;

import com.vortexrpg.enchantments.VortexEnchantments;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Handles loading and reloading of all plugin configuration files.
 */
public class ConfigManager {

    private final VortexEnchantments plugin;
    private FileConfiguration mainConfig;
    private FileConfiguration messagesConfig;
    private FileConfiguration customItemsConfig;

    public ConfigManager(VortexEnchantments plugin) {
        this.plugin = plugin;
        loadAll();
    }

    public void loadAll() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        mainConfig = plugin.getConfig();

        messagesConfig = loadOrCreate("messages.yml");
        customItemsConfig = loadOrCreate("custom_items.yml");
    }

    private FileConfiguration loadOrCreate(String name) {
        File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            plugin.saveResource(name, false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public void saveMessages() {
        try {
            ((YamlConfiguration) messagesConfig).save(new File(plugin.getDataFolder(), "messages.yml"));
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save messages.yml", e);
        }
    }

    public FileConfiguration getMainConfig() { return mainConfig; }
    public FileConfiguration getMessagesConfig() { return messagesConfig; }
    public FileConfiguration getCustomItemsConfig() { return customItemsConfig; }

    public String getMessage(String key) {
        String msg = messagesConfig.getString(key, "&cMissing message: " + key);
        return colorize(msg);
    }

    public String getMessage(String key, Object... replacements) {
        String msg = getMessage(key);
        for (int i = 0; i < replacements.length - 1; i += 2) {
            msg = msg.replace(String.valueOf(replacements[i]), String.valueOf(replacements[i + 1]));
        }
        return msg;
    }

    private String colorize(String s) {
        return s == null ? "" : s.replace("&", "§");
    }

    public boolean isDebugMode() {
        return mainConfig.getBoolean("debug", false);
    }

    public boolean isAnvilEnabled() {
        return mainConfig.getBoolean("anvil.enabled", true);
    }

    public boolean isEnchantTableEnabled() {
        return mainConfig.getBoolean("enchant_table.enabled", true);
    }

    public boolean isVillagerEnabled() {
        return mainConfig.getBoolean("villager_trades.enabled", true);
    }
}
