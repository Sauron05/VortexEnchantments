package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Metabolism: Doubles natural hunger regeneration rate.
 */
public class MetabolismEnchant extends VortexEnchant {
    public MetabolismEnchant() { super("metabolism", "Metabolism", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (player.getFoodLevel() < 18) return;
        double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        if (player.getHealth() >= maxHp) return;
        double healBonus = cfgd("heal_bonus", 0.25 * level);
        player.setHealth(Math.min(maxHp, player.getHealth() + healBonus));
    }

    @Override public String getDescription(int level) {
        return "§7When satiated: heal §a" + String.format("%.2f", 0.25 * level) + " §7extra HP/s.";
    }
}
