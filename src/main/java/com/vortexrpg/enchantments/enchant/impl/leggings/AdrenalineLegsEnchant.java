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
 * Adrenaline Legs: When hit below 40% HP, gain Strength boost.
 */
public class AdrenalineLegsEnchant extends VortexEnchant {
    public AdrenalineLegsEnchant() { super("adrenaline_legs", "Adrenaline Legs", EnchantRarity.RARE, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        double maxHp = victim.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        double threshold = cfgd("hp_threshold", 0.40);
        if (victim.getHealth() / maxHp > threshold) return;
        int dur = cfgi("duration", 40 + level * 20);
        victim.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, dur, level - 1, true, false, true));
    }

    @Override public String getDescription(int level) {
        return "§7Below 40% HP: gain §cStrength " + level + " §7for " + (2 + level) + "s.";
    }
}
