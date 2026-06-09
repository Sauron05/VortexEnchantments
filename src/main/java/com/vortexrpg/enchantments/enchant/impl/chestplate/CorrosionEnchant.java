package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.List;

/**
 * Corrosion: On hit, corrode attacker's held weapon durability.
 */
public class CorrosionEnchant extends VortexEnchant {
    public CorrosionEnchant() { super("corrosion", "Corrosion", EnchantRarity.EPIC, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (!(attacker instanceof LivingEntity living)) return;
        ItemStack weapon = living.getEquipment() != null ? living.getEquipment().getItemInMainHand() : null;
        if (weapon == null || weapon.getType().isAir()) return;
        if (!(weapon.getItemMeta() instanceof Damageable dmg)) return;
        int durabLoss = cfgi("durability_loss", 3 * level);
        dmg.setDamage(dmg.getDamage() + durabLoss);
        weapon.setItemMeta(dmg);
    }

    @Override public String getDescription(int level) {
        return "§7Attacker's weapon loses §c" + (3 * level) + " §7durability per hit on you.";
    }
}
