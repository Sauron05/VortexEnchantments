package com.vortexrpg.enchantments.enchant.impl.trident;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Maelstrom: After Riptide landing, creates 4/5/6 block vortex pulling items and XP toward player.
 * Implemented on movement (Riptide landing = fast water landing), pulls nearby entities.
 */
public class MaelstromEnchant extends VortexEnchant {
    private static final double[] RADIUS = {4, 5, 6};

    public MaelstromEnchant() { super("maelstrom", "Maelstrom", EnchantRarity.RARE, 3, List.of(ItemTarget.TRIDENT)); }

    @Override
    public void onMove(PlayerMoveEvent event, Player player, int level) {
        if (!isEnabled()) return;
        // Trigger on Riptide landing: player in water, high vertical velocity
        if (!player.isInWater()) return;
        if (player.getVelocity().getY() > -0.3) return;
        if (isOnCooldown(player)) return;
        setCooldownSeconds(player, 2);
        double radius = cfg("vortex_radius", RADIUS[level-1]);
        // Pull nearby items/XP orbs toward player
        for (Entity nearby : player.getNearbyEntities(radius, radius, radius)) {
            if (nearby instanceof Item || nearby instanceof ExperienceOrb) {
                Vector pull = player.getLocation().toVector().subtract(nearby.getLocation().toVector()).normalize().multiply(1.5);
                nearby.setVelocity(pull);
            }
        }
    }

    @Override public String getDescription() { return "Riptide landing creates a pull vortex."; }
    @Override public String getDescription(int level) {
        return "§7Riptide landing: §e" + (int)RADIUS[level-1] + " block§7 vortex pulls items and XP."; }
}
