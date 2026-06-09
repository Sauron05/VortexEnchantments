package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Crucible: Impact creates 2/3/4 block magma zone lasting 3/4/5s. */
public class CrucibleEnchant extends VortexEnchant {
    private static final int[] RADIUS = {2, 3, 4};
    private static final int[] DURATION = {3, 4, 5};
    public CrucibleEnchant() { super("crucible", "Crucible", EnchantRarity.EPIC, 3, List.of(ItemTarget.CROSSBOW)); }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        spawnMagmaZone(victim.getLocation(), level);
    }

    @Override
    public void onArrowHitBlock(org.bukkit.event.entity.ProjectileHitEvent event, Player shooter, int level) {
        if (!isEnabled()) return;
        spawnMagmaZone(event.getEntity().getLocation(), level);
    }

    private void spawnMagmaZone(Location center, int level) {
        int radius = cfgi("radius", RADIUS[level-1]);
        int durationTicks = cfgi("duration_seconds", DURATION[level-1]) * 20;
        for (LivingEntity e : MathUtil.getNearbyLiving(center, radius)) {
            if (!(e instanceof org.bukkit.entity.Player)) {
                e.setFireTicks(durationTicks);
            }
        }
        // Area damage
        MathUtil.getNearbyLiving(center, radius).forEach(e -> {
            if (e.getFireTicks() <= 0) e.setFireTicks(durationTicks);
        });
    }

    @Override public String getDescription() { return "Impact creates a burning magma zone."; }
    @Override public String getDescription(int level) {
        return "§7Impact: §c" + RADIUS[level-1] + "§7-block fire zone for §e" + DURATION[level-1] + "s§7."; }
}
