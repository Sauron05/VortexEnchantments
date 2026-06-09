package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/** Archon Shield: Block projectiles for all nearby allies in cone. */
public class ArchonShieldEnchant extends VortexEnchant {

    public ArchonShieldEnchant() { super("archon_shield", "Archon Shield", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        long tick = plugin.getServer().getCurrentTick();
        if (tick % 5 != 0) return;
        double radius = cfg("radius", 4.0 + level * 2);
        var dir = player.getLocation().getDirection().normalize();
        for (var entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof AbstractArrow arrow && !arrow.isOnGround()) {
                var toArrow = arrow.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                if (dir.angle(toArrow) < Math.PI / 3) {
                    // Check if arrow is heading toward an ally
                    for (LivingEntity ally : MathUtil.getNearbyLiving(arrow.getLocation(), 2.0)) {
                        if (ally instanceof Player p && !p.equals(player)) {
                            arrow.remove();
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override public String getDescription() { return "Block projectiles for allies in cone."; }
    @Override public String getDescription(int level) {
        return "§7Block: intercept projectiles near allies in §e" + (int)(4 + level * 2) + "b§7 cone."; }
}
