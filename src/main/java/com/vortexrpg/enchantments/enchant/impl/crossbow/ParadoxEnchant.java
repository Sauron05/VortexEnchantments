package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Paradox: Bolt causes the target to re-experience the last 3/5/7 seconds of
 * accumulated damage. Time loops against them.
 */
public class ParadoxEnchant extends VortexEnchant {

    private static final String META_DMG_HISTORY = "vortex_paradox_dmg";

    public ParadoxEnchant() {
        super("paradox", "Paradox", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(shooter)) return;

        int lookbackSeconds = cfgi("lookback", 1 + level * 2);
        double multiplier = cfgd("multiplier", 0.5);
        double maxDamage = cfgd("max_damage", 20.0);

        double storedDamage = 0;
        if (victim.hasMetadata(META_DMG_HISTORY)) {
            storedDamage = victim.getMetadata(META_DMG_HISTORY).getFirst().asDouble();
        }

        double paradoxDamage = Math.min(storedDamage * multiplier, maxDamage);

        if (paradoxDamage > 1.0) {
            new BukkitRunnable() {
                int ticks = 0;
                final double totalDmg = paradoxDamage;
                final int intervals = 5;
                final double perTick = totalDmg / intervals;

                @Override
                public void run() {
                    if (ticks >= intervals || victim.isDead()) {
                        cancel();
                        return;
                    }
                    victim.damage(perTick, shooter);
                    ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.REVERSE_PORTAL, 8, 0.3);
                    ticks++;
                }
            }.runTaskTimer(plugin, 0L, 4L);

            SoundUtil.play(victim.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1.0f, 0.3f);
        }

        victim.setMetadata(META_DMG_HISTORY,
                new FixedMetadataValue(plugin, storedDamage + event.getFinalDamage()));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (victim.hasMetadata(META_DMG_HISTORY) && !victim.isDead()) {
                    double current = victim.getMetadata(META_DMG_HISTORY).getFirst().asDouble();
                    victim.setMetadata(META_DMG_HISTORY,
                            new FixedMetadataValue(plugin, Math.max(0, current - event.getFinalDamage())));
                }
            }
        }.runTaskLater(plugin, lookbackSeconds * 20L);

        setCooldownFromConfig(shooter, "cooldown", 10.0);
    }

    @Override
    public String getDescription(int level) {
        int secs = 1 + level * 2;
        return "§7Bolt: §5§lPARADOX §7— target relives §e" + secs + "s §7of damage. 10s CD.";
    }
}
