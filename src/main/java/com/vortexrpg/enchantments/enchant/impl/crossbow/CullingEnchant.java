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
 * Culling: Targets below 20% HP have a 5/10/15% chance to be instantly killed.
 * Executioner's crossbow — finish off the weak.
 */
public class CullingEnchant extends VortexEnchant {

    public CullingEnchant() {
        super("culling", "Culling", EnchantRarity.EPIC, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double threshold = cfgd("hp_threshold", 0.20);
        double maxHp = victim.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        if (victim.getHealth() / maxHp > threshold) return;

        double chance = cfgd("execute_chance", level * 0.05);
        if (!MathUtil.chance(chance)) return;

        // Execute — set damage to lethal
        event.setDamage(victim.getHealth() + 1.0);

        ParticleUtil.burst(victim.getLocation().add(0, 1, 0), Particle.DAMAGE_INDICATOR, 15, 0.5);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 0.6f, 1.5f);
    }

    @Override
    public String getDescription(int level) {
        int pct = level * 5;
        return "§7Target below 20% HP: §c" + pct + "% execute §7chance.";
    }
}
