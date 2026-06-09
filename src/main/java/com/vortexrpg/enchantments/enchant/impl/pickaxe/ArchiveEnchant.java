package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Archive: First time mining a new block type, gain +5/8/10 bonus XP. Logged in PDM. */
public class ArchiveEnchant extends VortexEnchant {
    private static final int[] XP_BONUS = {5, 8, 10};

    public ArchiveEnchant() { super("archive_pick", "Archive", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        String key = "archive_" + event.getBlock().getType().name();
        if (plugin.getPlayerDataManager().hasVisitedBiome(player.getUniqueId(), key)) return;
        plugin.getPlayerDataManager().markBiomeVisited(player.getUniqueId(), key);
        player.giveExp(cfgi("first_mine_xp", XP_BONUS[level-1]));
    }

    @Override public String getDescription() { return "First mine of each block type grants bonus XP."; }
    @Override public String getDescription(int level) {
        return "§7First time mining a new block: §a+" + XP_BONUS[level-1] + " XP§7."; }
}
