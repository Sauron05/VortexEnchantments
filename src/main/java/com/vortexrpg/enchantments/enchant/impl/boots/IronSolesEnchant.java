package com.vortexrpg.enchantments.enchant.impl.boots;

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
 * IronSoles: Adds flat armor toughness passively.
 */
public class IronSolesEnchant extends VortexEnchant {
    public IronSolesEnchant() { super("iron_soles", "Iron Soles", EnchantRarity.COMMON, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        double toughness = cfgd("toughness", 1.0) * level;
        AttributeInstance attr = player.getAttribute(Attribute.ARMOR_TOUGHNESS);
        if (attr == null) return;
        NamespacedKey key = NamespacedKey.fromString("vortex:iron_soles");
        if (attr.getModifier(key) == null) {
            attr.addModifier(new AttributeModifier(key, toughness, AttributeModifier.Operation.ADD_NUMBER));
        }
    }

    @Override public String getDescription(int level) {
        return "§7Grants §a+" + level + " §7armor toughness.";
    }
}
