package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Fortified Legs: Grants bonus armor toughness while wearing.
 */
public class FortifiedLegsEnchant extends VortexEnchant {
    public FortifiedLegsEnchant() { super("fortified_legs", "Fortified Legs", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        double bonus = cfgd("toughness_per_level", 1.0) * level;
        AttributeInstance attr = player.getAttribute(Attribute.ARMOR_TOUGHNESS);
        if (attr == null) return;
        NamespacedKey key = NamespacedKey.fromString("vortex:fortified_legs");
        if (attr.getModifier(key) == null) {
            attr.addModifier(new AttributeModifier(key, bonus, AttributeModifier.Operation.ADD_NUMBER));
        }
    }

    @Override public String getDescription(int level) {
        return "§7Grants §b+" + level + " §7armor toughness while wearing.";
    }
}
