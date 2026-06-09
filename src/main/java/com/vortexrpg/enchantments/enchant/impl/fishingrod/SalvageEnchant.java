package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.Random;

/**
 * Salvage — Fishing Rod (Rare, Max 3)
 * 10/15/20% chance to catch ancient debris, raw ore, or other valuable mining drops.
 */
public class SalvageEnchant extends VortexEnchant {

    private static final Random RNG = new Random();
    private static final Material[] SALVAGE = {
        Material.IRON_INGOT, Material.GOLD_INGOT, Material.RAW_IRON,
        Material.RAW_GOLD, Material.COPPER_INGOT, Material.AMETHYST_SHARD
    };

    public SalvageEnchant() {
        super("salvage", "Salvage", "fishingrod");
    }

    @Override
    public String getTier() { return "RARE"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] pct = {10, 15, 20};
        return "§e" + pct[level - 1] + "%§7 chance to salvage buried ore or ingots from the depths.";
    }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        double[] chances = {0.10, 0.15, 0.20};
        if (RNG.nextDouble() < cfgd("chance", chances[level - 1])) {
            Material mat = SALVAGE[RNG.nextInt(SALVAGE.length)];
            player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(mat));
        }
    }
}
