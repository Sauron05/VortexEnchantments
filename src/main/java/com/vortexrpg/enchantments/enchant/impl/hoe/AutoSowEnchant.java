package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Auto Sow: Walking on farmland auto-plants seeds from inventory. */
public class AutoSowEnchant extends VortexEnchant {

    private static final Material[] SEED_TYPES = {Material.WHEAT_SEEDS, Material.CARROT, Material.POTATO, Material.BEETROOT_SEEDS};
    private static final Material[] CROP_TYPES = {Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS};

    public AutoSowEnchant() { super("auto_sow", "Auto Sow", EnchantRarity.RARE, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onMove(PlayerMoveEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Block below = player.getLocation().getBlock().getRelative(0, -1, 0);
        if (below.getType() != Material.FARMLAND) return;
        Block top = below.getRelative(0, 1, 0);
        if (!top.getType().isAir()) return;
        // Find a seed in inventory
        for (int i = 0; i < SEED_TYPES.length; i++) {
            ItemStack seed = findItem(player, SEED_TYPES[i]);
            if (seed != null) {
                top.setType(CROP_TYPES[i]);
                seed.setAmount(seed.getAmount() - 1);
                return;
            }
        }
    }

    private ItemStack findItem(Player player, Material mat) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == mat && item.getAmount() > 0) return item;
        }
        return null;
    }

    @Override public String getDescription() { return "Walking on farmland auto-plants seeds."; }
    @Override public String getDescription(int level) {
        return "§7Walk on farmland: §aauto-plant§7 seeds from inventory."; }
}
