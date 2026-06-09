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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Dominion: Hit target walks involuntarily toward the shooter for 2/3/4 seconds.
 * Mind control bolt — the hit entity is "dominated" and pulled toward you.
 */
public class DominionEnchant extends VortexEnchant {

    public DominionEnchant() {
        super("dominion", "Dominion", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(shooter)) return;

        int duration = cfgi("duration", (level + 1)) * 20;
        double speed = cfgd("walk_speed", 0.15);

        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 2, false, false));

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= duration || victim.isDead() || shooter.isDead()) {
                    cancel();
                    return;
                }
                if (ticks % 4 == 0) {
                    Vector toShooter = shooter.getLocation().toVector()
                            .subtract(victim.getLocation().toVector()).normalize().multiply(speed);
                    toShooter.setY(0);
                    victim.setVelocity(toShooter);
                    ParticleUtil.spawn(victim.getLocation().add(0, 2, 0), Particle.ENCHANTED_HIT, 3, 0.2);
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        SoundUtil.play(victim.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 0.8f, 0.5f);
        setCooldownFromConfig(shooter, "cooldown", 12.0);
    }

    @Override
    public String getDescription(int level) {
        int dur = level + 1;
        return "§7Bolt: §d§lDOMINATE §7— target walks toward you for §e" + dur + "s§7. 12s CD.";
    }
}
