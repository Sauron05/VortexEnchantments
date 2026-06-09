package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Pollinate: Harvest has 15/20/25% to advance identical crops within 4 blocks by 1 stage. */
public class PollinateEnchant extends VortexEnchant {
    private static final double[] CHANCE = {15, 20, 25};

    public PollinateEnchant() { super("pollinate", "Pollinate", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onHarvest(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level-1]))) return;
        Location center = event.getBlock().getLocation();
        int radius = cfgi("radius", 4);
        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block b = center.clone().add(x, y, z).getBlock();
                    if (b.getType() != event.getBlock().getType()) continue;
                    if (!(b.getBlockData() instanceof Ageable ageable)) continue;
                    if (ageable.getAge() >= ageable.getMaximumAge()) continue;
                    ageable.setAge(ageable.getAge() + 1);
                    b.setBlockData(ageable);
                }
            }
        }
    }

    @Override public String getDescription() { return "Harvest may grow nearby identical crops."; }
    @Override public String getDescription(int level) {
        return "§a" + (int)CHANCE[level-1] + "%§7 harvest: identical crops within §e4 blocks§7 advance §11 stage§7."; }
}
