package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/**
 * Featherfall Plus: Negates fall damage entirely up to a threshold.
 */
public class FeatherfallPlusEnchant extends VortexEnchant {
    public FeatherfallPlusEnchant() { super("featherfall_plus", "Featherfall Plus", EnchantRarity.RARE, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        double threshold = cfgd("negate_threshold", 4.0 * level);
        if (event.getDamage() <= threshold) {
            event.setCancelled(true);
        } else {
            event.setDamage(event.getDamage() - threshold);
        }
    }

    @Override public String getDescription(int level) {
        return "§7Negates up to §a" + (4 * level) + " §7fall damage.";
    }
}
