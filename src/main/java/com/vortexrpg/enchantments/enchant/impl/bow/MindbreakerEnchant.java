package com.vortexrpg.enchantments.enchant.impl.bow;

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
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.List;

/**
 * Mindbreaker: Arrow strips all potion effects from the target and converts
 * each removed effect into bonus damage (1/1.5/2 per effect).
 */
public class MindbreakerEnchant extends VortexEnchant {

    public MindbreakerEnchant() {
        super("mindbreaker", "Mindbreaker", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        Collection<PotionEffect> effects = victim.getActivePotionEffects();
        if (effects.isEmpty()) return;

        double perEffect = cfgd("damage_per_effect", 0.5 + level * 0.5);
        double bonus = effects.size() * perEffect;

        for (PotionEffect effect : effects) {
            victim.removePotionEffect(effect.getType());
        }

        event.setDamage(event.getDamage() + bonus);

        ParticleUtil.burst(victim.getLocation().add(0, 1, 0), Particle.WITCH, 15, 0.5);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 0.8f, 0.5f);
    }

    @Override
    public String getDescription(int level) {
        double per = 0.5 + level * 0.5;
        return "§7Strip all potion effects → §c+" + per + " damage §7per effect removed.";
    }
}
