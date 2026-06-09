package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

/** Root: Near trees, digging has 15/20/25% to drop sticks/apples/saplings. */
public class RootEnchant extends VortexEnchant {
    private static final double[] CHANCE = {15, 20, 25};
    private static final Material[] LOOT = {Material.STICK, Material.APPLE, Material.OAK_SAPLING};

    public RootEnchant() { super("root_shovel", "Root", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level-1]))) return;
        // Check if near a tree (log within 4 blocks)
        int treeRadius = cfgi("tree_radius", 4);
        boolean nearTree = false;
        for (int x = -treeRadius; x <= treeRadius && !nearTree; x++) {
            for (int y = -treeRadius; y <= treeRadius && !nearTree; y++) {
                for (int z = -treeRadius; z <= treeRadius && !nearTree; z++) {
                    Material m = event.getBlock().getRelative(x, y, z).getType();
                    if (m.name().endsWith("_LOG") || m == Material.MUSHROOM_STEM) nearTree = true;
                }
            }
        }
        if (!nearTree) return;
        Material drop = LOOT[new Random().nextInt(LOOT.length)];
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(drop));
    }

    @Override public String getDescription() { return "Digging near trees may drop tree goods."; }
    @Override public String getDescription(int level) {
        return "§7Near trees: §a" + (int)CHANCE[level-1] + "%§7 to drop stick/apple/sapling."; }
}
