package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Prism: Splits damage into three elemental types - fire (30%), ice/magic (30%),
 * and true damage (40%). Some mobs are weak to certain types.
 * Fire portion ignites, ice portion slows.
 */
public class PrismEnchant extends VortexEnchant {

    public PrismEnchant() {
        super("prism", "Prism", EnchantRarity.RARE, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double fireRatio = cfgd("fire_ratio", 0.30);
        double iceRatio = cfgd("ice_ratio", 0.30);
        double trueRatio = cfgd("true_ratio", 0.40);
        double bonusPerLevel = cfgd("bonus_per_level", 0.05);

        double baseDamage = event.getDamage();
        double totalBonus = 1.0 + (level - 1) * bonusPerLevel;

        double fireDmg = baseDamage * fireRatio * totalBonus;
        double iceDmg = baseDamage * iceRatio * totalBonus;
        double trueDmg = baseDamage * trueRatio * totalBonus;

        event.setDamage(fireDmg + iceDmg);

        victim.setFireTicks(cfgi("fire_ticks", 30 + level * 10));

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (!victim.isValid() || victim.isDead()) return;
            double health = victim.getHealth();
            victim.setHealth(Math.max(0.0, health - trueDmg));
        });

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.FLAME, 5, 0.3);
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SNOWFLAKE, 5, 0.3);
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.END_ROD, 5, 0.3);
    }

    @Override
    public String getDescription(int level) {
        int bonus = (int) ((level - 1) * 5);
        String extra = bonus > 0 ? " §a(+" + bonus + "% total)" : "";
        return "§7Splits damage: §c30% fire§7, §b30% ice§7, §e40% true§7." + extra;
    }
}
