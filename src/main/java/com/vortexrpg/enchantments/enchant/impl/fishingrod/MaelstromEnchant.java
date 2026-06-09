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

import java.util.List;

/** Maelstrom: Creates a massive vortex on cast dealing continuous damage. */
public class MaelstromEnchant extends VortexEnchant {

    public MaelstromEnchant() { super("maelstrom", "Maelstrom", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.FISHING) return;
        if (isOnCooldown(player)) return;
        setCooldownSeconds(player, cfgi("cooldown", Math.max(15, 35 - level * 5)));
        double radius = cfgd("radius", 4.0 + level);
        double dps = cfgd("dps", 1.5 + level * 0.5);
        int duration = cfgi("duration", 3 + level);
        org.bukkit.Location loc = event.getHook().getLocation();
        // Schedule repeated damage over duration
        for (int tick = 0; tick < duration; tick++) {
            final int t = tick;
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                loc.getWorld().spawnParticle(Particle.SPLASH, loc, 40, radius, 1, radius, 0.1);
                if (t == 0) loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_SPLASH, 2.0f, 0.5f);
                for (Entity e : loc.getWorld().getNearbyEntities(loc, radius, 2, radius)) {
                    if (e == player || !(e instanceof LivingEntity le)) continue;
                    if (le instanceof Player) continue;
                    le.damage(dps, player);
                }
            }, tick * 20L);
        }
    }

    @Override public String getDescription() { return "Creates a damaging vortex at bobber."; }
    @Override public String getDescription(int level) {
        return "§7On cast, §bmaelstrom§7 deals §c" + String.format("%.1f", 1.5 + level * 0.5) + "/s§7 in §e" + (int)(4.0 + level) + "§7-block radius for §e" + (3 + level) + "s§7."; }
}
