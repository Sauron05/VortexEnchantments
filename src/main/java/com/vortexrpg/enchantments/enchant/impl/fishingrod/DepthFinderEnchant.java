package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;

/**
 * DepthFinder — Fishing Rod (Rare, Max 3)
 * Increases the fishing radius/depth and shows nearby underwater entities as particles.
 */
public class DepthFinderEnchant extends VortexEnchant {

    public DepthFinderEnchant() {
        super("depth_finder", "DepthFinder", "fishingrod");
    }

    @Override
    public String getTier() { return "RARE"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] radius = {8, 12, 16};
        return "Reveals underwater entities within §e" + radius[level - 1] + " blocks§7 when casting.";
    }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (event.getState() != PlayerFishEvent.State.FISHING) return;
        double[] radii = {8, 12, 16};
        double radius = cfgd("reveal_radius", radii[level - 1]);
        org.bukkit.Location hookLoc = event.getHook().getLocation();
        for (org.bukkit.entity.Entity e : player.getWorld().getNearbyEntities(hookLoc, radius, radius, radius)) {
            if (!e.isInWater()) continue;
            player.getWorld().spawnParticle(Particle.BUBBLE_POP, e.getLocation(), 4, 0.2, 0.2, 0.2, 0);
        }
    }
}
