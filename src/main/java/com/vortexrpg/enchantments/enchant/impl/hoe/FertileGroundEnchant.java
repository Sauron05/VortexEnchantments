package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/** Fertile Ground: Tilling creates enriched farmland that grows crops faster. */
public class FertileGroundEnchant extends VortexEnchant {

    public FertileGroundEnchant() { super("fertile_ground", "Fertile Ground", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (block.getType() != Material.DIRT && block.getType() != Material.GRASS_BLOCK) return;
        // Apply bone meal to nearby crops after tilling
        int radius = cfgi("radius", level);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block crop = block.getRelative(x, 1, z);
                if (crop.getBlockData() instanceof org.bukkit.block.data.Ageable) {
                    crop.applyBoneMeal(org.bukkit.block.BlockFace.UP);
                }
            }
        }
        ParticleUtil.burst(block.getLocation().add(0.5, 1, 0.5), Particle.HAPPY_VILLAGER, 8, 1.0);
    }

    @Override public String getDescription() { return "Tilling fertilizes nearby crops."; }
    @Override public String getDescription(int level) {
        return "§7Till: fertilize crops in §e" + level + "§7 block radius."; }
}
