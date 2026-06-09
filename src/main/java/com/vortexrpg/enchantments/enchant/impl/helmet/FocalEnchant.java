package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Focal: Damage bonus when only one enemy is within 10 blocks (duelist bonus).
 */
public class FocalEnchant extends VortexEnchant {
    public FocalEnchant() { super("focal", "Focal", EnchantRarity.RARE, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double radius = cfgd("radius", 10.0);
        long hostileCount = attacker.getWorld().getNearbyLivingEntities(attacker.getLocation(), radius)
                .stream()
                .filter(e -> !e.equals(attacker) && e instanceof org.bukkit.entity.Monster)
                .count();
        if (hostileCount == 1) {
            double bonus = cfgd("bonus_pct", 0.10 + level * 0.10);
            event.setDamage(event.getDamage() * (1.0 + bonus));
        }
    }

    @Override public String getDescription(int level) {
        int pct = (int) ((0.10 + level * 0.10) * 100);
        return "§7If only one enemy nearby: §a+" + pct + "% §7damage (duelist).";
    }
}
