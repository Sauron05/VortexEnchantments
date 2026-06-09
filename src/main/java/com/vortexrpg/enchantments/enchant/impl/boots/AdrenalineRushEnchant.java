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
 * AdrenalineRush: After taking damage, gain Speed and Strength briefly.
 */
public class AdrenalineRushEnchant extends VortexEnchant {
    public AdrenalineRushEnchant() { super("adrenaline_rush", "Adrenaline Rush", EnchantRarity.EPIC, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(player)) return;
        double dmgThreshold = cfgd("damage_threshold", 4.0);
        if (event.getFinalDamage() < dmgThreshold) return;
        int dur = cfgi("duration", 40 + level * 20);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, dur, level - 1, true, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, dur, 0, true, false, true));
        setCooldownFromConfig(player, "cooldown", 15.0);
    }

    @Override public String getDescription(int level) {
        return "§7Heavy hit: gain §bSpeed " + level + " §7+ §cStrength §7for " + (2 + level) + "s. §815s CD.";
    }
}
