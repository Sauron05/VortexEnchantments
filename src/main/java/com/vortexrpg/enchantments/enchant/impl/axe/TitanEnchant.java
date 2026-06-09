package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Titan: Below 3♥ = axe damage ×2/×2.5/×3. Above 8♥ = damage ×0.5/0.6/0.7.
 */
public class TitanEnchant extends VortexEnchant {

    private static final double[] LOW_HP_MULT = {2.0, 2.5, 3.0};
    private static final double[] HIGH_HP_MULT = {0.5, 0.6, 0.7};

    public TitanEnchant() {
        super("titan", "Titan", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double hp = attacker.getHealth();
        double lowThreshold = cfg("low_hp_threshold", 6.0); // 3 hearts
        double highThreshold = cfg("high_hp_threshold", 16.0); // 8 hearts

        if (hp <= lowThreshold) {
            event.setDamage(event.getDamage() * cfg("low_hp_multiplier", LOW_HP_MULT[level - 1]));
        } else if (hp >= highThreshold) {
            event.setDamage(event.getDamage() * cfg("high_hp_multiplier", HIGH_HP_MULT[level - 1]));
        }
    }

    @Override
    public String getDescription() { return "Low HP: massive damage bonus. High HP: damage penalty."; }

    @Override
    public String getDescription(int level) {
        return "§7Below §c3♥§7: §a×" + LOW_HP_MULT[level-1] + "§7 dmg. Above §a8♥§7: §c×" + HIGH_HP_MULT[level-1] + "§7 dmg.";
    }
}
