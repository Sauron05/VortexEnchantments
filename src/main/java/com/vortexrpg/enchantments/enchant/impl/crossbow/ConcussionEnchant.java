package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Concussion: Bolt applies Blindness for 1/2/3s and Nausea for 2s.
 * Disorienting hit that scrambles the target's view.
 */
public class ConcussionEnchant extends VortexEnchant {

    public ConcussionEnchant() {
        super("concussion", "Concussion", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int blindDuration = cfgi("blind_duration", level) * 20;
        int nauseaDuration = cfgi("nausea_duration", 40);

        victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindDuration, 0, false, false));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, nauseaDuration, 0, false, false));

        ParticleUtil.spawn(victim.getLocation().add(0, 1.5, 0), Particle.SMOKE, 10, 0.3);
    }

    @Override
    public String getDescription(int level) {
        return "§7Bolt: §8Blindness " + level + "s §7+ §8Nausea 2s§7.";
    }
}
