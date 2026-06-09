package com.vortexrpg.enchantments.enchant.impl.chestplate;

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
 * Temporal Plate: Chance to negate a hit entirely. Has a cooldown.
 */
public class TemporalPlateEnchant extends VortexEnchant {
    public TemporalPlateEnchant() { super("temporal_plate", "Temporal Plate", EnchantRarity.EPIC, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(victim)) return;
        double chance = cfgd("negate_chance", 0.08 * level);
        if (Math.random() >= chance) return;
        event.setCancelled(true);
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.REVERSE_PORTAL, 15, 0.5);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 1.5f);
        setCooldownFromConfig(victim, "cooldown", 10.0);
    }

    @Override public String getDescription(int level) {
        return "§7" + (8 * level) + "% §7chance to negate an attack. §810s CD.";
    }
}
