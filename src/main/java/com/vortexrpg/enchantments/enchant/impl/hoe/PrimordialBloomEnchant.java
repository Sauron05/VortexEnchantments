package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Primordial Bloom: Harvesting creates a spreading vegetation wave. */
public class PrimordialBloomEnchant extends VortexEnchant {

    public PrimordialBloomEnchant() { super("primordial_bloom", "Primordial Bloom", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!(event.getBlock().getBlockData() instanceof org.bukkit.block.data.Ageable)) return;
        if (isOnCooldown(player)) return;
        int radius = cfgi("radius", 4 + level * 2);
        Block center = event.getBlock();
        // Spread vegetation
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radius * radius) continue;
                Block ground = center.getRelative(x, -1, z);
                Block top = center.getRelative(x, 0, z);
                if (ground.getType() == Material.DIRT && top.getType().isAir()) {
                    ground.setType(Material.GRASS_BLOCK);
                }
                if ((ground.getType() == Material.GRASS_BLOCK || ground.getType() == Material.FARMLAND)
                        && top.getBlockData() instanceof org.bukkit.block.data.Ageable) {
                    for (int i = 0; i < level; i++) {
                        top.applyBoneMeal(BlockFace.UP);
                    }
                }
            }
        }
        SoundUtil.play(center.getLocation(), Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1.0f, 1.8f);
        ParticleUtil.burst(center.getLocation().add(0.5, 0.5, 0.5), Particle.HAPPY_VILLAGER, 60, radius);
        setCooldownFromConfig(player, "cooldown", 20);
    }

    @Override public String getDescription() { return "Harvesting creates vegetation wave."; }
    @Override public String getDescription(int level) {
        return "§7Harvest: §agreen wave§7 in " + (4 + level * 2) + "b radius, growing all crops."; }
}
