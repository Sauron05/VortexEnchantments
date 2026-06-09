package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Nexus Tap: Mining ore heals and restores hunger. */
public class NexusTapEnchant extends VortexEnchant {
    private static final double[] HEAL = {1, 1.5, 2};
    private static final int[] FOOD = {1, 1, 2};

    public NexusTapEnchant() { super("nexus_tap", "Nexus Tap", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getBlock().getType().name().endsWith("_ORE")) return;
        double heal = cfg("heal", HEAL[level - 1]);
        double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        player.setHealth(Math.min(maxHp, player.getHealth() + heal));
        int food = cfgi("food", FOOD[level - 1]);
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + food));
    }

    @Override public String getDescription() { return "Mining ore heals and feeds you."; }
    @Override public String getDescription(int level) {
        return "§7Ore: §c+" + HEAL[level - 1] + "♥§7 heal + §a+" + FOOD[level - 1] + "§7 hunger."; }
}
