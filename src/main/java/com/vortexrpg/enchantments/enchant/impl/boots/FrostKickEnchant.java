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
 * FrostKick: Melee attacks apply Slowness to targets.
 */
public class FrostKickEnchant extends VortexEnchant {
    public FrostKickEnchant() { super("frost_kick", "Frost Kick", EnchantRarity.RARE, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double chance = cfgd("chance", 0.15 * level);
        if (Math.random() >= chance) return;
        int dur = cfgi("duration", 30 + level * 10);
        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, dur, level - 1, true, false, true));
    }

    @Override public String getDescription(int level) {
        return "§7" + (15 * level) + "% §7chance to apply §bSlowness " + level + " §7on hit.";
    }
}
