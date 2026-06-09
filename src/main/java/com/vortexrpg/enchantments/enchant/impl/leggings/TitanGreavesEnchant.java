package com.vortexrpg.enchantments.enchant.impl.leggings;

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
 * TitanGreaves: Gain Resistance when taking damage from entities while sneaking.
 */
public class TitanGreavesEnchant extends VortexEnchant {
    public TitanGreavesEnchant() { super("titan_greaves", "Titan Greaves", EnchantRarity.EPIC, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (!victim.isSneaking()) return;
        int dur = cfgi("duration", 40 + level * 20);
        victim.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, dur, level - 1, true, false, true));
    }

    @Override public String getDescription(int level) {
        return "§7While sneaking: taking damage grants §bResistance " + level + " §7for " + (2 + level) + "s.";
    }
}
