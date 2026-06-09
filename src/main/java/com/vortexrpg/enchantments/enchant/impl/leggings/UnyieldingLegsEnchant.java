package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * UnyieldingLegs: When hit that would drop HP below 1, survive with 1 HP once.
 */
public class UnyieldingLegsEnchant extends VortexEnchant {
    public UnyieldingLegsEnchant() { super("unyielding_legs", "Unyielding Legs", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        double hpAfter = victim.getHealth() - event.getFinalDamage();
        if (hpAfter >= 0) return;
        if (isOnCooldown(victim)) return;

        event.setDamage(0);
        victim.setHealth(cfgd("survive_hp", 1.0));
        ParticleUtil.burst(victim.getLocation().add(0, 1, 0), Particle.TOTEM_OF_UNDYING, 20, 1.5);
        SoundUtil.play(victim.getLocation(), Sound.ITEM_TOTEM_USE, 0.6f, 1.2f);
        double cd = cfgd("cooldown", 120.0 - level * 15.0);
        setCooldownFromConfig(victim, "cooldown", cd);
    }

    @Override public String getDescription(int level) {
        return "§7Survive lethal melee hit with §c1 HP§7. §8" + (int)(120 - level * 15) + "s CD.";
    }
}
