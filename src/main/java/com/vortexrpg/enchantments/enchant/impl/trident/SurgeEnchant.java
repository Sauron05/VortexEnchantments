package com.vortexrpg.enchantments.enchant.impl.trident;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

/**
 * Surge: Kill with throw charges next melee attack +80/90/100%. Kill with melee charges next throw similarly.
 */
public class SurgeEnchant extends VortexEnchant {
    private static final double[] BONUS = {0.80, 0.90, 1.00};

    public SurgeEnchant() { super("surge", "Surge", EnchantRarity.EPIC, 3, List.of(ItemTarget.TRIDENT)); }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, int level) {
        if (!isEnabled()) return;
        Entity lastDamager = event.getEntity().getKiller();
        if (lastDamager == null) return;
        // Check if killed by trident throw vs melee by checking last damage cause
        // Since both route through here, use PDM flags
        boolean wasThrow = plugin.getPlayerDataManager().getInt(killer.getUniqueId(), "surge_last_trident_throw") == 1;
        if (wasThrow) {
            plugin.getPlayerDataManager().setInt(killer.getUniqueId(), "surge_melee_charged", 1);
            plugin.getPlayerDataManager().setInt(killer.getUniqueId(), "surge_last_trident_throw", 0);
        } else {
            plugin.getPlayerDataManager().setInt(killer.getUniqueId(), "surge_throw_charged", 1);
        }
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity target, int level) {
        if (!isEnabled()) return;
        if (plugin.getPlayerDataManager().getInt(attacker.getUniqueId(), "surge_melee_charged") == 1) {
            event.setDamage(event.getDamage() * (1.0 + BONUS[level-1]));
            plugin.getPlayerDataManager().setInt(attacker.getUniqueId(), "surge_melee_charged", 0);
        }
    }

    @Override
    public void onTridentHit(EntityDamageByEntityEvent event, Player thrower, LivingEntity target, int level) {
        if (!isEnabled()) return;
        plugin.getPlayerDataManager().setInt(thrower.getUniqueId(), "surge_last_trident_throw", 1);
        if (plugin.getPlayerDataManager().getInt(thrower.getUniqueId(), "surge_throw_charged") == 1) {
            event.setDamage(event.getDamage() * (1.0 + BONUS[level-1]));
            plugin.getPlayerDataManager().setInt(thrower.getUniqueId(), "surge_throw_charged", 0);
        }
    }

    @Override public String getDescription() { return "Kills charge your next attack type with extra damage."; }
    @Override public String getDescription(int level) {
        return "§7Kill with throw: next melee §c+" + (int)(BONUS[level-1]*100) + "%§7. Kill with melee: next throw §c+" + (int)(BONUS[level-1]*100) + "%§7."; }
}
