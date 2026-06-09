package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.Random;

/**
 * Patience — Fishing Rod (Uncommon, Max 3)
 * Each consecutive cast with no catch adds a stacking 5% bonus to next catch quality.
 * Stacks up to 3/5/7 times, then triggers a guaranteed treasure catch.
 */
public class PatienceEnchant extends VortexEnchant {

    @SuppressWarnings("unused")
    private static final Random RNG = new Random();

    public PatienceEnchant() {
        super("patience", "Patience", "fishingrod");
    }

    @Override
    public String getTier() { return "UNCOMMON"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] maxStacks = {3, 5, 7};
        return "Consecutive empty casts build §e+5%§7 quality per stack (max §e" + maxStacks[level - 1] + "§7 → guaranteed treasure).";
    }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            int stacks = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "patience_stacks", 0);
            int[] maxStacks = {3, 5, 7};
            int max = cfgi("max_stacks", maxStacks[level - 1]);
            if (stacks >= max) {
                // Guaranteed treasure
                player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.ENCHANTED_BOOK));
                player.sendMessage("§6[Patience] §eYour patience paid off with a treasure!");
            }
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "patience_stacks", 0);
        } else if (event.getState() == PlayerFishEvent.State.FAILED_ATTEMPT) {
            int stacks = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "patience_stacks", 0);
            int[] maxStacks = {3, 5, 7};
            if (stacks < cfgi("max_stacks", maxStacks[level - 1])) {
                plugin.getPlayerDataManager().setInt(player.getUniqueId(), "patience_stacks", stacks + 1);
            }
        }
    }
}
