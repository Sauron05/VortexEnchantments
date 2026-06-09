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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Nullfield: Right-click to create a field that removes all positive potion effects
 * from enemies in a 5/7/9 block radius for 4 seconds.
 * 20-second cooldown. Anti-buff zone.
 */
public class NullfieldEnchant extends VortexEnchant {

    public NullfieldEnchant() {
        super("nullfield", "Nullfield", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;

        double radius = cfgd("radius", 3.0 + level * 2.0);
        int durationTicks = cfgi("duration_ticks", 80);
        var center = player.getLocation().clone();

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks += 10;
                if (ticks > durationTicks || !player.isValid()) {
                    cancel();
                    return;
                }

                ParticleUtil.drawCircle(center, radius, 20, Particle.ENCHANTED_HIT);

                for (LivingEntity entity : MathUtil.getNearbyLiving(center, radius)) {
                    if (entity.equals(player)) continue;
                    // Strip beneficial effects
                    var effects = new java.util.ArrayList<>(entity.getActivePotionEffects());
                    for (var effect : effects) {
                        var type = effect.getType();
                        if (type.equals(org.bukkit.potion.PotionEffectType.SPEED)
                                || type.equals(org.bukkit.potion.PotionEffectType.STRENGTH)
                                || type.equals(org.bukkit.potion.PotionEffectType.REGENERATION)
                                || type.equals(org.bukkit.potion.PotionEffectType.RESISTANCE)
                                || type.equals(org.bukkit.potion.PotionEffectType.FIRE_RESISTANCE)
                                || type.equals(org.bukkit.potion.PotionEffectType.ABSORPTION)
                                || type.equals(org.bukkit.potion.PotionEffectType.INVISIBILITY)) {
                            entity.removePotionEffect(type);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 10L);

        SoundUtil.play(center, Sound.ENTITY_WARDEN_SONIC_BOOM, 0.4f, 2.0f);
        setCooldownFromConfig(player, "cooldown", 20);
    }

    @Override
    public String getDescription(int level) {
        int r = 3 + level * 2;
        return "§7Right-click: §dstrips buffs §7from enemies in §e" + r + " blocks §7for §e4s§7. §8(20s CD)";
    }
}
