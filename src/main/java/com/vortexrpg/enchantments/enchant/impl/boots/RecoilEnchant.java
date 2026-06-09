package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Recoil: When hit, gain a brief Speed boost.
 */
public class RecoilEnchant extends VortexEnchant {
    public RecoilEnchant() { super("recoil", "Recoil", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        int dur = cfgi("duration", 20 + level * 10);
        victim.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, dur, level - 1, true, false, true));
    }

    @Override public String getDescription(int level) {
        return "§7When hit: gain §bSpeed " + level + " §7for " + (1 + level * 0.5) + "s.";
    }
}
