package com.vortexrpg.enchantments.enchant.impl.hammer;

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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Faultline: Right-click to slam a fissure eruption line — ground splits,
 * launching entities up + applying Slowness and Weakness for 3 seconds.
 * 18-second cooldown.
 */
public class FaultlineEnchant extends VortexEnchant {

    public FaultlineEnchant() {
        super("faultline", "Faultline", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;

        double length = cfgd("length", 6.0 + level * 3.0);
        double damage = cfgd("damage", 5.0 + level * 2.0);
        Vector dir = player.getLocation().getDirection().setY(0).normalize();
        Location start = player.getLocation().clone();

        new BukkitRunnable() {
            double dist = 1;

            @Override
            public void run() {
                if (dist > length) {
                    cancel();
                    return;
                }

                Location point = start.clone().add(dir.clone().multiply(dist));
                ParticleUtil.spawn(point, Particle.DUST_PLUME, 8, 0.5);
                ParticleUtil.spawn(point.clone().add(0, 0.5, 0), Particle.LAVA, 3, 0.3);

                for (var entity : point.getWorld().getNearbyEntities(point, 1.5, 2.0, 1.5)) {
                    if (entity.equals(player)) continue;
                    if (entity instanceof LivingEntity living) {
                        living.damage(damage, player);
                        living.setVelocity(new Vector(0, 0.8, 0));
                        living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 2, false, true));
                        living.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 1, false, true));
                    }
                }
                dist += 2;
            }
        }.runTaskTimer(plugin, 0L, 2L);

        SoundUtil.play(start, Sound.ENTITY_GENERIC_EXPLODE, 0.7f, 0.5f);
        setCooldownFromConfig(player, "cooldown", 18);
    }

    @Override
    public String getDescription(int level) {
        int len = (int) (6 + level * 3);
        return "§7Right-click: fissure eruption §e" + len + " blocks§7, launch + debuff. §8(18s CD)";
    }
}
