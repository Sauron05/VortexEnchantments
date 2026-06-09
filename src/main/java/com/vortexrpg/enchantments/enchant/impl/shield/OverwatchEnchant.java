package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

/**
 * Overwatch — Shield (Epic, Max 3)
 * Every 30/25/20 cumulative blocks causes an area shockwave knocking back nearby mobs 3 blocks.
 */
public class OverwatchEnchant extends VortexEnchant {

    public OverwatchEnchant() {
        super("overwatch", "Overwatch", "shield");
    }

    @Override
    public String getTier() { return "EPIC"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] thresholds = {30, 25, 20};
        return "Every §e" + thresholds[level - 1] + "§7 cumulative blocks triggers a shockwave knocking back nearby enemies.";
    }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!player.isBlocking()) return;

        int[] thresholds = {30, 25, 20};
        int threshold = cfgi("blocks_threshold", thresholds[level - 1]);
        int count = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "overwatch_blocks", 0) + 1;

        if (count >= threshold) {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "overwatch_blocks", 0);
            triggerShockwave(player, level);
        } else {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "overwatch_blocks", count);
        }
    }

    private void triggerShockwave(Player player, int level) {
        double radius = cfgd("knockback_radius", 5.0);
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation().add(0, 1, 0), 20, 1, 0.5, 1, 0);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.7f, 1.5f);

        for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)) {
            if (entity == player) continue;
            if (!(entity instanceof LivingEntity le)) continue;
            Vector dir = le.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            dir.setY(0.4);
            le.setVelocity(dir.multiply(1.2));
        }
    }
}
