package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Entangle: Attackers get Slowness when they hit you.
 */
public class EntangleEnchant extends VortexEnchant {
    public EntangleEnchant() { super("entangle", "Entangle", EnchantRarity.RARE, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (!(attacker instanceof LivingEntity living)) return;
        int dur = cfgi("duration", 20 + level * 20);
        int amp = cfgi("amplifier", level - 1);
        living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, dur, amp, false, false, true));
    }

    @Override public String getDescription(int level) {
        return "§7Attackers receive §9Slowness " + level + " §7for " + (1 + level) + "s.";
    }
}
