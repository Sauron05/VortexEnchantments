package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Ore Scout: Breaking stone briefly reveals nearby ores with particles. */
public class OreScoutEnchant extends VortexEnchant {
    private static final int[] RADIUS = {6, 8, 10};

    public OreScoutEnchant() { super("ore_scout", "Ore Scout", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (mat != Material.STONE && mat != Material.DEEPSLATE) return;
        int radius = cfgi("radius", RADIUS[level - 1]);
        Location center = event.getBlock().getLocation();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block b = center.getBlock().getRelative(x, y, z);
                    if (b.getType().name().endsWith("_ORE")) {
                        ParticleUtil.spawn(b.getLocation().add(0.5, 0.5, 0.5), Particle.ENCHANTED_HIT, 3, 0.2);
                    }
                }
            }
        }
    }

    @Override public String getDescription() { return "Breaking stone reveals nearby ores."; }
    @Override public String getDescription(int level) {
        return "§7Stone: reveals ores within §a" + RADIUS[level - 1] + " blocks§7."; }
}
