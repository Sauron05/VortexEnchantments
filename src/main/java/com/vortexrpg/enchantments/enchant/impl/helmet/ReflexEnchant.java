package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Reflex: When hit from behind, gain a brief Speed boost. */
public class ReflexEnchant extends VortexEnchant {
    public ReflexEnchant() { super("reflex", "Reflex", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        org.bukkit.util.Vector toAttacker = attacker.getLocation().toVector()
                .subtract(victim.getLocation().toVector()).normalize();
        org.bukkit.util.Vector look = victim.getLocation().getDirection().normalize();
        if (look.dot(toAttacker) < 0) {
            int dur = cfgi("duration", 20 + level * 20);
            victim.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.SPEED, dur, level - 1, true, false, false));
        }
    }

    @Override public String getDescription(int level) {
        return "§7Back-hit grants §aSpeed " + level + "§7 briefly.";
    }
}
