package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Fracture: Successive hits on the SAME armor slot deal increasing durability damage per stack.
 */
public class FractureEnchant extends VortexEnchant {

    private static final double[] DUR_MULTIPLIERS = {2.0, 2.5, 3.0};

    public FractureEnchant() {
        super("fracture", "Fracture", EnchantRarity.RARE, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (!(victim instanceof Player target)) return;

        int maxStacks = cfgi("max_stacks", 4);
        double durMult = DUR_MULTIPLIERS[level - 1];

        EntityEquipment eq = target.getEquipment();
        if (eq == null) return;

        // Determine targeted slot from attacker's horizontal facing (simplified)
        int slotIndex = getTargetedArmorSlot(attacker, target);
        String stackKey = "fracture_stacks_" + target.getUniqueId() + "_" + slotIndex;
        int stacks = plugin.getPlayerDataManager().getInt(attacker.getUniqueId(), stackKey);

        // Reset stacks if targeting a different slot than last time
        String lastSlotKey = "fracture_lastslot_" + target.getUniqueId();
        int lastSlot = plugin.getPlayerDataManager().getInt(attacker.getUniqueId(), lastSlotKey);
        if (lastSlot >= 0 && lastSlot != slotIndex) {
            stacks = 0;
        }

        stacks = Math.min(stacks + 1, maxStacks);
        plugin.getPlayerDataManager().setInt(attacker.getUniqueId(), stackKey, stacks);
        plugin.getPlayerDataManager().setInt(attacker.getUniqueId(), lastSlotKey, slotIndex);

        // Damage armor durability
        ItemStack armor = getArmorAtSlot(eq, slotIndex);
        if (armor != null && armor.getItemMeta() instanceof Damageable damageable) {
            int bonusDur = (int) (durMult * stacks);
            int newDamage = damageable.getDamage() + bonusDur;
            damageable.setDamage(newDamage);
            armor.setItemMeta((ItemMeta) damageable);
        }
    }

    private int getTargetedArmorSlot(Player attacker, Player target) {
        double relY = attacker.getEyeLocation().getY() - target.getLocation().getY();
        if (relY > 1.5) return 0; // helmet
        if (relY > 0.8) return 1; // chestplate
        if (relY > 0.2) return 2; // leggings
        return 3; // boots
    }

    private ItemStack getArmorAtSlot(EntityEquipment eq, int slot) {
        return switch (slot) {
            case 0 -> eq.getHelmet();
            case 1 -> eq.getChestplate();
            case 2 -> eq.getLeggings();
            default -> eq.getBoots();
        };
    }

    @Override
    public String getDescription() { return "Successive hits to the same armor slot deal stacking durability damage."; }

    @Override
    public String getDescription(int level) {
        return "Stack hits on same armor slot: §c×" + DUR_MULTIPLIERS[level-1] + " §7durability per stack (max 4).";
    }
}
