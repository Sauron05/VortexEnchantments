package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;

/**
 * LureCraft — Fishing Rod (Epic, Max 3)
 * Builds "lure charge" from consecutive catches; at max charge, dramatically reduces wait time for 60s.
 */
public class LureCraftEnchant extends VortexEnchant {

    public LureCraftEnchant() {
        super("lure_craft", "LureCraft", "fishingrod");
    }

    @Override
    public String getTier() { return "EPIC"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] maxStacks = {5, 4, 3};
        return "After §e" + maxStacks[level - 1] + "§7 consecutive catches, dramatically boost bite speed for §a60s§7.";
    }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            int stacks = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "lurecraft_stacks", 0) + 1;
            int[] maxStacks = {5, 4, 3};
            int max = cfgi("max_stacks", maxStacks[level - 1]);
            if (stacks >= max) {
                plugin.getPlayerDataManager().setInt(player.getUniqueId(), "lurecraft_stacks", 0);
                plugin.getPlayerDataManager().setLong(player.getUniqueId(), "lurecraft_active_until",
                        System.currentTimeMillis() + cfgi("active_duration_ms", 60000));
                player.sendMessage("§6[LureCraft] §eYour lure is irresistible — fish flock to your hook!");
            } else {
                plugin.getPlayerDataManager().setInt(player.getUniqueId(), "lurecraft_stacks", stacks);
            }
        } else if (event.getState() == PlayerFishEvent.State.FISHING) {
            long activeUntil = plugin.getPlayerDataManager().getLong(player.getUniqueId(), "lurecraft_active_until", 0L);
            if (System.currentTimeMillis() < activeUntil) {
                org.bukkit.entity.FishHook hook = event.getHook();
                hook.setMaxWaitTime(40);
                hook.setMinWaitTime(20);
            }
        }
    }
}
