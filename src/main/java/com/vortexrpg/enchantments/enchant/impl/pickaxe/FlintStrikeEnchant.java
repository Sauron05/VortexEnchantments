package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Flint Strike: Chance to ignite nearby mob when mining. */
public class FlintStrikeEnchant extends VortexEnchant {
    private static final double[] CHANCE = {3, 5, 8};

    public FlintStrikeEnchant() { super("flint_strike", "Flint Strike", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level - 1]))) return;
        double radius = cfg("radius", 4.0);
        for (LivingEntity e : MathUtil.getNearbyLiving(event.getBlock().getLocation(), radius)) {
            if (e.equals(player)) continue;
            e.setFireTicks(cfgi("fire_ticks", 40 + level * 20));
            break; // ignite nearest only
        }
    }

    @Override public String getDescription() { return "Mining may ignite nearby mobs."; }
    @Override public String getDescription(int level) {
        return "§7Mining: §a" + (int) CHANCE[level - 1] + "%§7 to §6ignite§7 nearest mob."; }
}
