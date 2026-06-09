package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * ExecutionerLegs: Massive bonus damage when target below 25% HP.
 */
public class ExecutionerLegsEnchant extends VortexEnchant {
    public ExecutionerLegsEnchant() { super("executioner_legs", "Executioner Legs", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double victimMaxHp = victim.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        double threshold = cfgd("hp_threshold", 0.25);
        if (victim.getHealth() / victimMaxHp > threshold) return;
        double bonus = cfgd("bonus_damage", 2.0 * level);
        event.setDamage(event.getDamage() + bonus);
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.DAMAGE_INDICATOR, 8, 0.3);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.8f, 0.6f);
    }

    @Override public String getDescription(int level) {
        return "§7Targets below 25% HP take §c+" + (2 * level) + " §7bonus damage.";
    }
}
