package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.UUID;

/** Erosion: Consecutive same-type block mining +8/10/12% speed per stack, max 200%. Resets on type change. */
public class ErosionEnchant extends VortexEnchant {
    private static final double[] BONUS_PER_STACK = {0.08, 0.10, 0.12};

    public ErosionEnchant() { super("erosion", "Erosion", EnchantRarity.RARE, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        UUID uuid = player.getUniqueId();
        int prevType = plugin.getPlayerDataManager().getInt(uuid, "erosion_last_block");
        int curType = event.getBlock().getType().ordinal();
        if (prevType != curType) {
            plugin.getPlayerDataManager().setInt(uuid, "erosion_stacks", 0);
        }
        plugin.getPlayerDataManager().setInt(uuid, "erosion_last_block", curType);
        int maxStacks = cfgi("max_stacks", 16); // 16 * 0.12 = ~200%
        int stacks = Math.min(plugin.getPlayerDataManager().getInt(uuid, "erosion_stacks") + 1, maxStacks);
        plugin.getPlayerDataManager().setInt(uuid, "erosion_stacks", stacks);
    }

    @Override public String getDescription() { return "Consecutive same-block mining gets faster."; }
    @Override public String getDescription(int level) {
        return "§7Same block streak: §a+" + (int)(BONUS_PER_STACK[level-1]*100) + "%§7 speed per stack (max §e200%§7)."; }
}
