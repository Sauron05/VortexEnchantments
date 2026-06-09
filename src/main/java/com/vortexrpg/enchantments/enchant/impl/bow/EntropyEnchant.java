package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;

/**
 * Entropy: Temporarily reduces target's effective max HP by 0.5/1/1.5 per arrow hit (stacks, 10s window).
 * Implemented as bonus damage that scales with how many arrows have hit.
 */
public class EntropyEnchant extends VortexEnchant {

    private static final Map<UUID, Integer> HIT_COUNT = new HashMap<>();
    private static final Map<UUID, Long> HIT_TIME = new HashMap<>();

    public EntropyEnchant() {
        super("entropy", "Entropy", EnchantRarity.RARE, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        UUID id = victim.getUniqueId();
        long now = System.currentTimeMillis();
        long window = cfgi("window_ms", 10000);

        Long last = HIT_TIME.get(id);
        if (last == null || now - last > window) {
            HIT_COUNT.put(id, 1);
        } else {
            HIT_COUNT.merge(id, 1, (a, b) -> a + b);
        }
        HIT_TIME.put(id, now);

        int hits = HIT_COUNT.getOrDefault(id, 1);
        double reductionPerHit = cfgd("reduction_per_hit", level * 0.5);
        double totalReduction = hits * reductionPerHit;

        double maxHp = victim.getAttribute(Attribute.MAX_HEALTH).getValue();
        double effectiveMax = Math.max(2.0, maxHp - totalReduction);
        if (victim.getHealth() > effectiveMax) {
            victim.setHealth(effectiveMax);
        }

        ParticleUtil.spawn(victim.getLocation().add(0, 1.5, 0), Particle.DAMAGE_INDICATOR, hits * 2, 0.3);
    }

    @Override
    public String getDescription(int level) {
        double red = level * 0.5;
        return "§7Each arrow §creduces §7target max HP by §c" + red + " §7(10s window).";
    }
}
