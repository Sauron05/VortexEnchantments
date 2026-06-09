package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Buckshot: Bolt impact sprays damage pellets in a small area.
 * Multiple enemies take reduced damage in a 2-block radius.
 */
public class BuckshotEnchant extends VortexEnchant {

    public BuckshotEnchant() {
        super("buckshot", "Buckshot", EnchantRarity.COMMON, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double radius = cfgd("radius", 2.0);
        double pelletDmg = event.getDamage() * cfgd("pellet_pct", 0.15 + level * 0.05);
        Location center = victim.getLocation();

        for (LivingEntity nearby : MathUtil.getNearbyLiving(center, radius)) {
            if (nearby.equals(shooter) || nearby.equals(victim)) continue;
            nearby.damage(pelletDmg, shooter);
        }

        ParticleUtil.burst(center, Particle.CRIT, 8, radius);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.15 + level * 0.05) * 100);
        return "§7Bolt sprays §cpellets §7— §c" + pct + "% AoE §7in 2 blocks.";
    }
}
