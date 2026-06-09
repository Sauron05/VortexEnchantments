package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;

/**
 * Sonar (Rod) — Fishing Rod (Uncommon, Max 3)
 * After casting, shows particle indicators at nearby underwater fish spawn areas to guide the player.
 * Mechanically: reduces wait time for fish by 15/25/35%.
 */
public class SonarRodEnchant extends VortexEnchant {

    public SonarRodEnchant() {
        super("sonar_rod", "Sonar", "fishingrod");
    }

    @Override
    public String getTier() { return "UNCOMMON"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] pct = {15, 25, 35};
        return "Emits sonar pulses on cast, reducing bite wait time by §a" + pct[level - 1] + "%§7.";
    }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (event.getState() != PlayerFishEvent.State.FISHING) return;
        // Spawn pulse particles at bobber
        org.bukkit.entity.FishHook hook = event.getHook();
        player.getWorld().spawnParticle(Particle.BUBBLE, hook.getLocation(), 12, 0.5, 0.3, 0.5, 0.05);

        // Reduce wait time: set shorter random wait
        int[] reductions = {15, 25, 35};
        int reduction = cfgi("wait_reduction_pct", reductions[level - 1]);
        int currentMax = hook.getMaxWaitTime();
        int newMax = (int)(currentMax * (1.0 - reduction / 100.0));
        hook.setMaxWaitTime(Math.max(newMax, 100));
        hook.setMinWaitTime((int)(hook.getMinWaitTime() * (1.0 - reduction / 100.0)));
    }
}
