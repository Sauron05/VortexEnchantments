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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Singularity: Right-click to create a gravity well at the look location
 * that pulls all entities within 4/6/8 blocks toward the center for 3 seconds.
 */
public class SingularityEnchant extends VortexEnchant {

    public SingularityEnchant() {
        super("singularity", "Singularity", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;

        double range = cfgd("range", 15.0);
        double radius = cfgd("radius", 2.0 + level * 2.0);

        // Place gravity well at looked-at block
        Location center = player.getTargetBlockExact((int) range) != null
                ? player.getTargetBlockExact((int) range).getLocation().add(0.5, 1, 0.5)
                : player.getEyeLocation().add(player.getLocation().getDirection().multiply(range));

        SoundUtil.play(center, Sound.BLOCK_PORTAL_TRIGGER, 0.5f, 2.0f);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks += 5;
                if (ticks > 60) {
                    cancel();
                    return;
                }

                ParticleUtil.drawCircle(center, radius, 20, Particle.REVERSE_PORTAL);
                ParticleUtil.spawn(center, Particle.PORTAL, 15, 0.5);

                for (var entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
                    if (entity.equals(player)) continue;
                    if (!(entity instanceof LivingEntity)) continue;

                    Vector pull = center.toVector().subtract(entity.getLocation().toVector());
                    double dist = pull.length();
                    if (dist > 0.5) {
                        pull.normalize().multiply(0.4);
                        entity.setVelocity(entity.getVelocity().add(pull));
                    }
                }
            }
        }.runTaskTimer(JavaPlugin.getProvidingPlugin(getClass()), 0, 5);

        setCooldownFromConfig(player, "cooldown", 20);
    }

    @Override
    public String getDescription(int level) {
        int radius = 2 + level * 2;
        return "§7Right-click: §5gravity well §7pulling enemies within §e" + radius + " blocks§7. §8(20s CD)";
    }
}
