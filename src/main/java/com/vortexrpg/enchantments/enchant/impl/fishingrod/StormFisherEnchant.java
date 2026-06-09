package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;

/**
 * StormFisher — Fishing Rod (Epic, Max 3)
 * During rain/thunderstorms, fish bite speed is massively reduced and rare catch rate doubles.
 */
public class StormFisherEnchant extends VortexEnchant {

    public StormFisherEnchant() {
        super("storm_fisher", "StormFisher", "fishingrod");
    }

    @Override
    public String getTier() { return "EPIC"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        return "During storms, gain §a+50/75/100%§7 bite speed and §edoubled§7 rare catch rates.";
    }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (event.getState() != PlayerFishEvent.State.FISHING) return;
        if (!player.getWorld().hasStorm()) return;

        org.bukkit.entity.FishHook hook = event.getHook();
        double[] reductions = {0.50, 0.75, 1.00};
        double reduction = cfgd("wait_reduction", reductions[level - 1]);
        hook.setMaxWaitTime((int)(hook.getMaxWaitTime() * (1.0 - reduction)));
        hook.setMinWaitTime(Math.max((int)(hook.getMinWaitTime() * (1.0 - reduction)), 1));
    }
}
