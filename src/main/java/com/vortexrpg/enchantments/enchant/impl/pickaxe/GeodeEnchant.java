package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

/** Geode: Breaking stone has small chance to spawn a hollow geode with amethyst. */
public class GeodeEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.5, 1.0, 1.5};

    public GeodeEnchant() { super("geode", "Geode", EnchantRarity.EPIC, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (mat != Material.STONE && mat != Material.DEEPSLATE) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level-1]))) return;
        Location center = event.getBlock().getLocation();
        // Clear 3-block hollow sphere
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (Math.abs(x) + Math.abs(y) + Math.abs(z) <= 1) continue; // keep center area
                    Block b = center.clone().add(x, y, z).getBlock();
                    if (b.getType() == Material.STONE || b.getType() == Material.DEEPSLATE) {
                        b.setType(Material.CALCITE);
                    }
                }
            }
        }
        // Drop amethyst
        int count = 2 + new Random().nextInt(3);
        for (int i = 0; i < count; i++) {
            center.getWorld().dropItemNaturally(center.add(0.5, 0.5, 0.5), new ItemStack(Material.AMETHYST_SHARD));
        }
    }

    @Override public String getDescription() { return "Mining stone may reveal a geode with amethyst."; }
    @Override public String getDescription(int level) {
        return "§7Stone/deepslate: §a" + CHANCE[level-1] + "%§7 chance to spawn a §bgeode§7 with §e2-4 amethyst§7."; }
}
