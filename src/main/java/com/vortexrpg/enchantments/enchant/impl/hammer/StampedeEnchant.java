package com.vortexrpg.enchantments.enchant.impl.hammer;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Stampede: While sprinting, deal 20/35/50% bonus damage + extra knockback.
 * Momentum-based hammering.
 */
public class StampedeEnchant extends VortexEnchant {

    public StampedeEnchant() {
        super("stampede", "Stampede", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (!attacker.isSprinting()) return;

        double bonus = cfgd("bonus", 0.05 + level * 0.15);
        event.setDamage(event.getDamage() * (1.0 + bonus));

        Vector push = attacker.getLocation().getDirection().normalize().multiply(1.5).setY(0.3);
        victim.setVelocity(push);

        ParticleUtil.spawn(victim.getLocation(), Particle.CLOUD, 8, 0.4);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.05 + level * 0.15) * 100);
        return "§7Sprint: §c+" + pct + "% §7damage + extra knockback.";
    }
}
