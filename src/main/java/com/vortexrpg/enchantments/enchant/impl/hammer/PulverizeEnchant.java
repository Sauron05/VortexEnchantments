package com.vortexrpg.enchantments.enchant.impl.hammer;

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
 * Pulverize: 20/30/40% chance to deal double damage vs. armored targets.
 */
public class PulverizeEnchant extends VortexEnchant {

    public PulverizeEnchant() {
        super("pulverize", "Pulverize", EnchantRarity.COMMON, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        // Check if target has any armor
        boolean isArmored = victim.getEquipment() != null
                && victim.getEquipment().getArmorContents().length > 0
                && java.util.Arrays.stream(victim.getEquipment().getArmorContents())
                .anyMatch(item -> item != null && !item.getType().isAir());

        if (!isArmored) return;

        double chance = cfgd("chance", 0.10 + level * 0.10);
        if (Math.random() > chance) return;

        event.setDamage(event.getDamage() * 2.0);
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.CRIT, 15, 0.4);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.10 + level * 0.10) * 100);
        return "§7" + pct + "% chance to §cdouble damage §7vs armored targets.";
    }
}
