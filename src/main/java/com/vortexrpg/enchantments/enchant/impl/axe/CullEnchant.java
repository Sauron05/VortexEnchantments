package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Cull: +60%/80%/100% damage to entities below 20% max HP.
 */
public class CullEnchant extends VortexEnchant {

    private static final double[] BONUS = {0.60, 0.80, 1.00};

    public CullEnchant() {
        super("cull", "Cull", EnchantRarity.RARE, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double threshold = cfg("hp_threshold_percent", 20.0) / 100.0;
        double maxHp = victim.getAttribute(Attribute.MAX_HEALTH).getValue();
        if (victim.getHealth() / maxHp <= threshold) {
            event.setDamage(event.getDamage() * (1.0 + cfg("bonus_damage_percent", BONUS[level - 1])));
        }
    }

    @Override
    public String getDescription() { return "Deals massive bonus damage to enemies near death."; }

    @Override
    public String getDescription(int level) {
        return "§7Targets below §c20% HP§7: §a+" + (int)(BONUS[level-1]*100) + "%§7 bonus damage.";
    }
}
