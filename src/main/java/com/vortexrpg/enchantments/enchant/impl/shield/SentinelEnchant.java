package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;

import java.util.List;

/** Sentinel: Standing still while blocking gives bonus armor. */
public class SentinelEnchant extends VortexEnchant {

    private static final NamespacedKey MOD_KEY = new NamespacedKey("vortexenchantments", "sentinel_shield");

    public SentinelEnchant() { super("sentinel", "Sentinel", EnchantRarity.RARE, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        var attr = player.getAttribute(Attribute.ARMOR);
        if (attr == null) return;
        boolean stationary = player.getVelocity().lengthSquared() < 0.001 && player.isBlocking();
        // Remove old modifier
        attr.getModifiers().stream()
                .filter(m -> m.key().equals(MOD_KEY))
                .forEach(attr::removeModifier);
        if (stationary) {
            double bonus = cfg("armor-bonus", 2.0 + level);
            attr.addModifier(new AttributeModifier(MOD_KEY, bonus,
                    AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));
        }
    }

    @Override public String getDescription() { return "Stand still blocking for bonus armor."; }
    @Override public String getDescription(int level) {
        return "§7Stand+block: §a+" + (int)(2 + level) + "§7 armor."; }
}
