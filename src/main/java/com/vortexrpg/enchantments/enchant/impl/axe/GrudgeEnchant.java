package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Grudge: Tracks damage received from specific entities. Next hit on that entity = +1%/1.5%/2% per half-heart dealt.
 */
public class GrudgeEnchant extends VortexEnchant {

    private static final double[] BONUS_PER_HALF_HEART = {0.01, 0.015, 0.02};

    public GrudgeEnchant() {
        super("grudge", "Grudge", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        var pdm = plugin.getPlayerDataManager();
        double damageReceived = pdm.getGrudgeDamage(attacker.getUniqueId(), victim.getUniqueId());
        if (damageReceived <= 0) return;

        double halfHearts = damageReceived / 0.5;
        double maxBonusPct = cfg("max_bonus_percent", 100.0);
        double bonus = Math.min(halfHearts * cfg("bonus_per_half_heart", BONUS_PER_HALF_HEART[level-1]), maxBonusPct / 100.0);
        event.setDamage(event.getDamage() * (1.0 + bonus));

        if (cfgb("reset_on_hit", true)) {
            pdm.clearGrudgeDamage(attacker.getUniqueId(), victim.getUniqueId());
        }
    }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (!(attacker instanceof LivingEntity)) return;
        plugin.getPlayerDataManager().addGrudgeDamage(victim.getUniqueId(), attacker.getUniqueId(), event.getFinalDamage());
    }

    @Override
    public String getDescription() { return "The more damage a foe deals to you, the harder your revenge hit."; }

    @Override
    public String getDescription(int level) {
        return "§7+§e" + (int)(BONUS_PER_HALF_HEART[level-1]*100) + "%§7 damage per §c½♥§7 the enemy dealt you. Resets on hit.";
    }
}
