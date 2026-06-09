package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.List;

/** ReelMaster: Bonus XP multiplier on each consecutive catch. */
public class ReelMasterEnchant extends VortexEnchant {

    public ReelMasterEnchant() { super("reel_master", "Reel Master", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        int streak = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "reel_master_streak", 0) + 1;
        int maxStreak = cfgi("max_streak", 5);
        streak = Math.min(streak, maxStreak);
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "reel_master_streak", streak);
        int bonusXp = cfgi("xp_per_streak", level) * streak;
        event.setExpToDrop(event.getExpToDrop() + bonusXp);
    }

    @Override public String getDescription() { return "Consecutive catches grant bonus XP."; }
    @Override public String getDescription(int level) {
        return "§7Each consecutive catch: §a+" + level + " XP§7 per streak (max 5)."; }
}
