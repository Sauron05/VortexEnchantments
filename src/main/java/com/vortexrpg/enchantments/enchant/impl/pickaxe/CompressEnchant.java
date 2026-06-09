package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Compress: 9 of same drop auto-compact into 1 block. E.g. 9 raw iron -> 1 raw iron block. */
public class CompressEnchant extends VortexEnchant {
    public CompressEnchant() { super("compress", "Compress", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        // Count matching items in inventory and compact them
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            for (Material mat : new Material[]{Material.RAW_IRON, Material.RAW_GOLD, Material.RAW_COPPER}) {
                Material block = getBlockVersion(mat);
                if (block == null) continue;
                int count = countInInventory(player, mat);
                int sets = count / 9;
                if (sets > 0) {
                    removeFromInventory(player, mat, sets * 9);
                    player.getInventory().addItem(new ItemStack(block, sets));
                }
            }
        }, 1L);
    }

    private int countInInventory(Player p, Material mat) {
        int count = 0;
        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null && item.getType() == mat) count += item.getAmount();
        }
        return count;
    }

    private void removeFromInventory(Player p, Material mat, int amount) {
        int toRemove = amount;
        for (ItemStack item : p.getInventory().getContents()) {
            if (item == null || item.getType() != mat) continue;
            if (item.getAmount() <= toRemove) {
                toRemove -= item.getAmount();
                item.setAmount(0);
            } else {
                item.setAmount(item.getAmount() - toRemove);
                break;
            }
        }
    }

    private Material getBlockVersion(Material mat) {
        return switch (mat) {
            case RAW_IRON -> Material.RAW_IRON_BLOCK;
            case RAW_GOLD -> Material.RAW_GOLD_BLOCK;
            case RAW_COPPER -> Material.RAW_COPPER_BLOCK;
            default -> null;
        };
    }

    @Override public String getDescription() { return "Auto-compacts 9 of same ore drop into a block."; }
    @Override public String getDescription(int level) {
        return "§79 of same raw ore auto-compacted into §a1 block§7."; }
}
