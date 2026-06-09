package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Gladiator: Deals bonus damage when fighting 1v1 (no other players within 15 blocks).
 * Bonus: +30/45/60%.
 */
public class GladiatorEnchant extends VortexEnchant {

    public GladiatorEnchant() {
        super("gladiator", "Gladiator", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double checkRadius = cfgd("isolation_radius", 15.0);
        long nearbyPlayers = attacker.getLocation().getNearbyPlayers(checkRadius).stream()
                .filter(p -> !p.equals(attacker) && !p.equals(victim))
                .count();

        if (nearbyPlayers > 0) return;

        double bonus = cfgd("bonus", 0.15 + level * 0.15);
        event.setDamage(event.getDamage() * (1 + bonus));

        ParticleUtil.spawn(attacker.getLocation().add(0, 2, 0), Particle.ENCHANTED_HIT, 8, 0.3);
        SoundUtil.play(attacker.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 0.6f, 0.8f);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.15 + level * 0.15) * 100);
        return "§7Deal §c+" + pct + "% §7damage when fighting alone (no players within 15 blocks).";
    }
}
