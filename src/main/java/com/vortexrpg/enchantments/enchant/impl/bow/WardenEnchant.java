package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.*;

/**
 * Warden: Arrows hitting blocks create a detection zone. Mobs entering ping the shooter.
 */
public class WardenEnchant extends VortexEnchant {

    private static final double[] RADIUS = {5.0, 7.0, 10.0};

    public WardenEnchant() {
        super("warden", "Warden", EnchantRarity.RARE, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitBlock(ProjectileHitEvent event, Player shooter, int level) {
        if (!isEnabled()) return;
        double radius = cfg("detection_radius", RADIUS[level - 1]);
        long durationSecs = cfgi("zone_duration_seconds", 30);
        Location zoneLoc = event.getEntity().getLocation();

        Set<UUID> alreadyPinged = new HashSet<>();
        // Period check: every 10 ticks
        plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
            if (!shooter.isOnline()) { task.cancel(); return; }
            for (LivingEntity e : MathUtil.getNearbyLiving(zoneLoc, radius)) {
                if (e.equals(shooter)) continue;
                if (!alreadyPinged.contains(e.getUniqueId())) {
                    alreadyPinged.add(e.getUniqueId());
                    shooter.playSound(shooter.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.5f);
                    shooter.sendMessage("§2[Warden] §7Detection ping! §e" + e.getName());
                }
            }
        }, 0L, 10L);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {}, durationSecs * 20L);
    }

    @Override
    public String getDescription() { return "Arrows placed in walls detect approaching enemies."; }

    @Override
    public String getDescription(int level) {
        return "§7Arrow in block: §adetection zone§7 " + RADIUS[level-1] + "§7-block radius for §e30s§7. Pings you.";
    }
}
