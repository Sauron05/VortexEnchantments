package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/**
 * IronWill: When below 25% HP, all damage reduced by X%.
 */
public class IronWillEnchant extends VortexEnchant {
    public IronWillEnchant() { super("iron_will", "Iron Will", EnchantRarity.RARE, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        double threshold = cfgd("hp_threshold", 0.25);
        if (player.getHealth() / maxHp > threshold) return;
        double pct = cfgd("reduction_pct", 0.10 * level);
        event.setDamage(event.getDamage() * (1.0 - pct));
    }

    @Override public String getDescription(int level) {
        return "§7Below 25% HP: all damage reduced by §a" + (10 * level) + "%§7.";
    }
}
