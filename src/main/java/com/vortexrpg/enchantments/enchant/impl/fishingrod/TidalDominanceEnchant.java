package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;

import java.util.List;

/** TidalDominance: Casting creates a massive tidal wave sweeping enemies. */
public class TidalDominanceEnchant extends VortexEnchant {

    public TidalDominanceEnchant() { super("tidal_dominance", "Tidal Dominance", EnchantRarity.MYTHIC, 1, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.FISHING) return;
        if (isOnCooldown(player)) return;
        setCooldownSeconds(player, cfgi("cooldown", 30));
        double radius = cfgd("radius", 10.0);
        double damage = cfgd("damage", 8.0);
        double force = cfgd("force", 1.5);
        org.bukkit.Location center = event.getHook().getLocation();
        center.getWorld().spawnParticle(Particle.SPLASH, center, 100, radius, 2, radius, 0.3);
        center.getWorld().playSound(center, Sound.ENTITY_GENERIC_SPLASH, 2.0f, 0.3f);
        for (Entity e : center.getWorld().getNearbyEntities(center, radius, 3, radius)) {
            if (e == player || !(e instanceof LivingEntity le)) continue;
            le.damage(damage, player);
            Vector push = le.getLocation().toVector().subtract(center.toVector()).normalize().multiply(force);
            push.setY(0.6);
            le.setVelocity(push);
        }
    }

    @Override public String getDescription() { return "Casting unleashes a devastating tidal wave."; }
    @Override public String getDescription(int level) {
        return "§dTidal wave§7: §c8§7 damage + massive knockback in §e10§7-block radius (§e30s§7 cd)."; }
}
