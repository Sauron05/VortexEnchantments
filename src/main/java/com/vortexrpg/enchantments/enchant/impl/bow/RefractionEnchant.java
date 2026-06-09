package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Refraction: Missed arrows bounce toward nearest entity within 6/8/10 blocks.
 */
public class RefractionEnchant extends VortexEnchant {

    private static final double[] DETECTION_RADIUS = {6.0, 8.0, 10.0};

    public RefractionEnchant() {
        super("refraction", "Refraction", EnchantRarity.RARE, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitBlock(ProjectileHitEvent event, Player shooter, int level) {
        if (!isEnabled()) return;
        if (!(event.getEntity() instanceof Arrow arrow)) return;
        double radius = cfg("detection_radius", DETECTION_RADIUS[level - 1]);
        double pct = cfg("bounce_damage_percent", 0.50);

        LivingEntity nearest = MathUtil.getNearestLiving(arrow.getLocation(), radius, e -> !e.equals(shooter));
        if (nearest == null) return;

        Arrow bounceArrow = arrow.getWorld().spawn(arrow.getLocation(), Arrow.class);
        bounceArrow.setShooter(shooter);
        bounceArrow.setDamage(arrow.getDamage() * pct);

        Vector dir = nearest.getLocation().add(0, nearest.getHeight() / 2.0, 0)
            .subtract(arrow.getLocation().toVector()).toVector().normalize().multiply(2.0);
        bounceArrow.setVelocity(dir);
    }

    @Override
    public String getDescription() { return "Arrows that miss deflect toward a nearby enemy."; }

    @Override
    public String getDescription(int level) {
        return "§7Missed arrows §ebounce§7 to nearest enemy within §e" + DETECTION_RADIUS[level-1] + "§7 blocks.";
    }
}
