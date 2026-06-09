package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * GroundedHeal: Slowly regenerate HP while sneaking.
 */
public class GroundedHealEnchant extends VortexEnchant {
    public GroundedHealEnchant() { super("grounded_heal", "Grounded Heal", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isSneaking()) return;
        double maxHp = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        if (player.getHealth() >= maxHp) return;
        double heal = cfgd("heal_per_tick", 0.3 * level);
        player.setHealth(Math.min(maxHp, player.getHealth() + heal));
    }

    @Override public String getDescription(int level) {
        return "§7Sneaking: regenerate §a" + String.format("%.1f", 0.3 * level) + " §7HP/s.";
    }
}
