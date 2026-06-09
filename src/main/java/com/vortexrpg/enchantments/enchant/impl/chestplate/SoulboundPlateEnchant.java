package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Soulbound Plate: On lethal damage, save the chestplate and re-equip on respawn.
 */
public class SoulboundPlateEnchant extends VortexEnchant {
    private static final java.util.Map<java.util.UUID, ItemStack> SAVED = new java.util.HashMap<>();

    public SoulboundPlateEnchant() { super("soulbound_plate", "Soulbound Plate", EnchantRarity.EPIC, 1, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (player.getHealth() - event.getFinalDamage() > 0) return;
        ItemStack chest = player.getInventory().getChestplate();
        if (chest == null) return;
        SAVED.put(player.getUniqueId(), chest.clone());
    }

    @Override
    public void onRespawn(org.bukkit.event.player.PlayerRespawnEvent event, Player player, int level) {
        if (!isEnabled()) return;
        ItemStack saved = SAVED.remove(player.getUniqueId());
        if (saved != null) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    player.getInventory().setChestplate(saved);
                }
            }, 1L);
        }
    }

    @Override public String getDescription(int level) {
        return "§7Chestplate is §6kept on death§7.";
    }
}
