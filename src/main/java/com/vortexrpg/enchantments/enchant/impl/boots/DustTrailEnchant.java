package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

/**
 * Dust Trail: Leave a particle trail when sprinting.
 */
public class DustTrailEnchant extends VortexEnchant {
    public DustTrailEnchant() { super("dust_trail", "Dust Trail", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onMove(PlayerMoveEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isSprinting()) return;
        ParticleUtil.spawn(player.getLocation(), Particle.CAMPFIRE_COSY_SMOKE, level, 0.2);
    }

    @Override public String getDescription(int level) {
        return "§7Leaves a dust trail when sprinting.";
    }
}
