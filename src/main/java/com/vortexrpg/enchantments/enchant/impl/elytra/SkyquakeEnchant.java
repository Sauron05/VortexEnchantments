package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/** Skyquake: Massive AoE damage on high-speed landing. */
public class SkyquakeEnchant extends VortexEnchant {

    public SkyquakeEnchant() { super("skyquake", "Skyquake", EnchantRarity.LEGENDARY, 1, List.of(ItemTarget.ELYTRA)); }

    @SuppressWarnings("deprecation")
    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        boolean wasGliding = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "sq_gl", 0) == 1;
        boolean isGliding = player.isGliding();
        if (isGliding) {
            plugin.getPlayerDataManager().setDouble(player.getUniqueId(), "sq_speed", player.getVelocity().length());
        }
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "sq_gl", isGliding ? 1 : 0);
        if (wasGliding && !isGliding && player.isOnGround()) {
            double speed = plugin.getPlayerDataManager().getDouble(player.getUniqueId(), "sq_speed", 0);
            if (speed < cfgd("min_speed", 1.5)) return;
            if (isOnCooldown(player)) return;
            setCooldownSeconds(player, cfgi("cooldown", 30));
            double radius = cfgd("radius", 12.0);
            double damage = cfgd("damage", 10.0);
            double launch = cfgd("launch", 1.5);
            player.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, player.getLocation(), 5, 2, 1, 2, 0);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.5f);
            for (Entity e : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)) {
                if (e == player || !(e instanceof LivingEntity le)) continue;
                le.damage(damage, player);
                Vector push = le.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                push.setY(launch);
                le.setVelocity(push.multiply(1.5));
            }
        }
    }

    @Override public String getDescription() { return "Catastrophic shockwave on extreme-speed landing."; }
    @Override public String getDescription(int level) {
        return "§7Land at max speed: §c10§7 damage + launch in §e12§7-block radius (§e30s§7 cd)."; }
}
