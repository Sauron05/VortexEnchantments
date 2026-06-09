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
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Shatter: AoE ground slam dealing 30% of hit damage in 2/3/4 block radius.
 */
public class ShatterEnchant extends VortexEnchant {

    public ShatterEnchant() {
        super("shatter", "Shatter", EnchantRarity.COMMON, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double radius = cfgd("radius", 1.0 + level);
        double aoePct = cfgd("aoe_percent", 0.30);
        double aoeDmg = event.getDamage() * aoePct;

        ParticleUtil.drawCircle(victim.getLocation(), radius, 16, Particle.BLOCK);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1.5f);

        for (LivingEntity nearby : MathUtil.getNearbyLiving(victim.getLocation(), radius)) {
            if (nearby.equals(victim) || nearby.equals(attacker)) continue;
            nearby.damage(aoeDmg, attacker);
        }
    }

    @Override
    public String getDescription(int level) {
        int radius = 1 + level;
        return "§7AoE §c30% §7damage in §e" + radius + "-block §7radius on hit.";
    }
}
