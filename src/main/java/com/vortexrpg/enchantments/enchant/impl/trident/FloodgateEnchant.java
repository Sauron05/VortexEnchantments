package com.vortexrpg.enchantments.enchant.impl.trident;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/** Floodgate: Impact creates temporary 3x3 water source for 4/5/6 seconds even on land. */
public class FloodgateEnchant extends VortexEnchant {
    private static final int[] DURATION = {4, 5, 6};

    public FloodgateEnchant() { super("floodgate", "Floodgate", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.TRIDENT)); }

    @Override
    public void onTridentHit(EntityDamageByEntityEvent event, Player thrower, LivingEntity target, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(thrower)) return;
        setCooldownSeconds(thrower, cfgi("cooldown", 8));
        int radius = cfgi("water_radius", 1); // 3x3 = radius 1
        int duration = cfgi("duration_seconds_" + level, DURATION[level-1]);
        Location center = target.getLocation();
        List<Block> placed = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block b = center.clone().add(x, 0, z).getBlock();
                if (b.getType() == Material.AIR || b.getType() == Material.GRASS_BLOCK) {
                    b.setType(Material.WATER);
                    placed.add(b);
                }
            }
        }
        new BukkitRunnable() {
            @Override public void run() {
                for (Block b : placed) {
                    if (b.getType() == Material.WATER) b.setType(Material.AIR);
                }
            }
        }.runTaskLater(plugin, duration * 20L);
    }

    @Override public String getDescription() { return "Impact floods area with temporary water."; }
    @Override public String getDescription(int level) {
        return "§7Impact: §b3×3 water§7 zone for §e" + DURATION[level-1] + "s§7."; }
}
