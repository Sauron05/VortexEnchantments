package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Catalyst: Damage multiplied by 1 + (potion effects on target × multiplier_per_effect).
 */
public class CatalystEnchant extends VortexEnchant {

    private static final double[] MULT_PER_EFFECT = {0.15, 0.20, 0.25};

    public CatalystEnchant() {
        super("catalyst", "Catalyst", EnchantRarity.EPIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        int effects = victim.getActivePotionEffects().size();
        if (effects == 0) return;

        double multiplierPerEffect = cfg("multiplier_per_effect", MULT_PER_EFFECT[level - 1]);
        double multiplier = 1.0 + effects * multiplierPerEffect;
        event.setDamage(event.getDamage() * multiplier);
    }

    @Override
    public String getDescription() { return "Deals more damage based on how many potion effects the target has."; }

    @Override
    public String getDescription(int level) {
        return "§7Damage ×§e(1 + effects × " + MULT_PER_EFFECT[level - 1] + ")§7. More effects = more damage.";
    }
}
