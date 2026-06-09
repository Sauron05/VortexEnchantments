package com.vortexrpg.enchantments.enchant.impl.spear;

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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Eidolon: Kill creates a ghost copy of the slain mob — an invisible
 * zombie with Glowing that fights for the attacker for 8/12/16 seconds.
 */
public class EidolonEnchant extends VortexEnchant {

    public EidolonEnchant() {
        super("eidolon", "Eidolon", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(killer)) return;

        int lifetimeTicks = cfgi("lifetime_ticks", (6 + level * 4) * 20);
        Location spawnLoc = victim.getLocation();

        ParticleUtil.spawn(spawnLoc, Particle.SOUL, 15, 0.5);
        SoundUtil.play(spawnLoc, Sound.ENTITY_VEX_AMBIENT, 0.7f, 0.5f);

        Zombie ghost = spawnLoc.getWorld().spawn(spawnLoc, Zombie.class, z -> {
            z.setAdult();
            z.customName(net.kyori.adventure.text.Component.text("§7" + victim.getName() + "'s Ghost"));
            z.setCustomNameVisible(true);
            z.setCanPickupItems(false);
            z.setShouldBurnInDay(false);
            z.setRemoveWhenFarAway(false);
            z.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, lifetimeTicks, 0, false, false));
            z.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, lifetimeTicks, 0, false, false));
        });

        // Target nearest hostile
        LivingEntity nearest = null;
        double nearestDist = 20;
        for (var entity : ghost.getNearbyEntities(15, 10, 15)) {
            if (entity.equals(killer) || entity.equals(ghost)) continue;
            if (!(entity instanceof LivingEntity le)) continue;
            if (le instanceof Player) continue;
            double dist = le.getLocation().distanceSquared(spawnLoc);
            if (dist < nearestDist * nearestDist) {
                nearest = le;
                nearestDist = Math.sqrt(dist);
            }
        }
        if (nearest != null) ghost.setTarget(nearest);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (ghost.isValid() && !ghost.isDead()) {
                    ParticleUtil.spawn(ghost.getLocation(), Particle.SOUL, 10, 0.4);
                    ghost.remove();
                }
            }
        }.runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), lifetimeTicks);

        setCooldownFromConfig(killer, "cooldown", 15);
    }

    @Override
    public String getDescription(int level) {
        int secs = 6 + level * 4;
        return "§7Kill raises a §7ghost minion §7for §e" + secs + "s§7.";
    }
}
