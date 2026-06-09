package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Lifeline: While below 30% health, gain 15/20/25% lifesteal on all sword hits.
 * Desperate survival enchantment that rewards aggressive low-HP play.
 */
public class LifelineEnchant extends VortexEnchant {

    public LifelineEnchant() {
        super("lifeline", "Lifeline", EnchantRarity.RARE, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double threshold = cfgd("health_threshold", 0.30);
        double lifesteal = cfgd("lifesteal_ratio", 0.1 + level * 0.05);

        double maxHealth = attacker.getAttribute(Attribute.MAX_HEALTH).getValue();
        if (attacker.getHealth() / maxHealth > threshold) return;

        double healAmount = event.getDamage() * lifesteal;
        double newHealth = Math.min(maxHealth, attacker.getHealth() + healAmount);
        attacker.setHealth(newHealth);

        ParticleUtil.spawn(attacker.getLocation().add(0, 1, 0), Particle.HEART, 3, 0.3);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.1 + level * 0.05) * 100);
        return "§7Below 30% HP: §a" + pct + "% lifesteal§7 on sword hits. Survival instinct.";
    }
}
