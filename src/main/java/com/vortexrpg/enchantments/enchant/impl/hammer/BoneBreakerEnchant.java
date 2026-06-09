package com.vortexrpg.enchantments.enchant.impl.hammer;

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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BoneBreaker: Each hit reduces target speed by 10/15/20% for 8 seconds.
 * Stacks up to 3 times — crippling slow. Uses Slowness amplifier stacking.
 */
public class BoneBreakerEnchant extends VortexEnchant {

    private static final Map<UUID, Integer> STACKS = new ConcurrentHashMap<>();

    public BoneBreakerEnchant() {
        super("bonebreaker", "Bone Breaker", EnchantRarity.EPIC, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        UUID victimId = victim.getUniqueId();
        int maxStacks = cfgi("max_stacks", 3);
        int current = STACKS.getOrDefault(victimId, 0);
        int newStacks = Math.min(current + 1, maxStacks);
        STACKS.put(victimId, newStacks);

        int duration = cfgi("duration", 160);
        int amp = (int) (newStacks * (cfgd("slow_per_stack", 0.05 + level * 0.05) * 10));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, Math.min(amp, 5), false, true));

        if (newStacks >= maxStacks) {
            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.DAMAGE_INDICATOR, 8, 0.3);
        }
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.05 + level * 0.05) * 100);
        return "§7Hits slow target §c-" + pct + "% §7per stack §8(max 3 stacks, 8s).";
    }
}
