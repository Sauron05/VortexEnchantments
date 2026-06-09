package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Fell: Critical hits launch a projectile forward dealing 2/3/4♥ to entities in path.
 */
@SuppressWarnings("deprecation")
public class FellEnchant extends VortexEnchant {

    private static final double[] PROJECTILE_DAMAGE = {4.0, 6.0, 8.0};
    private static final double[] RANGE_BLOCKS = {3.0, 4.0, 5.0};

    public FellEnchant() {
        super("fell", "Fell", EnchantRarity.EPIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        boolean critOnly = cfgb("crit_only", true);
        if (critOnly && !(!attacker.isOnGround() && attacker.getFallDistance() > 0)) return;

        double range = cfg("range_blocks", RANGE_BLOCKS[level - 1]);
        double damage = cfg("projectile_damage", PROJECTILE_DAMAGE[level - 1]);

        // Deal damage to all entities in facing direction
        var dir = attacker.getLocation().getDirection().normalize();
        var start = attacker.getEyeLocation();
        for (LivingEntity entity : MathUtil.getNearbyLiving(attacker.getLocation(), range)) {
            if (entity.equals(attacker)) continue;
            // Check if entity is roughly in front
            var toEntity = entity.getLocation().subtract(start.toVector()).toVector().normalize();
            double dot = dir.dot(toEntity);
            if (dot > 0.7 && !entity.equals(victim)) {
                entity.damage(damage, attacker);
            }
        }
    }

    @Override
    public String getDescription() { return "Critical hits launch a log-shaped wave at enemies ahead."; }

    @Override
    public String getDescription(int level) {
        return "§7Crits: deal §c" + (int)(PROJECTILE_DAMAGE[level-1]/2) + "♥§7 to enemies in front (§e" + RANGE_BLOCKS[level-1] + " blocks§7).";
    }
}
