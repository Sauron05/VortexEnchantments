package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * FortifiedAnkle: Grants Resistance after taking any damage.
 */
public class FortifiedAnkleEnchant extends VortexEnchant {
    public FortifiedAnkleEnchant() { super("fortified_ankle", "Fortified Ankle", EnchantRarity.EPIC, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(player)) return;
        int dur = cfgi("duration", 20 + level * 20);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, dur, level - 1, true, false, true));
        setCooldownFromConfig(player, "cooldown", 10.0);
    }

    @Override public String getDescription(int level) {
        return "§7After taking damage: §bResistance " + level + " §7for " + (1 + level) + "s. §810s CD.";
    }
}
