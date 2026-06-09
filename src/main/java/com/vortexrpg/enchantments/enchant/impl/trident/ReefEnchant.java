package com.vortexrpg.enchantments.enchant.impl.trident;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Reef: Creates slow zone at impact for 5 seconds (Slowness). */
public class ReefEnchant extends VortexEnchant {
    private static final double[] RADIUS = {3, 4, 5};
    private static final int[] DURATION = {5, 5, 5};

    public ReefEnchant() { super("reef", "Reef", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.TRIDENT)); }

    @Override
    public void onTridentHit(EntityDamageByEntityEvent event, Player thrower, LivingEntity target, int level) {
        apply(thrower, target, level);
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity target, int level) {
        apply(attacker, target, level);
    }

    private void apply(Player thrower, LivingEntity center, int level) {
        if (!isEnabled()) return;
        double radius = cfg("radius", RADIUS[level-1]);
        int dur = cfgi("duration_seconds", DURATION[level-1]);
        int slow = cfgi("slowness_level", 1);
        ParticleUtil.burst(center.getLocation(), Particle.FALLING_WATER, 30, (float)radius / 2);
        for (LivingEntity nearby : MathUtil.getNearbyLiving(center.getLocation(), radius)) {
            if (nearby.equals(thrower)) continue;
            nearby.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, dur * 20, slow - 1));
        }
    }

    @Override public String getDescription() { return "Impact creates a slow zone."; }
    @Override public String getDescription(int level) {
        return "§7Impact: §bSlow zone§7 of §a" + (int)RADIUS[level-1] + " blocks§7 radius for §e5s§7."; }
}
