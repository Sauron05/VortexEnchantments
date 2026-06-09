package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
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
 * Catalyst: Mark a target with 3 arrows within 5s → triggers chain explosion
 * dealing massive AoE damage in a 4-block radius.
 */
public class CatalystEnchant extends VortexEnchant {

    private static final Map<UUID, Integer> MARKS = new HashMap<>();
    private static final Map<UUID, Long> MARK_TIME = new HashMap<>();

    public CatalystEnchant() {
        super("catalyst", "Catalyst", EnchantRarity.RARE, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        UUID id = victim.getUniqueId();
        long now = System.currentTimeMillis();
        long window = cfgi("window_ms", 5000);
        int required = cfgi("marks_required", 3);

        Long last = MARK_TIME.get(id);
        if (last == null || now - last > window) {
            MARKS.put(id, 1);
        } else {
            MARKS.merge(id, 1, (a, b) -> a + b);
        }
        MARK_TIME.put(id, now);

        int marks = MARKS.getOrDefault(id, 0);
        if (marks >= required) {
            detonate(shooter, victim, level);
            MARKS.remove(id);
            MARK_TIME.remove(id);
        } else {
            ParticleUtil.spawn(victim.getLocation().add(0, 2, 0), Particle.ENCHANTED_HIT, marks * 4, 0.3);
        }
    }

    private void detonate(Player shooter, LivingEntity victim, int level) {
        Location center = victim.getLocation();
        double radius = cfgd("explosion_radius", 4.0);
        double damage = cfgd("explosion_damage", 4.0 + level * 3.0);

        for (LivingEntity target : MathUtil.getNearbyLiving(center, radius)) {
            if (target.equals(shooter)) continue;
            target.damage(damage, shooter);
        }

        ParticleUtil.burst(center, Particle.EXPLOSION, 5, 1.0);
        ParticleUtil.drawCircle(center, radius, 20, Particle.FLAME);
        SoundUtil.play(center, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);
    }

    @Override
    public String getDescription(int level) {
        double dmg = 4 + level * 3;
        return "§7Hit 3 arrows in 5s → §c§lCHAIN EXPLOSION §7— §c" + dmg + " AoE damage§7.";
    }
}
