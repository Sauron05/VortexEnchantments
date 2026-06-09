package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/** Aether: Grants +X max health permanently while wearing the helmet. */
public class AetherEnchant extends VortexEnchant {
    public AetherEnchant() { super("aether", "Aether", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        double bonus = cfgd("health_bonus", level * 2.0);
        var attr = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH);
        if (attr == null) return;
        var mod = attr.getModifiers().stream()
                .filter(m -> m.getKey().toString().equals("vortex:aether_hp"))
                .findFirst().orElse(null);
        if (mod == null) {
            attr.addModifier(new org.bukkit.attribute.AttributeModifier(
                    org.bukkit.NamespacedKey.fromString("vortex:aether_hp"),
                    bonus,
                    org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER));
        } else if (mod.getAmount() != bonus) {
            attr.removeModifier(mod);
            attr.addModifier(new org.bukkit.attribute.AttributeModifier(
                    org.bukkit.NamespacedKey.fromString("vortex:aether_hp"),
                    bonus,
                    org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER));
        }
    }

    @Override public String getDescription(int level) {
        return "§7Grants §a+" + (level * 2) + " §7max health while worn.";
    }
}
