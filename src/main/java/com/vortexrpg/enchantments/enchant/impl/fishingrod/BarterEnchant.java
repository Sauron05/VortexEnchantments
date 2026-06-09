package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.Random;

/**
 * Barter — Fishing Rod (Rare, Max 3)
 * 10/15/20% chance to catch a bonus random trade item (emerald, enchanted book, gold ingot, etc.)
 * alongside the normal catch.
 */
public class BarterEnchant extends VortexEnchant {

    private static final Random RNG = new Random();
    private static final Material[] BARTERS = {
        Material.EMERALD, Material.GOLD_INGOT, Material.DIAMOND,
        Material.ENCHANTED_BOOK, Material.NAME_TAG, Material.SADDLE
    };

    public BarterEnchant() {
        super("barter", "Barter", "fishingrod");
    }

    @Override
    public String getTier() { return "RARE"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] chances = {10, 15, 20};
        return "§e" + chances[level - 1] + "%§7 chance to catch a rare bonus trade item.";
    }

    @Override
    public void onFish(PlayerFishEvent event, org.bukkit.entity.Player player, int level) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        double[] chances = {0.10, 0.15, 0.20};
        if (RNG.nextDouble() < cfgd("chance", chances[level - 1])) {
            Material mat = BARTERS[RNG.nextInt(BARTERS.length)];
            player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(mat));
        }
    }
}
