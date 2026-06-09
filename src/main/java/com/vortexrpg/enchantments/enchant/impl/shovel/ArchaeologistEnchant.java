package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Archaeologist: Suspicious sand/gravel always yields a rare find (guaranteed non-empty loot). */
public class ArchaeologistEnchant extends VortexEnchant {
    public ArchaeologistEnchant() { super("archaeologist", "Archaeologist", EnchantRarity.EPIC, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (mat != Material.SUSPICIOUS_SAND && mat != Material.SUSPICIOUS_GRAVEL) return;
        // Guarantee a rare find by giving an emerald if no other drops
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            // Just drop a guaranteed item after the brush event
            event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5),
                new org.bukkit.inventory.ItemStack(Material.EMERALD));
        }, 1L);
    }

    @Override public String getDescription() { return "Suspicious blocks always yield a rare find."; }
    @Override public String getDescription(int level) { return "§7Suspicious sand/gravel: always §6rare loot§7 guaranteed."; }
}
