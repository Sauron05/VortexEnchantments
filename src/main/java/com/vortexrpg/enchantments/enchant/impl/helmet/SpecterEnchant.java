package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Specter: 10/15/20% chance to dodge melee attacks (damage set to 0).
 */
public class SpecterEnchant extends VortexEnchant {
    public SpecterEnchant() { super("specter", "Specter", EnchantRarity.RARE, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (attacker instanceof org.bukkit.entity.Projectile) return;
        double chance = cfgd("dodge_chance", 0.05 + level * 0.05);
        if (Math.random() < chance) {
            event.setCancelled(true);
            com.vortexrpg.enchantments.util.ParticleUtil.spawn(
                    victim.getLocation().add(0, 1, 0), org.bukkit.Particle.POOF, 8, 0.4);
        }
    }

    @Override public String getDescription(int level) {
        int pct = (int) ((0.05 + level * 0.05) * 100);
        return "§a" + pct + "%§7 chance to dodge melee attacks.";
    }
}
