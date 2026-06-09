package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;

/**
 * Pulsar: Every 5th arrow fired is a mega-shot dealing 3x damage + AoE explosion.
 * A rhythmic cannon — count your shots, time the devastation.
 */
public class PulsarEnchant extends VortexEnchant {

    private static final Map<UUID, Integer> SHOT_COUNT = new HashMap<>();

    public PulsarEnchant() {
        super("pulsar", "Pulsar", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int count = SHOT_COUNT.merge(shooter.getUniqueId(), 1, (a, b) -> a + b);
        int interval = cfgi("interval", 5);

        if (count % interval != 0) return;

        double multiplier = cfgd("damage_multiplier", 2.0 + level);
        double radius = cfgd("aoe_radius", 4.0);
        double aoeDmg = event.getDamage() * 0.5;

        event.setDamage(event.getDamage() * multiplier);

        for (LivingEntity nearby : MathUtil.getNearbyLiving(victim.getLocation(), radius)) {
            if (nearby.equals(shooter) || nearby.equals(victim)) continue;
            nearby.damage(aoeDmg, shooter);
        }

        ParticleUtil.burst(victim.getLocation(), Particle.END_ROD, 25, radius);
        ParticleUtil.drawCircle(victim.getLocation(), radius, 20, Particle.FLAME);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.8f, 1.8f);
    }

    @Override
    public String getDescription(int level) {
        double mult = 2 + level;
        return "§7Every 5th arrow: §6§lMEGA-SHOT §7— §c" + mult + "x damage §7+ §cAoE§7.";
    }
}
