package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.*;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.List;

/**
 * Whistle: Impact sound aggros all mobs within 10/15/20 blocks for 8s.
 */
public class WhistleEnchant extends VortexEnchant {

    private static final double[] RADIUS = {10.0, 15.0, 20.0};

    public WhistleEnchant() {
        super("whistle", "Whistle", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitBlock(ProjectileHitEvent event, Player shooter, int level) {
        if (!isEnabled()) return;
        aggro(event.getEntity().getLocation(), shooter, level);
    }

    @Override
    public void onArrowHitEntity(org.bukkit.event.entity.EntityDamageByEntityEvent event,
                                 Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        aggro(victim.getLocation(), shooter, level);
    }

    private void aggro(org.bukkit.Location loc, Player shooter, int level) {
        double radius = cfg("aggro_radius", RADIUS[level - 1]);
        long durationTicks = cfgi("aggro_duration_seconds", 8) * 20L;

        for (LivingEntity entity : MathUtil.getNearbyLiving(loc, radius)) {
            if (entity instanceof Mob mob && !(entity instanceof Player)) {
                mob.setTarget(shooter);
            }
        }

        loc.getWorld().playSound(loc, org.bukkit.Sound.ENTITY_ARROW_HIT, 2.0f, 0.5f);

        // Clear aggro after duration
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            for (LivingEntity entity : MathUtil.getNearbyLiving(loc, radius)) {
                if (entity instanceof Mob mob && mob.getTarget() != null
                    && mob.getTarget().equals(shooter)) {
                    mob.setTarget(null);
                }
            }
        }, durationTicks);
    }

    @Override
    public String getDescription() { return "Impact aggros nearby mobs toward the hit location."; }

    @Override
    public String getDescription(int level) {
        return "§7Arrow impact aggros all mobs within §e" + RADIUS[level-1] + "§7 blocks for §e8s§7.";
    }
}
