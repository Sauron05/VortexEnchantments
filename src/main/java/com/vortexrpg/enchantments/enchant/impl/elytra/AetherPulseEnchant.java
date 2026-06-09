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

/** AetherPulse: Periodic energy pulse while gliding deals damage and knockback. */
public class AetherPulseEnchant extends VortexEnchant {

    public AetherPulseEnchant() { super("aether_pulse", "Aether Pulse", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled() || !player.isGliding()) return;
        int interval = cfgi("interval", Math.max(2, 6 - level));
        int ticks = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "ap_t", 0) + 1;
        if (ticks >= interval) {
            ticks = 0;
            double radius = cfgd("radius", 8.0);
            double damage = cfgd("damage", 2.0 + level);
            double knockback = cfgd("knockback", 0.6);
            player.getWorld().spawnParticle(Particle.SONIC_BOOM, player.getLocation(), 1);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 0.5f, 1.5f);
            for (Entity e : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)) {
                if (e == player || !(e instanceof LivingEntity le)) continue;
                le.damage(damage, player);
                Vector push = le.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(knockback);
                le.setVelocity(le.getVelocity().add(push));
            }
        }
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "ap_t", ticks);
    }

    @Override public String getDescription() { return "Periodic shockwave while gliding."; }
    @Override public String getDescription(int level) {
        return "§7Pulse every §e" + Math.max(2, 6 - level) + "s§7 while gliding: §c" + (int)(2.0 + level) + "§7 damage + knockback in §e8§7 blocks."; }
}
