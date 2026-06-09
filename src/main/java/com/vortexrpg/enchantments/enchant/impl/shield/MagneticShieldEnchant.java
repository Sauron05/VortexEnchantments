package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/** Magnetic Shield: Pull dropped items toward you while blocking. */
public class MagneticShieldEnchant extends VortexEnchant {

    public MagneticShieldEnchant() { super("magnetic_shield", "Magnetic Shield", EnchantRarity.RARE, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        long tick = plugin.getServer().getCurrentTick();
        if (tick % 5 != 0) return;
        double radius = cfg("radius", 4.0 + level * 2);
        double speed = cfg("pull-speed", 0.15 + level * 0.05);
        for (var entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Item item) {
                Vector pull = player.getLocation().toVector().subtract(item.getLocation().toVector()).normalize().multiply(speed);
                item.setVelocity(pull);
            }
        }
    }

    @Override public String getDescription() { return "Pull items toward you while blocking."; }
    @Override public String getDescription(int level) {
        return "§7Block: pull items in §e" + (int)(4 + level * 2) + "b§7 toward you."; }
}
