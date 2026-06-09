package com.vortexrpg.enchantments.enchant.impl.hammer;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
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
 * Cataclysm: Right-click to perform a massive AoE ground pound —
 * 6/8/10 block radius, huge damage, flings enemies, applies Weakness.
 * 30-second cooldown. The ultimate hammer slam.
 */
public class CataclysmEnchant extends VortexEnchant {

    public CataclysmEnchant() {
        super("cataclysm", "Cataclysm", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;

        // Launch up
        player.setVelocity(new Vector(0, 1.2, 0));

        double radius = cfgd("radius", 4.0 + level * 2.0);
        double damage = cfgd("damage", 8.0 + level * 4.0);

        new BukkitRunnable() {
            boolean peaked = false;

            @Override
            public void run() {
                if (!peaked && player.getVelocity().getY() <= 0) {
                    peaked = true;
                }
                if (peaked && (player.getLocation().subtract(0, 0.1, 0).getBlock().getType().isSolid() || !player.isValid())) {
                    var loc = player.getLocation();

                    // Expanding shockwave visual
                    for (double r = 1; r <= radius; r += 2) {
                        ParticleUtil.drawCircle(loc, r, (int) (r * 6), Particle.DUST_PLUME);
                    }
                    ParticleUtil.spawn(loc, Particle.EXPLOSION, 5, 1.0);

                    for (LivingEntity e : MathUtil.getNearbyLiving(loc, radius)) {
                        if (e.equals(player)) continue;
                        e.damage(damage, player);
                        Vector fling = e.getLocation().toVector().subtract(loc.toVector())
                                .normalize().multiply(1.5).setY(0.7);
                        e.setVelocity(fling);
                        e.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 1, false, true));
                    }

                    player.setFallDistance(0);
                    SoundUtil.play(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f);
                    SoundUtil.play(loc, Sound.ENTITY_WARDEN_SONIC_BOOM, 0.5f, 0.8f);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 3L, 1L);

        setCooldownFromConfig(player, "cooldown", 30);
    }

    @Override
    public String getDescription(int level) {
        int r = 4 + level * 2;
        double d = 8 + level * 4;
        return "§7Right-click: §4Cataclysmic slam §7— §c" + d + " dmg §7in §e" + r + " blocks§7. §8(30s CD)";
    }
}
