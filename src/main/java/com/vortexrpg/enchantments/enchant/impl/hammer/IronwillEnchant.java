package com.vortexrpg.enchantments.enchant.impl.hammer;

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
 * Ironwill: When below 30% HP, deal 25/40/55% bonus damage.
 * Desperate warrior mechanic — the hammer swings heavier when you're dying.
 */
public class IronwillEnchant extends VortexEnchant {

    public IronwillEnchant() {
        super("ironwill", "Ironwill", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double maxHp = attacker.getAttribute(Attribute.MAX_HEALTH).getValue();
        double threshold = cfgd("hp_threshold", 0.30);
        if (attacker.getHealth() / maxHp > threshold) return;

        double bonus = cfgd("bonus", 0.10 + level * 0.15);
        event.setDamage(event.getDamage() * (1.0 + bonus));

        ParticleUtil.spawn(attacker.getLocation().add(0, 1, 0), Particle.ANGRY_VILLAGER, 5, 0.3);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.10 + level * 0.15) * 100);
        return "§7Below §c30% HP§7: deal §c+" + pct + "% §7damage.";
    }
}
