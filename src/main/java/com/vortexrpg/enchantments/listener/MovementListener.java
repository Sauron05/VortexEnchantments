package com.vortexrpg.enchantments.listener;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.data.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class MovementListener implements Listener {

    @SuppressWarnings("unused") // Reserved for future movement hooks
    private final VortexEnchantments plugin;
    private final PlayerDataManager data;

    public MovementListener(VortexEnchantments plugin) {
        this.plugin = plugin;
        this.data = plugin.getPlayerDataManager();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) return;
        Player player = event.getPlayer();
        // Track distance for Nomad and Capacitor
        double dist = event.getFrom().distance(event.getTo());
        data.addDistanceTraveled(player.getUniqueId(), dist);
        data.addWalkDistanceForCapacitor(player.getUniqueId(), dist);
        data.addStaticChargeDistance(player.getUniqueId(), dist);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (event.isSneaking()) {
            data.recordCrouchStart(player.getUniqueId());
        } else {
            data.clearCrouch(player.getUniqueId());
        }
    }
}
