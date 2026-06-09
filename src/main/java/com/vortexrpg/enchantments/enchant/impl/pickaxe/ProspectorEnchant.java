package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Prospector: Mining ore grants bonus XP. */
public class ProspectorEnchant extends VortexEnchant {
    private static final int[] XP = {2, 3, 5};

    public ProspectorEnchant() { super("prospector", "Prospector", EnchantRarity.COMMON, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getBlock().getType().name().endsWith("_ORE")) return;
        int bonus = cfgi("xp_bonus", XP[level - 1]);
        event.setExpToDrop(event.getExpToDrop() + bonus);
    }

    @Override public String getDescription() { return "Ore mining grants bonus XP."; }
    @Override public String getDescription(int level) {
        return "§7Ore: §a+" + XP[level - 1] + " XP§7 bonus per ore mined."; }
}
