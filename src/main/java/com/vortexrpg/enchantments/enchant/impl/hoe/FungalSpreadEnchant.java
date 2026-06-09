package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Fungal Spread: Breaking mushrooms spreads them to nearby mycelium/podzol. */
public class FungalSpreadEnchant extends VortexEnchant {

    public FungalSpreadEnchant() { super("fungal_spread", "Fungal Spread", EnchantRarity.RARE, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (mat != Material.RED_MUSHROOM && mat != Material.BROWN_MUSHROOM) return;
        int radius = cfgi("radius", 1 + level);
        int placed = 0;
        int max = cfgi("max-spread", level * 2);
        Block center = event.getBlock();
        for (int x = -radius; x <= radius && placed < max; x++) {
            for (int z = -radius; z <= radius && placed < max; z++) {
                Block ground = center.getRelative(x, -1, z);
                Block top = center.getRelative(x, 0, z);
                if (top.getType().isAir() && (ground.getType() == Material.MYCELIUM || ground.getType() == Material.PODZOL)) {
                    top.setType(mat);
                    placed++;
                }
            }
        }
        if (placed > 0) {
            ParticleUtil.burst(center.getLocation().add(0.5, 0.5, 0.5), Particle.SPORE_BLOSSOM_AIR, 15, radius);
        }
    }

    @Override public String getDescription() { return "Breaking mushrooms spreads them nearby."; }
    @Override public String getDescription(int level) {
        return "§7Mushrooms spread to §5" + level * 2 + "§7 nearby mycelium/podzol blocks."; }
}
