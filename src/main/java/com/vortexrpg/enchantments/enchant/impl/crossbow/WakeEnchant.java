package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Wake: Bolt leaves a wind trail; entities walking through are pushed in the bolt's direction.
 */
public class WakeEnchant extends VortexEnchant {
    public WakeEnchant() { super("wake", "Wake", EnchantRarity.RARE, 3, List.of(ItemTarget.CROSSBOW)); }

    @Override
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;
        if (!(event.getProjectile() instanceof AbstractArrow arrow)) return;
        double pushForce = cfg("push_force_" + level, 0.3 + level * 0.1);
        List<Location> trail = new ArrayList<>();

        new BukkitRunnable() {
            int ticks = 0;
            @Override public void run() {
                if (!arrow.isValid() || arrow.isOnGround() || ticks++ > 100) { cancel(); return; }
                trail.add(arrow.getLocation().clone());
                // Push entities near recent trail positions
                Vector dir = arrow.getVelocity().normalize();
                for (Location trailLoc : trail) {
                    for (LivingEntity nearby : MathUtil.getNearbyLiving(trailLoc, 1.5)) {
                        if (nearby.equals(shooter)) continue;
                        nearby.setVelocity(nearby.getVelocity().add(dir.clone().multiply(pushForce)));
                    }
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    @Override public String getDescription() { return "Bolt trail pushes entities in its path."; }
    @Override public String getDescription(int level) {
        return "§7Bolt leaves a wind trail that §bpushes §7entities along its path."; }
}
