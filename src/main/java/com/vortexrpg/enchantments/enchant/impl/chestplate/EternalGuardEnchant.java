package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

/**
 * Eternal Guard: Damage reduction scales with the number of enchanted armor pieces worn.
 */
public class EternalGuardEnchant extends VortexEnchant {
    public EternalGuardEnchant() { super("eternal_guard", "Eternal Guard", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        PlayerInventory inv = victim.getInventory();
        int enchantedPieces = 0;
        for (ItemStack armor : inv.getArmorContents()) {
            if (armor != null && !armor.getType().isAir() && !armor.getEnchantments().isEmpty()) {
                enchantedPieces++;
            }
        }
        double pctPer = cfgd("reduction_per_piece", 0.03 * level);
        double totalReduction = pctPer * enchantedPieces;
        event.setDamage(event.getDamage() * (1.0 - totalReduction));
    }

    @Override public String getDescription(int level) {
        return "§7" + (3 * level) + "% §7damage reduction per enchanted armor piece worn.";
    }
}
