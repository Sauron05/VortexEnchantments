package com.vortexrpg.enchantments.enchant.impl.hammer;

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

import java.util.List;

/**
 * GravityWell: Hit applies Levitation II for 1.5s, then when they crash back down
 * they take 50/75/100% bonus fall damage. Yeet-and-splat enchant.
 */
public class GravityWellEnchant extends VortexEnchant {

    public GravityWellEnchant() {
        super("gravitywell", "Gravity Well", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        int levDuration = cfgi("lev_ticks", 30);
        victim.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, levDuration, 1, false, true));

        double bonusMult = cfgd("crash_bonus", 0.25 + level * 0.25);

        ParticleUtil.spawn(victim.getLocation(), Particle.ENCHANTED_HIT, 10, 0.5);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.6f, 0.5f);

        // After levitation ends, deal bonus crash damage
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks++;
                if (ticks > 60 || !victim.isValid()) {
                    cancel();
                    return;
                }
                if (ticks > levDuration && victim.isOnGround()) {
                    double fallDmg = victim.getFallDistance() * bonusMult;
                    if (fallDmg > 0) {
                        victim.damage(fallDmg, attacker);
                        ParticleUtil.drawCircle(victim.getLocation(), 2.0, 12, Particle.DUST_PLUME);
                        SoundUtil.play(victim.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.7f, 0.8f);
                    }
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);

        setCooldownFromConfig(attacker, "cooldown", 10);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.25 + level * 0.25) * 100);
        return "§7Levitate target → crash for §c+" + pct + "% §7fall damage. §8(10s CD)";
    }
}
