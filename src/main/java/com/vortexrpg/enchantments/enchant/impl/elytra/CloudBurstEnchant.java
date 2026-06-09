package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** CloudBurst: Sneak while gliding to drop a slow cloud below. */
public class CloudBurstEnchant extends VortexEnchant {

    public CloudBurstEnchant() { super("cloud_burst", "Cloud Burst", EnchantRarity.RARE, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled() || !player.isGliding() || !event.isSneaking()) return;
        if (isOnCooldown(player)) return;
        setCooldownSeconds(player, cfgi("cooldown", 8));
        Location below = player.getLocation().subtract(0, 3, 0);
        double radius = cfgd("radius", 3.0);
        int duration = cfgi("slow_duration", 2 + level) * 20;
        below.getWorld().spawnParticle(Particle.CLOUD, below, 30, radius, 0.5, radius, 0.02);
        for (Entity e : below.getWorld().getNearbyEntities(below, radius, 2, radius)) {
            if (e == player || !(e instanceof LivingEntity le)) continue;
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 1, true, false));
        }
    }

    @Override public String getDescription() { return "Drop a slowing cloud below while gliding."; }
    @Override public String getDescription(int level) {
        return "§7Sneak to drop a §bcloud§7 that slows enemies for §e" + (2 + level) + "s§7."; }
}
