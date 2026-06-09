package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;

/**
 * Wormhole: Shoot two bolts into blocks within 5s — creates a portal.
 * Shooter teleports from first landing to second landing location.
 */
public class WormholeEnchant extends VortexEnchant {

    private static final Map<UUID, Location> FIRST_PORTAL = new HashMap<>();
    private static final Map<UUID, Long> FIRST_TIME = new HashMap<>();

    public WormholeEnchant() {
        super("wormhole", "Wormhole", EnchantRarity.EPIC, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitBlock(ProjectileHitEvent event, Player shooter, int level) {
        if (!isEnabled()) return;

        UUID sid = shooter.getUniqueId();
        long now = System.currentTimeMillis();
        long window = cfgi("window_ms", 5000);

        Location first = FIRST_PORTAL.get(sid);
        Long firstTime = FIRST_TIME.get(sid);

        if (first == null || firstTime == null || now - firstTime > window) {
            FIRST_PORTAL.put(sid, event.getEntity().getLocation());
            FIRST_TIME.put(sid, now);
            ParticleUtil.spawn(event.getEntity().getLocation(), Particle.PORTAL, 15, 0.5);
            return;
        }

        Location second = event.getEntity().getLocation();
        double maxDist = cfgd("max_distance", 20.0 + level * 10.0);
        if (first.distance(second) > maxDist) return;

        // Teleport shooter to second location
        Location safe = second.clone().add(0, 1, 0);
        safe.setYaw(shooter.getLocation().getYaw());
        safe.setPitch(shooter.getLocation().getPitch());
        shooter.teleport(safe);

        ParticleUtil.drawLine(first, second, Particle.PORTAL, 0.3);
        ParticleUtil.burst(first, Particle.REVERSE_PORTAL, 15, 0.5);
        ParticleUtil.burst(second, Particle.REVERSE_PORTAL, 15, 0.5);
        SoundUtil.play(second, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

        FIRST_PORTAL.remove(sid);
        FIRST_TIME.remove(sid);
    }

    @Override
    public String getDescription(int level) {
        int range = (int) (20 + level * 10);
        return "§7Two block shots → §d§lWORMHOLE §7teleport (max §e" + range + " blocks§7).";
    }
}
