package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.Random;

/**
 * BiomeBonus — Fishing Rod (Rare, Max 3)
 * Based on the biome the player is in, catches a biome-appropriate bonus item (jungle = cocoa, etc).
 */
@SuppressWarnings("removal")
public class BiomeBonusEnchant extends VortexEnchant {

    private static final Random RNG = new Random();

    public BiomeBonusEnchant() {
        super("biome_bonus", "BiomeBonus", "fishingrod");
    }

    @Override
    public String getTier() { return "RARE"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        return "Catches a biome-appropriate bonus item based on where you're fishing.";
    }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        double[] chances = {0.20, 0.30, 0.40};
        if (RNG.nextDouble() > cfgd("chance", chances[level - 1])) return;

        String biomeName = player.getLocation().getBlock().getBiome().name().toLowerCase();
        Material reward;
        if (biomeName.contains("jungle")) reward = Material.COCOA_BEANS;
        else if (biomeName.contains("desert")) reward = Material.SAND;
        else if (biomeName.contains("snowy") || biomeName.contains("ice") || biomeName.contains("frozen")) reward = Material.SNOWBALL;
        else if (biomeName.contains("swamp")) reward = Material.SLIME_BALL;
        else if (biomeName.contains("mushroom")) reward = Material.RED_MUSHROOM;
        else if (biomeName.contains("ocean") || biomeName.contains("river")) reward = Material.KELP;
        else if (biomeName.contains("nether")) reward = Material.NETHER_WART;
        else reward = Material.WHEAT_SEEDS;

        player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(reward));
    }
}
