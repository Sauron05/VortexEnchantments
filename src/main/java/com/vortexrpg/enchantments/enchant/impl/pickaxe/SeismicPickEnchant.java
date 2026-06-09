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
import java.util.Set;

/** Seismic Pick: Breaking stone reveals nearby ores with glow particles. */
public class SeismicPickEnchant extends VortexEnchant {
    private static final int[] RADIUS = {4, 6, 8};
    private static final Set<Material> TRIGGER = Set.of(Material.STONE, Material.DEEPSLATE);

    public SeismicPickEnchant() { super("seismic_pick", "Seismic Pick", EnchantRarity.RARE, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!TRIGGER.contains(event.getBlock().getType())) return;
        int radius = cfgi("radius", RADIUS[level - 1]);
        Location center = event.getBlock().getLocation();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block b = center.getBlock().getRelative(x, y, z);
                    if (b.getType().name().endsWith("_ORE")) {
                        ParticleUtil.spawn(b.getLocation().add(0.5, 1.2, 0.5), Particle.ENCHANTED_HIT, 5, 0.1);
                    }
                }
            }
        }
    }

    @Override public String getDescription() { return "Stone mining pulses to reveal nearby ores."; }
    @Override public String getDescription(int level) {
        return "§7Stone: ores glow within §a" + RADIUS[level - 1] + " blocks§7."; }
}
