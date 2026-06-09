package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Terra Nova: Mining replaces 3×3 area with random biome-themed blocks. */
public class TerraNovaEnchant extends VortexEnchant {

    private static final Material[] TERRA_BLOCKS = {
            Material.GRASS_BLOCK, Material.MOSS_BLOCK, Material.SAND,
            Material.RED_SAND, Material.PODZOL, Material.MYCELIUM,
            Material.CLAY, Material.MUD, Material.SNOW_BLOCK
    };

    public TerraNovaEnchant() { super("terra_nova", "Terra Nova", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(player)) return;
        int radius = cfgi("radius", 1 + level);
        Block center = event.getBlock();
        Location loc = center.getLocation().add(0.5, 0.5, 0.5);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block b = center.getRelative(x, 0, z);
                if (b.equals(center)) continue;
                if (!b.getType().isAir() && b.getType() != Material.BEDROCK) {
                    Material newMat = TERRA_BLOCKS[(int) (Math.random() * TERRA_BLOCKS.length)];
                    b.setType(newMat);
                }
            }
        }
        SoundUtil.play(loc, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 0.8f);
        ParticleUtil.burst(loc, Particle.HAPPY_VILLAGER, 30, radius + 1);
        setCooldownFromConfig(player, "cooldown", 10);
    }

    @Override public String getDescription() { return "Mining transforms the area with random biome blocks."; }
    @Override public String getDescription(int level) {
        return "§7Transforms §e" + (1 + level) + "×" + (1 + level) + "§7 area into random biome terrain."; }
}
