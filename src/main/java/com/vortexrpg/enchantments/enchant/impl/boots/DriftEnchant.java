package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.List;

/** Drift: Using elytra? No — reduce air resistance; in water glide horizontally without sinking. Reuse as: drifting on ice makes you faster. */
public class DriftEnchant extends VortexEnchant {
    public DriftEnchant() { super("drift", "Drift", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onMove(PlayerMoveEvent event, Player player, int level) {
        if (!isEnabled() || !event.hasChangedBlock()) return;
        var below = player.getLocation().subtract(0, 1, 0).getBlock().getType();
        if (below == org.bukkit.Material.ICE || below == org.bukkit.Material.PACKED_ICE || below == org.bukkit.Material.BLUE_ICE) {
            // Amplify horizontal movement on ice
            Vector vel = player.getVelocity();
            player.setVelocity(vel.multiply(1.0 + 0.05 * level));
        }
    }

    @Override public String getDescription() { return "Glide faster on ice."; }
    @Override public String getDescription(int level) {
        return "§7On ice: §a+" + (5*level) + "§a%§7 horizontal speed."; }
}
