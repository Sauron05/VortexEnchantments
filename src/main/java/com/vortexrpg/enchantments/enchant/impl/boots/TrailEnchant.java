package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

/** Trail: Leaves a 2-tick glowing footprint behind you (particles + brief lit campfire style). */
public class TrailEnchant extends VortexEnchant {
    public TrailEnchant() { super("trail", "Trail", EnchantRarity.UNCOMMON, 1, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onMove(PlayerMoveEvent event, Player player, int level) {
        if (!isEnabled() || !event.hasChangedBlock()) return;
        com.vortexrpg.enchantments.util.ParticleUtil.trail(event.getFrom(), org.bukkit.Particle.DUST,
            3, 0.2f);
    }

    @Override public String getDescription() { return "Leaves a visible particle trail."; }
    @Override public String getDescription(int level) { return "§7Leaves a §aparticle trail§7 as you move."; }
}
