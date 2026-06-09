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
 * MirrorLegs: Chance to reflect projectile damage back at attacker.
 */
public class MirrorLegsEnchant extends VortexEnchant {
    public MirrorLegsEnchant() { super("mirror_legs", "Mirror Legs", EnchantRarity.RARE, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (event.getCause() != EntityDamageByEntityEvent.DamageCause.PROJECTILE) return;
        double chance = cfgd("reflect_chance", 0.10 * level);
        if (Math.random() >= chance) return;
        if (attacker instanceof org.bukkit.entity.LivingEntity living) {
            living.damage(event.getDamage() * 0.5, victim);
        }
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 10, 0.4);
        SoundUtil.play(victim.getLocation(), Sound.ITEM_SHIELD_BLOCK, 0.6f, 1.5f);
    }

    @Override public String getDescription(int level) {
        return "§7" + (10 * level) + "% §7chance to reflect 50% projectile damage.";
    }
}
