package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/** Moonwalk: Move in the reverse direction at 50% speed while sneaking (effect only). */
public class MoonwalkEnchant extends VortexEnchant {
    public MoonwalkEnchant() { super("moonwalk", "Moonwalk", EnchantRarity.UNCOMMON, 1, List.of(ItemTarget.BOOTS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isSneaking()) return;
        // Reverse velocity horizontal component when sneaking
        var vel = player.getVelocity();
        if (vel.getX() != 0 || vel.getZ() != 0) {
            player.setVelocity(vel.setX(-vel.getX() * 0.5).setZ(-vel.getZ() * 0.5));
        }
    }

    @Override public String getDescription() { return "Sneaking glides you backwards."; }
    @Override public String getDescription(int level) { return "§7Sneaking reverses movement at half speed."; }
}
