package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;

/**
 * Chum — Fishing Rod (Common, Max 3)
 * Each cast has a 10/15/20% chance to "chum" the water, reducing the next bite wait time significantly.
 */
public class ChumEnchant extends VortexEnchant {

    private static final java.util.Random RNG = new java.util.Random();

    public ChumEnchant() {
        super("chum", "Chum", "fishingrod");
    }

    @Override
    public String getTier() { return "COMMON"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] pct = {10, 15, 20};
        return "§e" + pct[level - 1] + "%§7 chance to chum the water, halving the next bite wait time.";
    }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (event.getState() != PlayerFishEvent.State.FISHING) return;
        double[] chances = {0.10, 0.15, 0.20};
        if (RNG.nextDouble() < cfgd("chance", chances[level - 1])) {
            org.bukkit.entity.FishHook hook = event.getHook();
            hook.setMaxWaitTime(hook.getMaxWaitTime() / 2);
            hook.setMinWaitTime(hook.getMinWaitTime() / 2);
            player.sendMessage("§b[Chum] §fThe water churns with activity!");
        }
    }
}
