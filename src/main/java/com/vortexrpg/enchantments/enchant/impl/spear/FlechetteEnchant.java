package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Flechette: Hits spray 2/3/4 shards of damage at nearby enemies,
 * each dealing 25% of the original hit damage.
 */
public class FlechetteEnchant extends VortexEnchant {

    public FlechetteEnchant() {
        super("flechette", "Flechette", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int shards = cfgi("shards", 1 + level);
        double shardPct = cfgd("shard_percent", 0.25);
        double radius = cfgd("radius", 4.0);
        double shardDmg = event.getDamage() * shardPct;

        int hit = 0;
        for (LivingEntity nearby : MathUtil.getNearbyLiving(victim.getLocation(), radius)) {
            if (nearby.equals(victim) || nearby.equals(attacker)) continue;
            if (hit >= shards) break;
            nearby.damage(shardDmg, attacker);
            ParticleUtil.drawLine(victim.getLocation().add(0, 1, 0),
                    nearby.getLocation().add(0, 1, 0), Particle.CRIT, 0.4);
            hit++;
        }
    }

    @Override
    public String getDescription(int level) {
        return "§7Hits spray §e" + (1 + level) + " shards §7at nearby enemies for §c25% §7damage each.";
    }
}
