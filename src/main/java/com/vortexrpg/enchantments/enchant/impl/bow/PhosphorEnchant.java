package com.vortexrpg.enchantments.enchant.impl.bow;

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
 * Phosphor: Arrows apply Glowing for 3/5/7s and ignite the target for 2s.
 * Burning tracers — mark your enemy for the whole team.
 */
public class PhosphorEnchant extends VortexEnchant {

    public PhosphorEnchant() {
        super("phosphor", "Phosphor", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int glowDuration = cfgi("glow_duration", (1 + level * 2)) * 20;
        int fireTicks = cfgi("fire_ticks", 40);

        victim.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, glowDuration, 0, false, false));
        victim.setFireTicks(fireTicks);

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.FLAME, 8, 0.4);
    }

    @Override
    public String getDescription(int level) {
        int dur = 1 + level * 2;
        return "§7Arrows apply §eGlowing " + dur + "s §7+ §6fire 2s§7.";
    }
}
