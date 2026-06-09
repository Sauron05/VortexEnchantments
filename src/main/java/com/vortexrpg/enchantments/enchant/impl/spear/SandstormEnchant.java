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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Sandstorm: Thrown trident impact creates a damaging cloud in a 3/4/5 block
 * radius that applies Blindness and deals 2 DPS for 3 seconds.
 */
public class SandstormEnchant extends VortexEnchant {

    public SandstormEnchant() {
        super("sandstorm", "Sandstorm", EnchantRarity.RARE, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(shooter)) return;

        Location center = victim.getLocation();
        double radius = cfgd("radius", 2.0 + level);
        double dps = cfgd("dps", 2.0);

        SoundUtil.play(center, Sound.ENTITY_HUSK_AMBIENT, 1.0f, 0.5f);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks += 10;
                if (ticks > 60) {
                    cancel();
                    return;
                }

                ParticleUtil.drawCircle(center, radius, 20, Particle.CAMPFIRE_SIGNAL_SMOKE);
                ParticleUtil.spawn(center.clone().add(0, 1, 0), Particle.DUST_PLUME, 15, radius * 0.5);

                for (LivingEntity le : MathUtil.getNearbyLiving(center, radius)) {
                    if (le.equals(shooter)) continue;
                    le.damage(dps * 0.5, shooter);
                    le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0, false, false));
                }
            }
        }.runTaskTimer(JavaPlugin.getProvidingPlugin(getClass()), 0, 10);

        setCooldownFromConfig(shooter, "cooldown", 15);
    }

    @Override
    public String getDescription(int level) {
        int radius = 2 + level;
        return "§7Thrown hit: §e" + radius + "-block §7sandstorm (§8Blindness §7+ §c2 DPS§7, 3s).";
    }
}
