package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Nova — Elytra (Legendary, Max 1)
 * While gliding at max speed in a straight line for 3s, triggers a force-nova on next landing,
 * launching all nearby entities away violently.
 */
@SuppressWarnings("deprecation")
public class NovaEnchant extends VortexEnchant {

    public NovaEnchant() {
        super("nova", "Nova", "elytra");
    }

    @Override
    public String getTier() { return "LEGENDARY"; }

    @Override
    public int getMaxLevel() { return 1; }

    @Override
    public String getDescription(int level) {
        return "§dGlide§7 at max speed for §e3s§7 to charge a §dNova§7 — an explosive burst upon landing.";
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (player.isGliding()) {
            double speed = player.getVelocity().length();
            if (speed >= cfgd("min_speed", 1.0)) {
                long chargeStart = plugin.getPlayerDataManager().getLong(player.getUniqueId(), "nova_charge_start", -1L);
                if (chargeStart < 0) {
                    plugin.getPlayerDataManager().setLong(player.getUniqueId(), "nova_charge_start", System.currentTimeMillis());
                } else {
                    long charged = System.currentTimeMillis() - chargeStart;
                    if (charged >= cfgi("charge_ms", 3000)) {
                        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "nova_ready", 1);
                        player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 4, 0.3, 0.3, 0.3, 0.02);
                    }
                }
            } else {
                plugin.getPlayerDataManager().setLong(player.getUniqueId(), "nova_charge_start", -1L);
            }
        } else if (player.isOnGround()) {
            int ready = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "nova_ready", 0);
            if (ready == 1) {
                plugin.getPlayerDataManager().setInt(player.getUniqueId(), "nova_ready", 0);
                plugin.getPlayerDataManager().setLong(player.getUniqueId(), "nova_charge_start", -1L);
                triggerNova(player);
            }
        }
    }

    private void triggerNova(Player player) {
        double radius = cfgd("radius", 8.0);
        double force = cfgd("force", 2.5);
        player.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, player.getLocation(), 3);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.2f, 0.6f);
        for (Entity e : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)) {
            if (e == player || !(e instanceof LivingEntity le)) continue;
            Vector dir = le.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            dir.setY(0.5);
            le.setVelocity(dir.multiply(force));
            le.damage(8.0, player);
        }
    }
}
