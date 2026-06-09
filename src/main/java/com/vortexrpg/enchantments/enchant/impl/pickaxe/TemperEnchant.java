package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.UUID;

/** Temper: Every 50 blocks mined adds +1% permanent mining speed (caps at 30/40/50%). Resets on repair. */
public class TemperEnchant extends VortexEnchant {
    private static final int[] MAX_STACKS = {30, 40, 50};

    public TemperEnchant() { super("temper", "Temper", EnchantRarity.RARE, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        UUID uuid = player.getUniqueId();
        int interval = cfgi("blocks_per_stack", 50);
        int maxStacks = cfgi("max_stacks_" + level, MAX_STACKS[level-1]);
        int count = plugin.getPlayerDataManager().getInt(uuid, "temper_block_count") + 1;
        plugin.getPlayerDataManager().setInt(uuid, "temper_block_count", count);
        if (count % interval == 0) {
            int stacks = plugin.getPlayerDataManager().getInt(uuid, "temper_stacks");
            if (stacks < maxStacks) {
                plugin.getPlayerDataManager().setInt(uuid, "temper_stacks", stacks + 1);
            }
        }
    }

    @Override public String getDescription() { return "Permanent mining speed grows with blocks mined."; }
    @Override public String getDescription(int level) {
        return "§7Every §e50 blocks§7: §a+1%§7 permanent mining speed (cap §e" + MAX_STACKS[level-1] + "%§7)."; }
}
