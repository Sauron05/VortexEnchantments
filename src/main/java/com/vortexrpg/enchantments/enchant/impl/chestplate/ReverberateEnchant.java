package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Reverberate: When taking a critical hit, emit AoE damage around you.
 */
public class ReverberateEnchant extends VortexEnchant {
    public ReverberateEnchant() { super("reverberate", "Reverberate", EnchantRarity.EPIC, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (!(attacker instanceof Player atkPlayer)) return;
        if (atkPlayer.getFallDistance() <= 0) return; // Only on crit
        if (isOnCooldown(victim)) return;

        double radius = cfgd("radius", 4.0 + level);
        double dmg = cfgd("aoe_damage", 2.0 * level);

        for (LivingEntity e : MathUtil.getNearbyLiving(victim.getLocation(), radius)) {
            if (e.equals(victim)) continue;
            e.damage(dmg, victim);
        }
        ParticleUtil.burst(victim.getLocation(), Particle.SONIC_BOOM, 1, 2.0);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.6f, 1.2f);
        setCooldownFromConfig(victim, "cooldown", 8.0);
    }

    @Override public String getDescription(int level) {
        return "§7On crit received: AoE §c" + (2 * level) + " §7damage in " + (int)(4 + level) + " blocks. §88s CD.";
    }
}
