package com.vortexrpg.enchantments.enchant.impl.crossbow;

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
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Voltage: Bolt chains lightning to 2/3/4 nearby entities on hit.
 * Each chain deals 2 damage and applies brief Slowness.
 */
public class VoltageEnchant extends VortexEnchant {

    public VoltageEnchant() {
        super("voltage", "Voltage", EnchantRarity.RARE, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int chains = cfgi("chains", 1 + level);
        double chainDmg = cfgd("chain_damage", 2.0);
        double chainRange = cfgd("chain_range", 4.0);

        List<LivingEntity> nearby = MathUtil.getNearbyLiving(victim.getLocation(), chainRange);
        nearby.removeIf(e -> e.equals(victim) || e.equals(shooter));

        int count = 0;
        LivingEntity prev = victim;
        for (LivingEntity target : nearby) {
            if (count >= chains) break;
            target.damage(chainDmg, shooter);
            ParticleUtil.drawLine(prev.getLocation().add(0, 1, 0),
                    target.getLocation().add(0, 1, 0), Particle.ELECTRIC_SPARK, 0.2);
            prev = target;
            count++;
        }

        if (count > 0) {
            SoundUtil.play(victim.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 0.5f, 1.8f);
        }
    }

    @Override
    public String getDescription(int level) {
        int chains = 1 + level;
        return "§7Bolt §echains lightning §7to §e" + chains + " §7nearby (§c2 dmg §7each).";
    }
}
