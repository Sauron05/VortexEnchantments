package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Gravedigger: On kill, a zombie minion rises from the grave and fights
 * for you for 8/12/16 seconds. The minion targets nearby hostile entities.
 * Max 1/2/3 active minions.
 */
public class GravediggerEnchant extends VortexEnchant {

    public GravediggerEnchant() {
        super("gravedigger", "Gravedigger", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int maxMinions = cfgi("max_minions", level);
        String minionKey = "gravedigger_minions";
        int active = plugin.getPlayerDataManager().getInt(killer.getUniqueId(), minionKey);
        if (active >= maxMinions) return;

        Location spawnLoc = victim.getLocation();
        int lifetimeTicks = cfgi("lifetime_ticks", 120 + level * 80);

        // Rising animation
        ParticleUtil.spawn(spawnLoc, Particle.LARGE_SMOKE, 20, 0.5);
        ParticleUtil.spawn(spawnLoc, Particle.SOUL, 8, 0.3);
        SoundUtil.play(spawnLoc, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.6f, 0.5f);

        Zombie minion = spawnLoc.getWorld().spawn(spawnLoc, Zombie.class, z -> {
            z.setAdult();
            z.customName(net.kyori.adventure.text.Component.text("§2" + killer.getName() + "'s Minion"));
            z.setCustomNameVisible(true);
            z.setCanPickupItems(false);
            z.setShouldBurnInDay(false);
            z.setRemoveWhenFarAway(false);
        });

        plugin.getPlayerDataManager().setInt(killer.getUniqueId(), minionKey, active + 1);

        // Find nearest hostile to target
        LivingEntity nearest = null;
        double nearestDist = 20;
        for (var entity : minion.getNearbyEntities(15, 10, 15)) {
            if (entity.equals(killer) || entity.equals(minion)) continue;
            if (!(entity instanceof LivingEntity le)) continue;
            if (le instanceof Player) continue; // don't target players
            double dist = le.getLocation().distanceSquared(spawnLoc);
            if (dist < nearestDist * nearestDist) {
                nearest = le;
                nearestDist = Math.sqrt(dist);
            }
        }
        if (nearest != null) {
            minion.setTarget(nearest);
        }

        // Despawn after lifetime
        new BukkitRunnable() {
            @Override
            public void run() {
                if (minion.isValid() && !minion.isDead()) {
                    ParticleUtil.spawn(minion.getLocation(), Particle.SOUL, 10, 0.4);
                    SoundUtil.play(minion.getLocation(), Sound.ENTITY_ZOMBIE_DEATH, 0.5f, 1.5f);
                    minion.remove();
                }
                int current = plugin.getPlayerDataManager().getInt(killer.getUniqueId(), minionKey);
                plugin.getPlayerDataManager().setInt(killer.getUniqueId(), minionKey, Math.max(0, current - 1));
            }
        }.runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), lifetimeTicks);

        killer.sendMessage("§2[Gravedigger] §7A minion rises! (" + (active + 1) + "/" + maxMinions + ")");
    }

    @Override
    public String getDescription(int level) {
        int secs = (120 + level * 80) / 20;
        return "§7Kills raise a §2zombie minion §7for §e" + secs + "s§7. Max §e" + level + " §7minion(s).";
    }
}
