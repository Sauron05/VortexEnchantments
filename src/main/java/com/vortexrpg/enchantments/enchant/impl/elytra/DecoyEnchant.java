package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

/**
 * Decoy — Elytra (Epic, Max 1)
 * While gliding, periodically sheds an invisible armor-stand decoy at last position to confuse mobs.
 */
@SuppressWarnings("deprecation")
public class DecoyEnchant extends VortexEnchant {

    public DecoyEnchant() {
        super("decoy", "Decoy", "elytra");
    }

    @Override
    public String getTier() { return "EPIC"; }

    @Override
    public int getMaxLevel() { return 1; }

    @Override
    public String getDescription(int level) {
        return "While gliding, drops a §7decoy§7 every 5s that briefly distracts nearby mobs.";
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (!player.isGliding()) return;
        long now = System.currentTimeMillis();
        long last = plugin.getPlayerDataManager().getLong(player.getUniqueId(), "decoy_last", 0L);
        int intervalMs = cfgi("interval_ms", 5000);
        if (now - last < intervalMs) return;
        plugin.getPlayerDataManager().setLong(player.getUniqueId(), "decoy_last", now);

        Location loc = player.getLocation();
        player.getWorld().spawnParticle(Particle.POOF, loc, 8, 0.3, 0.3, 0.3, 0.02);

        ArmorStand stand = player.getWorld().spawn(loc, ArmorStand.class, s -> {
            s.setVisible(false);
            s.setGravity(false);
            s.setSmall(true);
            s.setMarker(true);
            s.setCustomName("§7[Decoy]");
            s.setCustomNameVisible(false);
        });

        int lifetickMs = cfgi("decoy_lifetime_ms", 3000);
        plugin.getServer().getScheduler().runTaskLater(plugin, stand::remove, lifetickMs / 50L);
    }
}
