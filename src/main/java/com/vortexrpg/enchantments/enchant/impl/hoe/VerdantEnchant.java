package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/** Verdant: Tilling has 3/4/5% chance to automatically sprout a random crop. */
public class VerdantEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.03, 0.04, 0.05};
    private static final Material[] CROPS = {
        Material.WHEAT_SEEDS, Material.CARROT, Material.POTATO,
        Material.BEETROOT_SEEDS, Material.MELON_SEEDS, Material.PUMPKIN_SEEDS
    };

    public VerdantEnchant() { super("verdant", "Verdant", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Block clicked = event.getClickedBlock();
        if (clicked == null) return;
        if (clicked.getType() != Material.DIRT && clicked.getType() != Material.GRASS_BLOCK) return;
        double chance = cfg("chance", CHANCE[level-1]);
        if (Math.random() < chance) {
            // After tilling (next tick), plant crop on new farmland
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (clicked.getType() == Material.FARMLAND) {
                    Block above = clicked.getRelative(0, 1, 0);
                    if (above.getType() == Material.AIR) {
                        above.setType(CROPS[(int)(Math.random() * CROPS.length)]);
                    }
                }
            }, 2L);
        }
    }

    @Override public String getDescription() { return "Tilling may automatically sprout a crop."; }
    @Override public String getDescription(int level) {
        return "§a" + (int)(CHANCE[level-1]*100) + "§a%§7 chance to sprout random crop when tilling."; }
}
