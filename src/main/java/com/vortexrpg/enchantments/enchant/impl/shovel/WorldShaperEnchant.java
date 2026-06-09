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

/** World Shaper: Mine all soft blocks in a 5-block radius sphere. */
public class WorldShaperEnchant extends VortexEnchant {

    public WorldShaperEnchant() { super("world_shaper", "World Shaper", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(player)) return;
        int radius = cfgi("radius", 3 + level);
        Block center = event.getBlock();
        Location loc = center.getLocation().add(0.5, 0.5, 0.5);
        int broken = 0;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z > radius * radius) continue;
                    Block b = center.getRelative(x, y, z);
                    if (b.equals(center)) continue;
                    if (isSoftBlock(b.getType())) {
                        b.breakNaturally(player.getInventory().getItemInMainHand());
                        broken++;
                    }
                }
            }
        }
        if (broken > 0) {
            SoundUtil.play(loc, Sound.ENTITY_WARDEN_SONIC_BOOM, 0.8f, 1.2f);
            ParticleUtil.burst(loc, Particle.EXPLOSION, 5, radius);
            setCooldownFromConfig(player, "cooldown", 15);
        }
    }

    private boolean isSoftBlock(Material mat) {
        return mat == Material.DIRT || mat == Material.GRASS_BLOCK || mat == Material.SAND
                || mat == Material.RED_SAND || mat == Material.GRAVEL || mat == Material.CLAY
                || mat == Material.SOUL_SAND || mat == Material.SOUL_SOIL || mat == Material.MUD
                || mat == Material.SNOW_BLOCK || mat == Material.COARSE_DIRT || mat == Material.PODZOL
                || mat == Material.MYCELIUM || mat == Material.ROOTED_DIRT || mat == Material.FARMLAND
                || mat == Material.DIRT_PATH || mat == Material.SNOW || mat == Material.MUDDY_MANGROVE_ROOTS;
    }

    @Override public String getDescription() { return "Mine all soft blocks in a massive sphere."; }
    @Override public String getDescription(int level) {
        return "§7Breaks §eall§7 soft blocks in §6" + (3 + level) + "b§7 radius sphere."; }
}
