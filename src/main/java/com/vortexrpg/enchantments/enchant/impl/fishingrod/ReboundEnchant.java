package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;

/**
 * Rebound — Fishing Rod (Uncommon, Max 3)
 * When the bobber hits a solid block underwater, it bounces and repositions closer to a fish-rich zone.
 * Mechanically: on BITE_NOT_REACHED, teleports hook slightly and refreshes wait.
 */
public class ReboundEnchant extends VortexEnchant {

    public ReboundEnchant() {
        super("rebound", "Rebound", "fishingrod");
    }

    @Override
    public String getTier() { return "UNCOMMON"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        return "Bobber bounces to a better position when it misses, shortening wait time.";
    }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (event.getState() != PlayerFishEvent.State.FAILED_ATTEMPT) return;
        // Nudge hook slightly forward and reset wait
        org.bukkit.entity.FishHook hook = event.getHook();
        Vector dir = player.getLocation().getDirection().setY(0).normalize().multiply(0.5);
        hook.setVelocity(hook.getVelocity().add(dir));
        int reduction = cfgi("wait_reduction_ticks", 20 * level);
        hook.setMaxWaitTime(Math.max(hook.getMaxWaitTime() - reduction, 60));
    }
}
