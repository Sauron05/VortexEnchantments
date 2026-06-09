package com.vortexrpg.enchantments.enchant.impl.trident;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.util.Vector;

import java.util.List;

/** Undertow: On kill, pull all mobs within 6/7/8 blocks 3 blocks toward kill spot. */
public class UndertowEnchant extends VortexEnchant {
    private static final double[] RADIUS = {6, 7, 8};
    public UndertowEnchant() { super("undertow", "Undertow", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.TRIDENT)); }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, int level) {
        if (!isEnabled()) return;
        Location killLoc = event.getEntity().getLocation();
        double radius = cfg("pull_radius", RADIUS[level-1]);
        double dist = cfg("pull_distance", 3.0);
        for (LivingEntity nearby : MathUtil.getNearbyLiving(killLoc, radius)) {
            if (nearby.equals(killer)) continue;
            Vector pull = killLoc.toVector().subtract(nearby.getLocation().toVector()).normalize().multiply(dist);
            nearby.setVelocity(nearby.getVelocity().add(pull));
        }
    }

    @Override public String getDescription() { return "Kills pull nearby mobs toward the kill spot."; }
    @Override public String getDescription(int level) {
        return "§7Kill: pull all mobs within §a" + (int)RADIUS[level-1] + " blocks§7 §e3 blocks§7 toward kill spot."; }
}
