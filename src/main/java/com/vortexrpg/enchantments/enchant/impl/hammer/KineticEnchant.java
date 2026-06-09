package com.vortexrpg.enchantments.enchant.impl.hammer;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Kinetic: Store damage taken, then release it as bonus damage on next attack.
 * Stores up to 20 HP of damage, resets after release.
 */
public class KineticEnchant extends VortexEnchant {

    private static final Map<UUID, Double> STORED = new ConcurrentHashMap<>();

    public KineticEnchant() {
        super("kinetic", "Kinetic", EnchantRarity.RARE, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;

        double max = cfgd("max_stored", 20.0);
        double current = STORED.getOrDefault(victim.getUniqueId(), 0.0);
        double stored = Math.min(current + event.getDamage(), max);
        STORED.put(victim.getUniqueId(), stored);

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 3, 0.2);
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double stored = STORED.getOrDefault(attacker.getUniqueId(), 0.0);
        if (stored <= 0) return;

        double multiplier = cfgd("release_multiplier", 0.3 + level * 0.2);
        event.setDamage(event.getDamage() + stored * multiplier);
        STORED.remove(attacker.getUniqueId());

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SONIC_BOOM, 1, 0.1);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.4f, 1.8f);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.3 + level * 0.2) * 100);
        return "§7Absorb damage taken, release §c" + pct + "% §7as bonus on next hit.";
    }
}
