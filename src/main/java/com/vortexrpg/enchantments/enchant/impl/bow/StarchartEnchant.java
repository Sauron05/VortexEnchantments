package com.vortexrpg.enchantments.enchant.impl.bow;

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

import java.util.*;

/**
 * Starchart: Mark 3/4/5 targets with arrows, then lightning strikes connect them all,
 * dealing shared damage. Marks last 8s. Constellation in the sky.
 */
public class StarchartEnchant extends VortexEnchant {

    private static final Map<UUID, List<UUID>> MARKED = new HashMap<>();
    private static final Map<UUID, Long> MARK_START = new HashMap<>();

    public StarchartEnchant() {
        super("starchart", "Starchart", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        UUID sid = shooter.getUniqueId();
        long now = System.currentTimeMillis();
        long window = cfgi("window_ms", 8000);
        int required = cfgi("marks_required", 2 + level);

        Long start = MARK_START.get(sid);
        if (start == null || now - start > window) {
            MARKED.put(sid, new ArrayList<>());
            MARK_START.put(sid, now);
        }

        List<UUID> marks = MARKED.computeIfAbsent(sid, k -> new ArrayList<>());
        UUID vid = victim.getUniqueId();
        if (!marks.contains(vid)) {
            marks.add(vid);
        }

        ParticleUtil.spawn(victim.getLocation().add(0, 2.5, 0), Particle.END_ROD, 6 + marks.size() * 2, 0.2);

        if (marks.size() >= required) {
            detonateConstellation(shooter, marks, level);
            MARKED.remove(sid);
            MARK_START.remove(sid);
        }
    }

    private void detonateConstellation(Player shooter, List<UUID> marks, int level) {
        double damage = cfgd("chain_damage", 4.0 + level * 2.0);

        List<LivingEntity> targets = new ArrayList<>();
        for (UUID id : marks) {
            for (org.bukkit.entity.Entity e : shooter.getWorld().getEntities()) {
                if (e.getUniqueId().equals(id) && e instanceof LivingEntity le) {
                    targets.add(le);
                    break;
                }
            }
        }

        for (int i = 0; i < targets.size(); i++) {
            LivingEntity a = targets.get(i);
            LivingEntity b = targets.get((i + 1) % targets.size());
            a.getWorld().strikeLightningEffect(a.getLocation());
            a.damage(damage, shooter);
            ParticleUtil.drawLine(a.getLocation().add(0, 1, 0), b.getLocation().add(0, 1, 0), Particle.END_ROD, 0.3);
        }

        SoundUtil.play(shooter.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.5f);
    }

    @Override
    public String getDescription(int level) {
        int req = 2 + level;
        return "§7Mark §e" + req + " §7targets → §e⚡constellation strike§7 linking them all.";
    }
}
