package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

/**
 * Wingspan — Elytra (Uncommon, Max 3)
 * Increases the effective glide duration by reducing elytra durability loss rate.
 * Implemented as a tick-based durability repair (counters natural wear).
 */
public class WingspanEnchant extends VortexEnchant {

    public WingspanEnchant() {
        super("wingspan", "Wingspan", "elytra");
    }

    @Override
    public String getTier() { return "UNCOMMON"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] pct = {25, 50, 75};
        return "Reduces elytra durability consumption by §a" + pct[level - 1] + "%§7 while gliding.";
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (!player.isGliding()) return;
        // Repair durability to counteract base wear (elytra loses 1 durability every ~20 ticks of gliding)
        // At level 3 we repair every tick effectively becoming 75% saved
        int[] repairFreq = {4, 2, 1}; // repair every N ticks
        long tick = plugin.getPlayerDataManager().getLong(player.getUniqueId(), "wingspan_tick", 0L) + 1;
        plugin.getPlayerDataManager().setLong(player.getUniqueId(), "wingspan_tick", tick);
        if (tick % repairFreq[level - 1] == 0) {
            org.bukkit.inventory.ItemStack chest = player.getInventory().getChestplate();
            if (chest != null && chest.getType() == org.bukkit.Material.ELYTRA) {
                org.bukkit.inventory.meta.Damageable meta = (org.bukkit.inventory.meta.Damageable) chest.getItemMeta();
                if (meta != null && meta.getDamage() > 0) {
                    meta.setDamage(meta.getDamage() - 1);
                    chest.setItemMeta(meta);
                }
            }
        }
    }
}
