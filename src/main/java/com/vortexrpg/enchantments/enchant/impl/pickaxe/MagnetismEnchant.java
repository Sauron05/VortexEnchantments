package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/** Magnetism: All drops within 3/4/5 blocks fly toward the player automatically. */
public class MagnetismEnchant extends VortexEnchant {
    private static final double[] RADIUS = {3, 4, 5};

    public MagnetismEnchant() { super("magnetism", "Magnetism", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        double radius = cfg("magnet_radius", RADIUS[level-1]);
        for (org.bukkit.entity.Entity nearby : player.getNearbyEntities(radius, radius, radius)) {
            if (!(nearby instanceof Item item)) continue;
            Vector pull = player.getLocation().add(0, 1, 0).toVector().subtract(item.getLocation().toVector());
            if (pull.length() < 0.5) continue;
            item.setVelocity(pull.normalize().multiply(0.3));
        }
    }

    @Override public String getDescription() { return "Pulls nearby drops toward you."; }
    @Override public String getDescription(int level) {
        return "§7All drops within §a" + (int)RADIUS[level-1] + " blocks§7 fly toward you."; }
}
