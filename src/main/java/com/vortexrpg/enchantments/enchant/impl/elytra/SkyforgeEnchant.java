package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.List;

/** Skyforge: Repairs elytra durability while gliding in daylight. */
public class SkyforgeEnchant extends VortexEnchant {

    public SkyforgeEnchant() { super("skyforge", "Skyforge", EnchantRarity.RARE, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled() || !player.isGliding()) return;
        if (!player.getWorld().isDayTime() || player.getLocation().getBlock().getLightFromSky() < 10) return;
        int interval = cfgi("repair_interval", Math.max(2, 10 - level * 2));
        int ticks = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "skyforge_t", 0) + 1;
        if (ticks >= interval) {
            ticks = 0;
            ItemStack chest = player.getInventory().getChestplate();
            if (chest != null && chest.getItemMeta() instanceof Damageable dmg && dmg.getDamage() > 0) {
                dmg.setDamage(dmg.getDamage() - 1);
                chest.setItemMeta(dmg);
            }
        }
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "skyforge_t", ticks);
    }

    @Override public String getDescription() { return "Solar-powered elytra repair while gliding."; }
    @Override public String getDescription(int level) {
        return "§7Repairs §a1 durability§7 every §e" + Math.max(2, 10 - level * 2) + "s§7 while gliding in sunlight."; }
}
