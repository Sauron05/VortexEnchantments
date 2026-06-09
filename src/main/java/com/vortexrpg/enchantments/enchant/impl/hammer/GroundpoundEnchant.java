package com.vortexrpg.enchantments.enchant.impl.hammer;

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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Groundpound: Right-click to launch up 3 blocks, then slam down dealing AoE damage.
 * 12-second cooldown. Slam damage = 3/5/7 per entity hit.
 */
public class GroundpoundEnchant extends VortexEnchant {

    public GroundpoundEnchant() {
        super("groundpound", "Groundpound", EnchantRarity.RARE, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;

        // Launch up
        player.setVelocity(player.getVelocity().setY(0.9));

        double radius = cfgd("radius", 2.0 + level);
        double damage = cfgd("slam_damage", 1.0 + level * 2.0);

        new BukkitRunnable() {
            boolean rising = true;

            @Override
            public void run() {
                if (rising) {
                    if (player.getVelocity().getY() <= 0) {
                        rising = false;
                    }
                    return;
                }
                // Landed check
                if (player.getLocation().subtract(0, 0.1, 0).getBlock().getType().isSolid() || !player.isValid()) {
                    Location land = player.getLocation();
                    ParticleUtil.drawCircle(land, radius, 20, Particle.DUST_PLUME);
                    SoundUtil.play(land, Sound.ENTITY_GENERIC_EXPLODE, 0.6f, 1.2f);

                    for (LivingEntity e : MathUtil.getNearbyLiving(land, radius)) {
                        if (e.equals(player)) continue;
                        e.damage(damage, player);
                    }
                    player.setFallDistance(0);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 2L, 1L);

        setCooldownFromConfig(player, "cooldown", 12);
    }

    @Override
    public String getDescription(int level) {
        int r = 2 + level;
        double d = 1 + level * 2;
        return "§7Right-click: leap + slam §c" + d + " dmg §7in §e" + r + "-block §7radius. §8(12s CD)";
    }
}
