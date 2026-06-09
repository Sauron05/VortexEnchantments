package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Armageddon: Killing an enemy rains fire from the sky in the area.
 * Drops 5/8/12 fireballs over 3 seconds in a radius around the kill location.
 */
public class ArmageddonEnchant extends VortexEnchant {

    public ArmageddonEnchant() {
        super("armageddon", "Armageddon", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(killer)) return;

        int totalDrops = cfgi("fire_drops", 3 + level * 3);
        double radius = cfgd("radius", 4.0 + level * 2.0);
        double dropDamage = cfgd("drop_damage", 2.0 + level);
        Location center = victim.getLocation();

        new BukkitRunnable() {
            int drops = 0;

            @Override
            public void run() {
                if (drops >= totalDrops) {
                    cancel();
                    return;
                }

                double offX = (Math.random() - 0.5) * radius * 2;
                double offZ = (Math.random() - 0.5) * radius * 2;
                Location dropLoc = center.clone().add(offX, 0, offZ);

                ParticleUtil.spawn(dropLoc.clone().add(0, 8, 0), Particle.FLAME, 5, 0.3);
                ParticleUtil.drawLine(dropLoc.clone().add(0, 8, 0), dropLoc, Particle.FLAME, 8);
                ParticleUtil.spawn(dropLoc, Particle.LAVA, 8, 0.5);

                for (LivingEntity nearby : MathUtil.getNearbyLiving(dropLoc, 1.5)) {
                    if (nearby.equals(killer)) continue;
                    nearby.damage(dropDamage, killer);
                    nearby.setFireTicks(40);
                }

                SoundUtil.play(dropLoc, Sound.ENTITY_BLAZE_SHOOT, 0.5f, 0.6f);
                drops++;
            }
        }.runTaskTimer(JavaPlugin.getProvidingPlugin(getClass()), 5, 4);

        SoundUtil.play(center, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.7f, 0.5f);
        setCooldownFromConfig(killer, "cooldown", 15);
    }

    @Override
    public String getDescription(int level) {
        int drops = 3 + level * 3;
        return "§7Kills rain §c" + drops + " fire strikes §7from the sky.";
    }
}
