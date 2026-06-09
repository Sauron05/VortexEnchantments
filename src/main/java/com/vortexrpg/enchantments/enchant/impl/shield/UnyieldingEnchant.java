package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.meta.Damageable;

import java.util.List;

/** Unyielding: Shield cannot break; stays at 1 durability and still blocks 50%. */
public class UnyieldingEnchant extends VortexEnchant {

    public UnyieldingEnchant() { super("unyielding", "Unyielding", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        var shield = player.getInventory().getItemInOffHand();
        if (shield.getType() != Material.SHIELD) return;
        if (!(shield.getItemMeta() instanceof Damageable dmg)) return;
        int maxDur = shield.getType().getMaxDurability();
        if (dmg.getDamage() >= maxDur - 2) {
            dmg.setDamage(maxDur - 1);
            shield.setItemMeta(dmg);
            double reduction = cfg("low-durability-reduction", 0.3 + level * 0.1);
            event.setDamage(event.getDamage() * (1.0 - reduction));
        }
    }

    @Override public String getDescription() { return "Shield never breaks; blocks at low durability."; }
    @Override public String getDescription(int level) {
        return "§7Shield can't break. At 1 dur: §a" + (int)((0.3 + level * 0.1) * 100) + "%§7 block."; }
}
