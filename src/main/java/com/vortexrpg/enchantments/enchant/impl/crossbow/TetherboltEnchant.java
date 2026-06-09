package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
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
 * Tetherbolt: Hit two entities within 5s to tether them — when one takes damage,
 * the other takes 30/40/50% of that damage too.
 */
public class TetherboltEnchant extends VortexEnchant {

    private static final Map<UUID, UUID> FIRST_TARGET = new HashMap<>();
    private static final Map<UUID, Long> FIRST_HIT_TIME = new HashMap<>();
    private static final Map<UUID, UUID> TETHERED_PAIR = new HashMap<>();

    public TetherboltEnchant() {
        super("tetherbolt", "Tetherbolt", EnchantRarity.RARE, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        UUID sid = shooter.getUniqueId();
        UUID vid = victim.getUniqueId();
        long now = System.currentTimeMillis();
        long window = cfgi("window_ms", 5000);

        // Check if this target is tethered and apply shared damage
        UUID partner = TETHERED_PAIR.get(vid);
        if (partner != null) {
            double sharePct = cfgd("share_pct", 0.20 + level * 0.10);
            double sharedDmg = event.getDamage() * sharePct;
            for (org.bukkit.entity.Entity e : victim.getWorld().getEntities()) {
                if (e.getUniqueId().equals(partner) && e instanceof LivingEntity le) {
                    le.damage(sharedDmg, shooter);
                    ParticleUtil.drawLine(victim.getLocation().add(0, 1, 0),
                            le.getLocation().add(0, 1, 0), Particle.ELECTRIC_SPARK, 0.3);
                    break;
                }
            }
            return;
        }

        UUID firstId = FIRST_TARGET.get(sid);
        Long firstTime = FIRST_HIT_TIME.get(sid);

        if (firstId == null || firstTime == null || now - firstTime > window || firstId.equals(vid)) {
            FIRST_TARGET.put(sid, vid);
            FIRST_HIT_TIME.put(sid, now);
            ParticleUtil.spawn(victim.getLocation().add(0, 2, 0), Particle.ELECTRIC_SPARK, 6, 0.2);
        } else {
            // Second target hit within window — create tether
            TETHERED_PAIR.put(firstId, vid);
            TETHERED_PAIR.put(vid, firstId);
            FIRST_TARGET.remove(sid);
            FIRST_HIT_TIME.remove(sid);

            SoundUtil.play(victim.getLocation(), Sound.BLOCK_CHAIN_PLACE, 1.0f, 0.5f);
            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ELECTRIC_SPARK, 12, 0.4);

            // Remove tether after 10s
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                TETHERED_PAIR.remove(firstId);
                TETHERED_PAIR.remove(vid);
            }, 200L);
        }
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.20 + level * 0.10) * 100);
        return "§7Hit 2 targets → §etether §7them (§c" + pct + "% §7shared damage for 10s).";
    }
}
