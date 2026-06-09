package com.vortexrpg.enchantments.enchant.impl.leggings;

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
 * Entrap: Attackers get Weakness when they hit you.
 */
public class EntrapEnchant extends VortexEnchant {
    public EntrapEnchant() { super("entrap", "Entrap", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (!(attacker instanceof LivingEntity living)) return;
        int dur = cfgi("duration", 20 + level * 20);
        living.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, dur, level - 1, false, false, true));
    }

    @Override public String getDescription(int level) {
        return "§7Attackers receive §9Weakness " + level + " §7for " + (1 + level) + "s.";
    }
}
