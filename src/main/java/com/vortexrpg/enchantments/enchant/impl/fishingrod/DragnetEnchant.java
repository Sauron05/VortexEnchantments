package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.Random;

/**
 * Dragnet — Fishing Rod (Epic, Max 3)
 * On catching a fish, 20/30/40% chance to drag up an entire net: 3/4/5 extra fish.
 */
public class DragnetEnchant extends VortexEnchant {

    private static final Random RNG = new Random();
    private static final Material[] FISH = {
        Material.COD, Material.SALMON, Material.TROPICAL_FISH, Material.PUFFERFISH
    };

    public DragnetEnchant() {
        super("dragnet", "Dragnet", "fishingrod");
    }

    @Override
    public String getTier() { return "EPIC"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] counts = {3, 4, 5};
        double[] chances = {20, 30, 40};
        return "§e" + (int)chances[level - 1] + "%§7 chance to haul up §a" + counts[level - 1] + "§7 extra fish at once.";
    }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        double[] chances = {0.20, 0.30, 0.40};
        if (RNG.nextDouble() < cfgd("chance", chances[level - 1])) {
            int[] counts = {3, 4, 5};
            int count = cfgi("extra_count", counts[level - 1]);
            for (int i = 0; i < count; i++) {
                Material fish = FISH[RNG.nextInt(FISH.length)];
                player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(fish));
            }
            player.sendMessage("§3[Dragnet] §bYou hauled in an entire net!");
        }
    }
}
