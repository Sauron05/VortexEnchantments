package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Thornsplate: Attackers take flat damage per hit when they strike you.
 */
public class ThornsplateEnchant extends VortexEnchant {
    public ThornsplateEnchant() { super("thornsplate", "Thornsplate", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (!(attacker instanceof LivingEntity living)) return;
        double dmg = cfgd("reflect_damage", 0.5 + level * 0.5);
        living.damage(dmg, victim);
    }

    @Override public String getDescription(int level) {
        return "§7Attackers take §c" + String.format("%.1f", 0.5 + level * 0.5) + " §7damage per hit.";
    }
}
