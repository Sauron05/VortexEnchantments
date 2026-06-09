package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/** BogWalker: Walk on water (Water Walking effect via constant refresh). */
public class BogWalkerEnchant extends VortexEnchant {
    public BogWalkerEnchant() { super("bog_walker", "Bog Walker", EnchantRarity.RARE, 1, List.of(ItemTarget.BOOTS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        // Cancel sinking in water by pushing player up if in water
        if (player.isInWater() && !player.isSneaking()) {
            var vel = player.getVelocity();
            player.setVelocity(vel.setY(Math.max(vel.getY(), 0.1)));
        }
    }

    @Override public String getDescription() { return "Float on water surfaces."; }
    @Override public String getDescription(int level) { return "§7You float on water (sneak to sink)."; }
}
