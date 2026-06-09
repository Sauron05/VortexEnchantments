package com.vortexrpg.enchantments.enchant.impl.bow;

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
 * Tracer: Arrows leave a particle trail. Repeated arrows to the same target
 * deal 15/25/35% bonus damage (stacks reset after 5s or target switch).
 */
public class TracerEnchant extends VortexEnchant {

    private static final Map<UUID, UUID> LAST_TARGET = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> LAST_HIT = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> STACKS = new ConcurrentHashMap<>();

    public TracerEnchant() {
        super("tracer", "Tracer", EnchantRarity.COMMON, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        UUID sid = shooter.getUniqueId();
        UUID vid = victim.getUniqueId();
        long now = System.currentTimeMillis();

        UUID lastVid = LAST_TARGET.get(sid);
        long lastTime = LAST_HIT.getOrDefault(sid, 0L);

        int stacks;
        if (vid.equals(lastVid) && now - lastTime < 5000) {
            stacks = STACKS.getOrDefault(sid, 0) + 1;
        } else {
            stacks = 1;
        }

        LAST_TARGET.put(sid, vid);
        LAST_HIT.put(sid, now);
        STACKS.put(sid, stacks);

        if (stacks > 1) {
            double bonus = cfgd("bonus_per_stack", 0.05 + level * 0.10) * (stacks - 1);
            event.setDamage(event.getDamage() * (1.0 + bonus));
            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, stacks * 3, 0.3);
        }
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.05 + level * 0.10) * 100);
        return "§7Repeat shots to same target: §c+" + pct + "% §7per arrow §8(5s window).";
    }
}
