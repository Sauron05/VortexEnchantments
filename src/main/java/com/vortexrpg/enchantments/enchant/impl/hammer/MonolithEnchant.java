package com.vortexrpg.enchantments.enchant.impl.hammer;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Monolith: Hitting the same target repeatedly stacks +5/8/10% damage per hit.
 * Resets if you switch targets.
 */
public class MonolithEnchant extends VortexEnchant {

    private static final Map<UUID, UUID> TARGETS = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> STACKS = new ConcurrentHashMap<>();

    public MonolithEnchant() {
        super("monolith", "Monolith", EnchantRarity.RARE, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        UUID attackerId = attacker.getUniqueId();
        UUID victimId = victim.getUniqueId();
        UUID lastTarget = TARGETS.get(attackerId);

        int stacks;
        if (victimId.equals(lastTarget)) {
            stacks = STACKS.getOrDefault(attackerId, 0) + 1;
        } else {
            stacks = 1;
        }
        TARGETS.put(attackerId, victimId);
        STACKS.put(attackerId, stacks);

        double perStack = cfgd("per_stack", 0.02 + level * 0.03);
        double bonus = stacks * perStack;
        event.setDamage(event.getDamage() * (1.0 + bonus));

        if (stacks >= 3) {
            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, stacks * 2, 0.3);
        }
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.02 + level * 0.03) * 100);
        return "§7Same target: §c+" + pct + "% §7per hit §8(resets on switch).";
    }
}
