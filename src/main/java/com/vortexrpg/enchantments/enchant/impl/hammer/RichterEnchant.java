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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Richter: Right-click to send an earthquake line forward 8/12/16 blocks.
 * All entities in the path take damage and get launched upwards.
 * 15-second cooldown.
 */
public class RichterEnchant extends VortexEnchant {

    public RichterEnchant() {
        super("richter", "Richter", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;

        double length = cfgd("length", 4.0 + level * 4.0);
        double damage = cfgd("damage", 4.0 + level * 2.0);
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
                ParticleUtil.spawn(point, Particle.DUST_PLUME, 6, 0.5);
                SoundUtil.play(point, Sound.BLOCK_STONE_BREAK, 0.5f, 0.5f);

                for (var entity : point.getWorld().getNearbyEntities(point, 1.5, 2.0, 1.5)) {
                    if (entity.equals(player)) continue;
                    if (entity instanceof LivingEntity living) {
                        living.damage(damage, player);
                        living.setVelocity(new Vector(0, 0.6, 0));
                    }
                }
                dist += 2;
            }
        }.runTaskTimer(plugin, 0L, 2L);

        setCooldownFromConfig(player, "cooldown", 15);
    }

    @Override
    public String getDescription(int level) {
        int len = 4 + level * 4;
        return "§7Right-click: earthquake line §e" + len + " blocks §7forward, launches enemies. §8(15s CD)";
    }
}
