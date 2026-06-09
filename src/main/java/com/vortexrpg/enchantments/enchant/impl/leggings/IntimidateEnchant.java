package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Intimidate: Passively reduces damage dealt by nearby enemies.
 */
public class IntimidateEnchant extends VortexEnchant {
    public IntimidateEnchant() { super("intimidate", "Intimidate", EnchantRarity.EPIC, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        double radius = cfgd("radius", 5.0);
        List<LivingEntity> nearby = MathUtil.getNearbyLiving(player.getLocation(), radius);
        if (nearby.size() <= 1) return;
        ParticleUtil.spawn(player.getLocation().add(0, 1, 0), Particle.SOUL, 2, 0.5);
    }

    @Override
    public void onDamaged(org.bukkit.event.entity.EntityDamageByEntityEvent event, Player victim, org.bukkit.entity.Entity attacker, int level) {
        if (!isEnabled()) return;
        double radius = cfgd("radius", 5.0);
        if (attacker.getLocation().distance(victim.getLocation()) > radius) return;
        double pctReduction = cfgd("reduction_pct", 0.05 * level);
        event.setDamage(event.getDamage() * (1.0 - pctReduction));
    }

    @Override public String getDescription(int level) {
        return "§7Enemies within 5 blocks deal §a" + (5 * level) + "% §7less damage.";
    }
}
