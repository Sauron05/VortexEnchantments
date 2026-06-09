package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Psyche: When taking fatal damage, instead survive at 1 HP + gain Absorption. 60s CD. */
public class PsycheEnchant extends VortexEnchant {
    public PsycheEnchant() { super("psyche", "Psyche", EnchantRarity.RARE, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(victim)) return;
        double afterHealth = victim.getHealth() - event.getFinalDamage();
        if (afterHealth > 0) return;
        event.setDamage(0);
        victim.setHealth(1.0);
        int absorb = cfgi("absorption_level", level);
        victim.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, absorb - 1));
        com.vortexrpg.enchantments.util.ParticleUtil.burst(victim.getLocation(), org.bukkit.Particle.TOTEM_OF_UNDYING, 30, 1.0);
        com.vortexrpg.enchantments.util.SoundUtil.play(victim.getLocation(), org.bukkit.Sound.ITEM_TOTEM_USE, 1.0f, 1.2f);
        setCooldownFromConfig(victim, "cooldown", 60.0);
    }

    @Override public String getDescription(int level) {
        return "§7Survive fatal damage at §c1 HP§7 + gain Absorption " + level + ". §860s CD.";
    }
}
