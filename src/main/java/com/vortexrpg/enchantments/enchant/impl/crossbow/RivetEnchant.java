package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Rivet: Bolt pins entity to nearest wall. Target stuck for 1.5/2/2.5s. */
public class RivetEnchant extends VortexEnchant {
    private static final double[] PIN_SECS = {1.5, 2.0, 2.5};
    public RivetEnchant() { super("rivet", "Rivet", EnchantRarity.RARE, 3, List.of(ItemTarget.CROSSBOW)); }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        int wallRadius = cfgi("wall_detection_radius", level + 1);
        double pinSecs = cfg("pin_duration_seconds", PIN_SECS[level-1]);

        // Find nearest wall
        Location victimLoc = victim.getLocation();
        for (int x = -wallRadius; x <= wallRadius; x++) {
            for (int z = -wallRadius; z <= wallRadius; z++) {
                Block b = victimLoc.getWorld().getBlockAt(
                    victimLoc.getBlockX() + x, victimLoc.getBlockY(), victimLoc.getBlockZ() + z);
                if (b.getType().isSolid()) {
                    // Teleport to wall
                    victim.teleport(b.getLocation().add(0.5, 0, 0.5));
                    long expiry = System.currentTimeMillis() + (long)(pinSecs * 1000);
                    plugin.getPlayerDataManager().setLong(victim.getUniqueId(), "rivet_pinned", expiry);
                    if (victim instanceof Player p) p.sendMessage("§c[Rivet] §7You're pinned to the wall!");
                    return;
                }
            }
        }
    }

    @Override public String getDescription() { return "Pins target to the nearest wall."; }
    @Override public String getDescription(int level) {
        return "§7Bolt pins target to wall for §e" + PIN_SECS[level-1] + "s§7."; }
}
