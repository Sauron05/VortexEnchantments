package com.vortexrpg.enchantments.enchant.impl.hammer;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Hammerfall: When the player is falling, hit creates an area-of-effect landing
 * and negates fall damage.
 */
public class HammerfallEnchant extends VortexEnchant {

    public HammerfallEnchant() {
        super("hammerfall", "Hammerfall", EnchantRarity.RARE, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (attacker.getFallDistance() < 1.5f) return;

        double radius = cfgd("radius", 1.5 + level);
        double damage = cfgd("aoe_damage", 2.0 + level * 2.0);

        for (LivingEntity e : MathUtil.getNearbyLiving(victim.getLocation(), radius)) {
            if (e.equals(attacker) || e.equals(victim)) continue;
            e.damage(damage, attacker);
        }

        attacker.setFallDistance(0);
        ParticleUtil.drawCircle(victim.getLocation(), radius, 16, Particle.CAMPFIRE_SIGNAL_SMOKE);
        SoundUtil.play(victim.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.8f, 1.0f);
    }

    @Override
    public String getDescription(int level) {
        int r = (int) (1.5 + level);
        double d = 2 + level * 2;
        return "§7Falling hit: §cAoE " + d + " dmg §7in " + r + " blocks + no fall damage.";
    }
}
