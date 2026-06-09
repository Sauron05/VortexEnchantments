package com.vortexrpg.enchantments.enchant.impl.hammer;

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

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Detonate: Every 3rd hit on the same target triggers a massive AoE explosion.
 * 40/60/80% of total accumulated damage as explosion radius damage.
 */
public class DetonateEnchant extends VortexEnchant {

    private static final Map<UUID, Map<UUID, Integer>> HIT_COUNT = new ConcurrentHashMap<>();
    private static final Map<UUID, Map<UUID, Double>> STORED_DMG = new ConcurrentHashMap<>();

    public DetonateEnchant() {
        super("detonate", "Detonate", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        UUID aId = attacker.getUniqueId();
        UUID vId = victim.getUniqueId();

        HIT_COUNT.computeIfAbsent(aId, k -> new ConcurrentHashMap<>());
        STORED_DMG.computeIfAbsent(aId, k -> new ConcurrentHashMap<>());

        int hits = HIT_COUNT.get(aId).getOrDefault(vId, 0) + 1;
        double totalDmg = STORED_DMG.get(aId).getOrDefault(vId, 0.0) + event.getDamage();

        if (hits >= 3) {
            // DETONATE!
            double aoePct = cfgd("explosion_percent", 0.20 + level * 0.20);
            double aoeDmg = totalDmg * aoePct;
            double radius = cfgd("radius", 4.0);

            for (LivingEntity nearby : MathUtil.getNearbyLiving(victim.getLocation(), radius)) {
                if (nearby.equals(attacker)) continue;
                nearby.damage(aoeDmg, attacker);
            }

            ParticleUtil.drawCircle(victim.getLocation(), radius, 24, Particle.FLAME);
            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.EXPLOSION, 3, 0.5);
            SoundUtil.play(victim.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.8f, 1.0f);

            HIT_COUNT.get(aId).remove(vId);
            STORED_DMG.get(aId).remove(vId);
        } else {
            HIT_COUNT.get(aId).put(vId, hits);
            STORED_DMG.get(aId).put(vId, totalDmg);
            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.LAVA, hits * 2, 0.2);
        }
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.20 + level * 0.20) * 100);
        return "§7Every 3rd hit: §cAoE explosion §7for §c" + pct + "% §7accumulated damage.";
    }
}
