package com.vortexrpg.enchantments.enchant.impl.trident;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Eddy: After throwing trident, hold right-click to steer it mid-flight for 1/1.5/2 seconds.
 */
public class EddyEnchant extends VortexEnchant {
    private static final double[] CONTROL_DURATION = {1.0, 1.5, 2.0};

    public EddyEnchant() { super("eddy", "Eddy", EnchantRarity.EPIC, 3, List.of(ItemTarget.TRIDENT)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        // Find a trident in the air owned by this player
        Trident controlled = null;
        for (Entity e : player.getWorld().getEntities()) {
            if (e instanceof Trident t && t.getShooter() instanceof Player p && p.equals(player)) {
                if (!t.isOnGround()) { controlled = t; break; }
            }
        }
        if (controlled == null) return;
        double controlSecs = cfg("control_duration_" + level, CONTROL_DURATION[level-1]);
        final Trident trident = controlled;
        new BukkitRunnable() {
            int ticks = 0;
            @Override public void run() {
                if (!trident.isValid() || trident.isOnGround() || ticks++ > controlSecs * 20) { cancel(); return; }
                if (!player.isSneaking() && !player.isHandRaised()) { cancel(); return; }
                Vector dir = player.getEyeLocation().getDirection().normalize().multiply(trident.getVelocity().length());
                trident.setVelocity(dir);
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    @Override public String getDescription() { return "Steer your thrown trident mid-flight."; }
    @Override public String getDescription(int level) {
        return "§7After throwing: hold §eright-click§7 to steer for §a" + CONTROL_DURATION[level-1] + "s§7."; }
}
