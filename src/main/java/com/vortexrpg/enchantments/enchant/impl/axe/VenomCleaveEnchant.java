package com.vortexrpg.enchantments.enchant.impl.axe;

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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * VenomCleave: Poison spreads to nearby enemies when hitting a poisoned target.
 * Spread radius: 3/4/5 blocks.
 */
public class VenomCleaveEnchant extends VortexEnchant {

    public VenomCleaveEnchant() {
        super("venomcleave", "Venom Cleave", EnchantRarity.EPIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int poisonTicks = cfgi("poison_ticks", 60 + level * 20);
        int poisonLevel = cfgi("poison_level", 0);

        victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, poisonTicks, poisonLevel, false, true));

        if (victim.hasPotionEffect(PotionEffectType.POISON)) {
            double spreadRadius = cfgd("spread_radius", 2.0 + level);
            for (LivingEntity nearby : MathUtil.getNearbyLiving(victim.getLocation(), spreadRadius)) {
                if (nearby.equals(attacker) || nearby.equals(victim)) continue;
                nearby.addPotionEffect(new PotionEffect(PotionEffectType.POISON, poisonTicks / 2, poisonLevel, false, true));
            }
            ParticleUtil.drawCircle(victim.getLocation(), spreadRadius, 12, Particle.ITEM_SLIME);
        }

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ITEM_SLIME, 8, 0.3);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_SPIDER_AMBIENT, 0.5f, 1.5f);
    }

    @Override
    public String getDescription(int level) {
        int r = (int) (2 + level);
        return "§7Attacks poison target. Hitting poisoned targets spreads poison within §e" + r + " blocks§7.";
    }
}
