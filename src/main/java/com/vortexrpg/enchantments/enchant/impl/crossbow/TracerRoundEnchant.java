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
 * TracerRound: Bolt marks target with Glowing for 5/8/12 seconds.
 * Tag and track — the target can't hide.
 */
public class TracerRoundEnchant extends VortexEnchant {

    public TracerRoundEnchant() {
        super("tracerround", "Tracer Round", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int duration = cfgi("glow_duration", 3 + level * 3) * 20;
        victim.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration, 0, false, false));
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.END_ROD, 8, 0.3);
    }

    @Override
    public String getDescription(int level) {
        int dur = 3 + level * 3;
        return "§7Bolt: §eGlowing " + dur + "s §7— target can't hide.";
    }
}
