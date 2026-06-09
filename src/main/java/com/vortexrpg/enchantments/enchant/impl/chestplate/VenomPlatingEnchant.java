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
 * Venom Plating: When hit, attacker gets Poison.
 */
public class VenomPlatingEnchant extends VortexEnchant {
    public VenomPlatingEnchant() { super("venom_plating", "Venom Plating", EnchantRarity.EPIC, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (!(attacker instanceof LivingEntity living)) return;
        int dur = cfgi("duration", 40 + level * 20);
        int amp = cfgi("amplifier", level - 1);
        living.addPotionEffect(new PotionEffect(PotionEffectType.POISON, dur, amp, false, true, true));
    }

    @Override public String getDescription(int level) {
        return "§7Attackers receive §2Poison " + level + " §7for " + (2 + level) + "s.";
    }
}
