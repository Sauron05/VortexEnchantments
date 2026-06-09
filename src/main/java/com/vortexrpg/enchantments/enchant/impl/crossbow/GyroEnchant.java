package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Gyro: Bolt gently homes toward nearest entity within 2/2.5/3 blocks each tick.
 */
public class GyroEnchant extends VortexEnchant {
    private static final double[] RANGE = {2.0, 2.5, 3.0};

    public GyroEnchant() { super("gyro", "Gyro", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.CROSSBOW)); }

    @Override
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;
        if (!(event.getProjectile() instanceof AbstractArrow arrow)) return;
        double range = cfg("range_" + level, RANGE[level-1]);

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (!arrow.isValid() || arrow.isOnGround() || ticks++ > 100) { cancel(); return; }
                LivingEntity nearest = MathUtil.getNearestLiving(arrow.getLocation(), range);
                if (nearest == null || nearest.equals(shooter)) return;
                Vector toTarget = nearest.getEyeLocation().toVector().subtract(arrow.getLocation().toVector()).normalize();
                Vector vel = arrow.getVelocity();
                arrow.setVelocity(vel.add(toTarget.multiply(0.05)).normalize().multiply(vel.length()));
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    @Override public String getDescription() { return "Bolts gently curve toward nearby entities."; }
    @Override public String getDescription(int level) {
        return "§7Bolt homes toward nearest entity within §a" + RANGE[level-1] + " blocks."; }
}
