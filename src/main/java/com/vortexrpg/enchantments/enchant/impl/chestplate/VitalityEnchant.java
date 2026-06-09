package com.vortexrpg.enchantments.enchant.impl.chestplate;

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
 * Vitality: Grants bonus max health while wearing.
 */
public class VitalityEnchant extends VortexEnchant {
    public VitalityEnchant() { super("vitality", "Vitality", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        double hpPer = cfgd("hp_per_level", 2.0);
        double amount = hpPer * level;
        AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
        if (attr == null) return;
        NamespacedKey key = NamespacedKey.fromString("vortex:vitality_hp");
        if (attr.getModifier(key) == null) {
            attr.addModifier(new AttributeModifier(key, amount, AttributeModifier.Operation.ADD_NUMBER));
        }
    }

    @Override public String getDescription(int level) {
        return "§7Grants §c+" + (int)(2 * level) + " §7max health while wearing.";
    }
}
