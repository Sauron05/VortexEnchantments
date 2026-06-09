package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Apex: The fewer enemies nearby, the more damage you deal.
 * Perfect for 1v1 duels. 1 enemy = +50/60/70% damage.
 * More enemies reduce the bonus.
 */
public class ApexEnchant extends VortexEnchant {

    public ApexEnchant() {
        super("apex", "Apex", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double scanRadius = cfgd("scan_radius", 10.0);
        double maxBonus = cfgd("max_bonus", 0.4 + level * 0.1);

        int nearbyEnemies = 0;
        for (Entity e : attacker.getNearbyEntities(scanRadius, scanRadius, scanRadius)) {
            if (e instanceof LivingEntity && !(e instanceof Player p && p.equals(attacker))) {
                if (e instanceof org.bukkit.entity.Monster || (e instanceof Player && !e.equals(victim))) {
                    nearbyEnemies++;
                }
            }
        }

        double bonus;
        if (nearbyEnemies <= 1) {
            bonus = maxBonus;
        } else if (nearbyEnemies <= 3) {
            bonus = maxBonus * 0.5;
        } else {
            bonus = 0;
        }

        if (bonus > 0) {
            event.setDamage(event.getDamage() * (1.0 + bonus));
        }
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.4 + level * 0.1) * 100);
        return "§7Fewer enemies nearby = more damage. Solo: §c+" + pct + "%§7. Crowded: no bonus.";
    }
}
