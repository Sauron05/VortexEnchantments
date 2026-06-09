package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Lancet: Critical hits cause a bleed effect that heals the attacker
 * for 15/25/35% of the bleed damage dealt over 4 seconds.
 */
public class LancetEnchant extends VortexEnchant {

    public LancetEnchant() {
        super("lancet", "Lancet", EnchantRarity.RARE, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (attacker.getAttackCooldown() != 1.0f) return; // only on crits (full cooldown + falling)

        double bleedTotal = event.getDamage() * 0.30;
        double healPct = cfgd("heal_percent", 0.05 + level * 0.10);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks++;
                if (ticks > 4 || victim.isDead() || !victim.isValid()) {
                    cancel();
                    return;
                }
                double tickDmg = bleedTotal / 4.0;
                victim.damage(tickDmg, attacker);

                double heal = tickDmg * healPct;
                attacker.setHealth(Math.min(attacker.getHealth() + heal,
                        attacker.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue()));

                ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.DAMAGE_INDICATOR, 4, 0.2);
                ParticleUtil.spawn(attacker.getLocation().add(0, 1, 0), Particle.HEART, 1, 0.2);
            }
        }.runTaskTimer(JavaPlugin.getProvidingPlugin(getClass()), 20, 20);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.05 + level * 0.10) * 100);
        return "§7Crits cause §cbleed§7; heal §a" + pct + "% §7of bleed damage.";
    }
}
