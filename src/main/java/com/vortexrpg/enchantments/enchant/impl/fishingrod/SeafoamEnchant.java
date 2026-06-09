package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.List;

/** Seafoam: Casting rod creates a bubble particle burst at the bobber. Nearby fish take note. */
public class SeafoamEnchant extends VortexEnchant {

    public SeafoamEnchant() { super("seafoam", "Seafoam", EnchantRarity.RARE, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.FISHING) return;
        Location bobber = event.getHook().getLocation();
        if (bobber.getBlock().getType() != Material.WATER) return;
        int particles = cfgi("particles", 15 + level * 5);
        bobber.getWorld().spawnParticle(Particle.BUBBLE_COLUMN_UP, bobber, particles, 1.0, 0.5, 1.0, 0.05);
        // Grant bonus XP on next catch from this cast
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "seafoam_active", level);
    }

    @Override public String getDescription() { return "Bubbles at your bobber attract fish."; }
    @Override public String getDescription(int level) {
        return "§7Casting creates §bbubbles§7 at the bobber for extra fish attention."; }
}
