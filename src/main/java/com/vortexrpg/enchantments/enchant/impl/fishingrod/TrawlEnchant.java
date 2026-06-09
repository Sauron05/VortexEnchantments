package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.Random;

/**
 * Trawl — Fishing Rod (Rare, Max 3)
 * Each catch has a 20/30/40% chance to reel in 2 extra fish simultaneously.
 */
public class TrawlEnchant extends VortexEnchant {

    private static final Random RNG = new Random();

    public TrawlEnchant() {
        super("trawl", "Trawl", "fishingrod");
    }

    @Override
    public String getTier() { return "RARE"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] pct = {20, 30, 40};
        return "§e" + pct[level - 1] + "%§7 chance to catch §a2 extra fish§7 simultaneously.";
    }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        double[] chances = {0.20, 0.30, 0.40};
        if (RNG.nextDouble() < cfgd("chance", chances[level - 1])) {
            int extra = cfgi("extra_fish", 2);
            for (int i = 0; i < extra; i++) {
                player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.COD));
            }
        }
    }
}
