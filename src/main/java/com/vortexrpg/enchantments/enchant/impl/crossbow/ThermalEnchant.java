package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Thermal: Impact applies Glowing to all entities within 8/10/12 blocks for 3/4/5 seconds.
 */
public class ThermalEnchant extends VortexEnchant {
    private static final double[] RADIUS = {8, 10, 12};
    private static final int[] DURATION = {3, 4, 5};

    public ThermalEnchant() { super("thermal", "Thermal", EnchantRarity.EPIC, 3, List.of(ItemTarget.CROSSBOW)); }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity target, int level) {
        applyGlow(target.getLocation(), shooter, level);
    }

    private void applyGlow(Location loc, Player shooter, int level) {
        if (!isEnabled()) return;
        double radius = cfg("radius_" + level, RADIUS[level-1]);
        int durationTicks = (int)(cfg("duration_" + level, DURATION[level-1]) * 20);
        for (LivingEntity nearby : MathUtil.getNearbyLiving(loc, radius)) {
            if (nearby.equals(shooter)) continue;
            nearby.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, durationTicks, 0, false, false));
        }
    }

    @Override public String getDescription() { return "Impact reveals nearby entities through walls."; }
    @Override public String getDescription(int level) {
        return "§7Impact: §eGlowing§7 on entities within §a" + (int)RADIUS[level-1] + " blocks§7 for §e" + DURATION[level-1] + "s§7."; }
}
