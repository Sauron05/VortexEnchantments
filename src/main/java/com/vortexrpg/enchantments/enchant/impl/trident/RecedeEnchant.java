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

/** Recede: On hit, removes water sources in 4/5/6 block radius for 8/10/12 seconds, then restores. */
public class RecedeEnchant extends VortexEnchant {
    private static final double[] RADIUS = {4, 5, 6};
    private static final int[] DURATION = {8, 10, 12};

    public RecedeEnchant() { super("recede", "Recede", EnchantRarity.EPIC, 3, List.of(ItemTarget.TRIDENT)); }

    private void apply(LivingEntity target, Player thrower, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(thrower)) return;
        setCooldownSeconds(thrower, 10);
        double radius = cfg("drain_radius", RADIUS[level-1]);
        int duration = cfgi("drain_duration_" + level, DURATION[level-1]);
        Location center = target.getLocation();
        List<Location> drained = new ArrayList<>();
        int r = (int) radius;
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    Block b = center.clone().add(x, y, z).getBlock();
                    if (b.getType() == Material.WATER) {
                        drained.add(b.getLocation().clone());
                        b.setType(Material.AIR);
                    }
                }
            }
        }
        new BukkitRunnable() {
            @Override public void run() {
                for (Location loc : drained) {
                    if (loc.getBlock().getType() == Material.AIR) loc.getBlock().setType(Material.WATER);
                }
            }
        }.runTaskLater(plugin, duration * 20L);
    }

    @Override
    public void onTridentHit(EntityDamageByEntityEvent event, Player thrower, LivingEntity target, int level) {
        apply(target, thrower, level);
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity target, int level) {
        apply(target, attacker, level);
    }

    @Override public String getDescription() { return "Drains water near hit target temporarily."; }
    @Override public String getDescription(int level) {
        return "§7Hit: removes water in §a" + (int)RADIUS[level-1] + " block§7 radius for §e" + DURATION[level-1] + "s§7."; }
}
