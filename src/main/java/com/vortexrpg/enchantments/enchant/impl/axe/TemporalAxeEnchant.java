package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Temporal Axe: Tracks where entities have been over the last 2 seconds.
 * On hit, 25/35/45% chance to rewind the target back to where they were
 * 2 seconds ago — a powerful displacement/escape-denial tool.
 */
public class TemporalAxeEnchant extends VortexEnchant {

    private record TimedLocation(Location loc, long time) {}
    private static final Map<UUID, LinkedList<TimedLocation>> POSITION_HISTORY = new HashMap<>();

    public TemporalAxeEnchant() {
        super("temporalaxe", "Temporal Axe", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.AXE));
    }

    private void recordPosition(LivingEntity entity) {
        LinkedList<TimedLocation> history = POSITION_HISTORY.computeIfAbsent(
                entity.getUniqueId(), k -> new LinkedList<>());
        long now = System.currentTimeMillis();
        history.addFirst(new TimedLocation(entity.getLocation().clone(), now));
        // Purge entries older than 3 seconds
        while (!history.isEmpty() && now - history.getLast().time() > 3000) {
            history.removeLast();
        }
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        // Always record victim's current position for future rewinds
        recordPosition(victim);

        double chance = cfgd("chance", 0.15 + level * 0.1);
        if (Math.random() > chance) return;

        LinkedList<TimedLocation> history = POSITION_HISTORY.get(victim.getUniqueId());
        if (history == null || history.size() < 3) return; // need some history

        // Get oldest recorded position (~2 seconds ago)
        Location rewindLoc = history.getLast().loc();

        // Safety: same world, reasonable distance
        if (!rewindLoc.getWorld().equals(victim.getWorld())) return;
        if (rewindLoc.distanceSquared(victim.getLocation()) < 4) return; // too close, not worth it
        if (rewindLoc.distanceSquared(victim.getLocation()) > 2500) return; // too far, suspicious

        // Draw rewind trail
        Location current = victim.getLocation().clone();
        ParticleUtil.drawLine(current.add(0, 1, 0), rewindLoc.clone().add(0, 1, 0),
                Particle.REVERSE_PORTAL, 15);

        // Teleport victim back
        float currentYaw = victim.getLocation().getYaw();
        float currentPitch = victim.getLocation().getPitch();
        rewindLoc.setYaw(currentYaw);
        rewindLoc.setPitch(currentPitch);
        victim.teleport(rewindLoc);

        ParticleUtil.spawn(rewindLoc, Particle.REVERSE_PORTAL, 25, 0.6);
        ParticleUtil.spawn(rewindLoc, Particle.ENCHANTED_HIT, 10, 0.4);
        SoundUtil.play(rewindLoc, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 0.8f, 1.5f);

        if (victim instanceof Player p) {
            p.sendMessage("§d[Temporal Axe] §7You were rewound in time!");
        }
        attacker.sendMessage("§d[Temporal Axe] §7Target rewound!");

        // Clear history after rewind
        history.clear();
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.15 + level * 0.1) * 100);
        return "§7" + pct + "% chance to §drewind §7target to their position from §e2s ago§7.";
    }
}
