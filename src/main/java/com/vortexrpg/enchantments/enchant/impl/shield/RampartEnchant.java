package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Rampart: Blocking increases your armor value by 2/4/6 for 3s after block. */
public class RampartEnchant extends VortexEnchant {
    private static final double[] ARMOR = {2.0, 4.0, 6.0};

    public RampartEnchant() { super("rampart", "Rampart", EnchantRarity.RARE, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled() || !player.isBlocking()) return;
        double bonus = cfg("armor_bonus", ARMOR[level-1]);
        var attr = player.getAttribute(org.bukkit.attribute.Attribute.ARMOR);
        if (attr == null) return;
        attr.addModifier(new org.bukkit.attribute.AttributeModifier(
            new org.bukkit.NamespacedKey("vortexenchantments", "rampart"),
            bonus, org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER,
            org.bukkit.inventory.EquipmentSlotGroup.HAND
        ));
        plugin.getServer().getScheduler().runTaskLater(plugin, () ->
            attr.getModifiers().stream()
                .filter(m -> m.key().namespace().equals("vortexenchantments") && m.key().value().equals("rampart"))
                .forEach(attr::removeModifier), 60L);
    }

    @Override public String getDescription() { return "Blocking temporarily increases armor."; }
    @Override public String getDescription(int level) {
        return "§7On block: §a+" + (int)ARMOR[level-1] + "§7 armor for 3s."; }
}
