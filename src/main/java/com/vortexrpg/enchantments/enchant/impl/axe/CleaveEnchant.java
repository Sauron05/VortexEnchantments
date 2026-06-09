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
 * Cleave: 60%/60%/60% to target, 40% split to all within 2/3/4 blocks AOE.
 */
public class CleaveEnchant extends VortexEnchant {

    private static final double[] RADIUS = {2.0, 3.0, 4.0};

    public CleaveEnchant() {
        super("cleave", "Cleave", EnchantRarity.RARE, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double radius = cfg("radius", RADIUS[level - 1]);
        double primaryPct = cfg("primary_percent", 0.60);
        double aoePct = cfg("aoe_percent", 0.40);

        double totalDamage = event.getDamage();
        event.setDamage(totalDamage * primaryPct);

        double aoeDamage = totalDamage * aoePct;
        for (LivingEntity nearby : MathUtil.getNearbyLiving(victim.getLocation(), radius)) {
            if (nearby.equals(victim) || nearby.equals(attacker)) continue;
            nearby.damage(aoeDamage, attacker);
        }
    }

    @Override
    public String getDescription() { return "Damage splits between the target and all nearby enemies."; }

    @Override
    public String getDescription(int level) {
        return "§760%§7 to target, §c40%§7 AOE damage within §e" + RADIUS[level-1] + " blocks§7.";
    }
}
