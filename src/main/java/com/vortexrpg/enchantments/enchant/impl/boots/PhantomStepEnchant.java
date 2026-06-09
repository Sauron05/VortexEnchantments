package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * PhantomStep: Chance to completely dodge melee attacks.
 */
public class PhantomStepEnchant extends VortexEnchant {
    public PhantomStepEnchant() { super("phantom_step", "Phantom Step", EnchantRarity.EPIC, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (event.getCause() != EntityDamageByEntityEvent.DamageCause.ENTITY_ATTACK) return;
        double chance = cfgd("dodge_chance", 0.05 * level);
        if (Math.random() >= chance) return;
        event.setCancelled(true);
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 12, 0.5);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.4f, 1.8f);
    }

    @Override public String getDescription(int level) {
        return "§7" + (5 * level) + "% §7chance to dodge melee attacks.";
    }
}
