package com.vortexrpg.enchantments.system;

import com.vortexrpg.enchantments.VortexEnchantments;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Multi-world support — allows disabling VortexEnchantments effects in specific worlds.
 */
public class WorldManager {

    private final VortexEnchantments plugin;
    private final Set<String> disabledWorlds = new HashSet<>();

    public WorldManager(VortexEnchantments plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        disabledWorlds.clear();
        List<String> worlds = plugin.getConfig().getStringList("multi-world.disabled-worlds");
        for (String world : worlds) {
            disabledWorlds.add(world.toLowerCase());
        }
    }

    /**
     * Check if VortexEnchantments effects are enabled in the given world.
     *
     * @param worldName The world name to check
     * @return true if enchant effects should fire in this world
     */
    public boolean isWorldEnabled(String worldName) {
        if (!plugin.getConfig().getBoolean("multi-world.enabled", false)) return true;
        return !disabledWorlds.contains(worldName.toLowerCase());
    }

    public Set<String> getDisabledWorlds() {
        return Set.copyOf(disabledWorlds);
    }
}
