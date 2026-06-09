package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.List;

/**
 * Schism: On critical hit, target's helmet and chestplate lose 15%/25%/50% durability instantly.
 */
@SuppressWarnings("deprecation")
public class SchismEnchant extends VortexEnchant {

    private static final double[] DURABILITY_PERCENT = {0.15, 0.25, 0.50};

    public SchismEnchant() {
        super("schism", "Schism", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        // Critical hit: player is falling and not on ground
        boolean isCrit = !attacker.isOnGround() && attacker.getFallDistance() > 0;
        if (!isCrit) return;

        double pct = cfg("durability_percent", DURABILITY_PERCENT[level - 1]);
        if (victim instanceof Player target) {
            damageArmorPiece(target.getInventory().getHelmet(), pct);
            damageArmorPiece(target.getInventory().getChestplate(), pct);
        }
    }

    private void damageArmorPiece(ItemStack item, double pct) {
        if (item == null || !(item.getItemMeta() instanceof Damageable meta)) return;
        int maxDur = item.getType().getMaxDurability();
        int dmg = (int)(maxDur * pct);
        meta.setDamage(Math.min(meta.getDamage() + dmg, maxDur));
        item.setItemMeta(meta);
    }

    @Override
    public String getDescription() { return "Critical hits shatter the target's helmet and chestplate."; }

    @Override
    public String getDescription(int level) {
        return "§7Critical hits: §creduces§7 helmet & chestplate durability by §e" + (int)(DURABILITY_PERCENT[level-1]*100) + "%§7.";
    }
}
