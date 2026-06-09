package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * PursuitBoots: On hit, gain Speed if target is further than 4 blocks.
 */
public class PursuitBootsEnchant extends VortexEnchant {
    public PursuitBootsEnchant() { super("pursuit_boots", "Pursuit Boots", EnchantRarity.RARE, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double dist = attacker.getLocation().distance(victim.getLocation());
        if (dist < cfgd("distance_threshold", 4.0)) return;
        int dur = cfgi("duration", 30 + level * 10);
        attacker.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, dur, level - 1, true, false, true));
    }

    @Override public String getDescription(int level) {
        return "§7Hitting targets 4+ blocks away grants §bSpeed " + level + "§7.";
    }
}
