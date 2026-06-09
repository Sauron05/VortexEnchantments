package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.Set;

/** Mega Dig: Mine 3×3×3 cube of soft blocks. */
public class MegaDigEnchant extends VortexEnchant {
    private static final Set<Material> SOFT = Set.of(
            Material.DIRT, Material.GRASS_BLOCK, Material.SAND, Material.GRAVEL,
            Material.RED_SAND, Material.COARSE_DIRT, Material.CLAY, Material.SOUL_SAND,
            Material.SOUL_SOIL, Material.SNOW_BLOCK, Material.MUD, Material.PODZOL);

    public MegaDigEnchant() { super("mega_dig", "Mega Dig", EnchantRarity.EPIC, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!SOFT.contains(event.getBlock().getType())) return;
        Block center = event.getBlock();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    Block b = center.getRelative(x, y, z);
                    if (SOFT.contains(b.getType())) {
                        b.breakNaturally(player.getInventory().getItemInMainHand());
                    }
                }
            }
        }
    }

    @Override public String getDescription() { return "Mine 3×3×3 cube of soft blocks."; }
    @Override public String getDescription(int level) {
        return "§7Mine §a3×3×3§7 cube of soft blocks."; }
}
