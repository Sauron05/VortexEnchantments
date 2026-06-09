package com.vortexrpg.enchantments.hook;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.PluginCompat;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * PlaceholderAPI expansion for VortexEnchantments.
 * 
 * Placeholders:
 *   %vortex_total_enchants%     — Total registered enchantments
 *   %vortex_held_enchant_count% — Number of Vortex enchants on held item
 *   %vortex_souls%              — Player's souls balance
 *   %vortex_version%            — Plugin version
 */
public class VortexPlaceholders extends PlaceholderExpansion {

    private final VortexEnchantments plugin;

    public VortexPlaceholders(VortexEnchantments plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() { return "vortex"; }

    @Override
    public @NotNull String getAuthor() { return PluginCompat.authors(plugin).isEmpty() ? "VortexRPG" : PluginCompat.authors(plugin).get(0); }

    @Override
    public @NotNull String getVersion() { return PluginCompat.version(plugin); }

    @Override
    public boolean persist() { return true; }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        // Global placeholders (no player needed)
        if (params.equalsIgnoreCase("total_enchants")) {
            return String.valueOf(plugin.getEnchantManager().getEnchantCount());
        }
        if (params.equalsIgnoreCase("version")) {
            return PluginCompat.version(plugin);
        }

        // Player-specific placeholders
        if (player == null) return "";

        if (params.equalsIgnoreCase("held_enchant_count")) {
            ItemStack held = player.getInventory().getItemInMainHand();
            Map<VortexEnchant, Integer> enchants = plugin.getEnchantManager().getEnchants(held);
            return String.valueOf(enchants.size());
        }

        if (params.equalsIgnoreCase("souls")) {
            return String.valueOf(plugin.getSoulsManager().getSouls(player));
        }

        return null;
    }
}
