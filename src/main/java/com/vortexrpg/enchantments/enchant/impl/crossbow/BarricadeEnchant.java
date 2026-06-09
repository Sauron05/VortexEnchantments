package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Barricade: Creates a 1/2/3-wide, 3-tall barrier wall at the impact point for 2/3/4 seconds.
 */
public class BarricadeEnchant extends VortexEnchant {
    private static final int[] WIDTH = {1, 2, 3};
    private static final int[] DURATION = {2, 3, 4};

    public BarricadeEnchant() { super("barricade", "Barricade", EnchantRarity.RARE, 3, List.of(ItemTarget.CROSSBOW)); }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity target, int level) {
        placeWall(target.getLocation(), shooter, level);
    }

    private void placeWall(Location loc, Player shooter, int level) {
        if (!isEnabled()) return;
        int width = cfgi("width_" + level, WIDTH[level-1]);
        int duration = cfgi("duration_" + level, DURATION[level-1]);
        // Build perpendicular to shooter's yaw
        double yaw = Math.toRadians(shooter.getLocation().getYaw() + 90);
        Location base = loc.getBlock().getLocation();
        List<Block> placed = new ArrayList<>();
        for (int w = -(width/2); w <= (width/2); w++) {
            for (int h = 0; h < 3; h++) {
                Block b = base.clone().add(
                    Math.cos(yaw) * w, h, Math.sin(yaw) * w
                ).getBlock();
                if (b.getType() == Material.AIR) {
                    b.setType(Material.BARRIER);
                    placed.add(b);
                }
            }
        }
        SoundUtil.play(base, Sound.BLOCK_STONE_PLACE, 1f, 0.8f);
        new BukkitRunnable() {
            @Override public void run() {
                for (Block b : placed) {
                    if (b.getType() == Material.BARRIER) b.setType(Material.AIR);
                }
            }
        }.runTaskLater(plugin, duration * 20L);
    }

    @Override public String getDescription() { return "Bolts create a temporary barrier wall at impact."; }
    @Override public String getDescription(int level) {
        return "§7Impact: §b" + WIDTH[level-1] + "×3§7 barrier wall for §e" + DURATION[level-1] + "s§7."; }
}
