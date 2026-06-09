package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;

/**
 * Whirlpool — Fishing Rod (Epic, Max 3)
 * After catching 3/2/1 fish in quick succession, triggers a whirlpool effect:
 * pulls all nearby entities toward the bobber location and damages them.
 */
public class WhirlpoolEnchant extends VortexEnchant {

    public WhirlpoolEnchant() {
        super("whirlpool", "Whirlpool", "fishingrod");
    }

    @Override
    public String getTier() { return "EPIC"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] req = {3, 2, 1};
        return "Catching §e" + req[level - 1] + "§7 fish in a row triggers a §bwhirlpool§7 that pulls and damages nearby mobs.";
    }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        int count = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "whirlpool_count", 0) + 1;
        int[] required = {3, 2, 1};
        int req = cfgi("required_catches", required[level - 1]);
        if (count >= req) {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "whirlpool_count", 0);
            triggerWhirlpool(player, event.getHook().getLocation(), level);
        } else {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "whirlpool_count", count);
        }
    }

    private void triggerWhirlpool(Player player, org.bukkit.Location center, int level) {
        double radius = cfgd("radius", 5.0);
        double dmg = cfgd("damage", 4.0 * level);
        center.getWorld().spawnParticle(org.bukkit.Particle.SPLASH, center, 40, 1, 0.5, 1, 0.1);
        for (org.bukkit.entity.Entity e : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (e == player) continue;
            if (!(e instanceof org.bukkit.entity.LivingEntity le)) continue;
            org.bukkit.util.Vector dir = center.toVector().subtract(e.getLocation().toVector()).normalize().multiply(0.8);
            dir.setY(0.2);
            le.setVelocity(dir);
            le.damage(dmg, player);
        }
    }
}
