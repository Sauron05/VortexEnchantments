package com.vortexrpg.enchantments.enchant.impl.spear;

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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Resonance: Ground-impact attacks create 3 expanding damage rings
 * (2/3/4 block max radius), each dealing 40% of the hit damage.
 */
public class ResonanceEnchant extends VortexEnchant {

    public ResonanceEnchant() {
        super("resonance", "Resonance", EnchantRarity.EPIC, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        double maxRadius = cfgd("max_radius", 1.0 + level);
        double ringDmg = event.getDamage() * cfgd("ring_percent", 0.40);
        Location center = victim.getLocation();

        SoundUtil.play(center, Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1.0f, 0.8f);

        new BukkitRunnable() {
            int ring = 0;

            @Override
            public void run() {
                ring++;
                if (ring > 3) {
                    cancel();
                    return;
                }

                double radius = (maxRadius / 3.0) * ring;
                ParticleUtil.drawCircle(center, radius, (int) (radius * 8), Particle.ENCHANTED_HIT);

                for (LivingEntity le : MathUtil.getNearbyLiving(center, radius + 1)) {
                    if (le.equals(attacker)) continue;
                    double dist = le.getLocation().distance(center);
                    if (dist >= radius - 0.5 && dist <= radius + 0.5) {
                        le.damage(ringDmg, attacker);
                    }
                }
            }
        }.runTaskTimer(JavaPlugin.getProvidingPlugin(getClass()), 4, 6);

        setCooldownFromConfig(attacker, "cooldown", 8);
    }

    @Override
    public String getDescription(int level) {
        int radius = 1 + level;
        return "§7Creates §e3 expanding rings §7(" + radius + " blocks), each dealing §c40% §7damage.";
    }
}
