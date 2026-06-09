package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Grit: Below 50% HP, gain a flat damage reduction bonus per level.
 */
public class GritEnchant extends VortexEnchant {
    public GritEnchant() { super("grit", "Grit", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        double maxHp = victim.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        double threshold = cfgd("hp_threshold", 0.50);
        if (victim.getHealth() / maxHp > threshold) return;
        double reduction = cfgd("flat_reduction", 1.0 * level);
        event.setDamage(Math.max(0, event.getDamage() - reduction));
    }

    @Override public String getDescription(int level) {
        return "§7Below 50% HP: reduce incoming damage by §a" + level + "§7.";
    }
}
