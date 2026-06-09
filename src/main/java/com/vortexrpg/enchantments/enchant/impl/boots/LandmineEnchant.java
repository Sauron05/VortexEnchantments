package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

/** Landmine: When stepping on gravel, place a timed explosive that blasts nearby enemies in 3s. */
public class LandmineEnchant extends VortexEnchant {
    public LandmineEnchant() { super("landmine", "Landmine", EnchantRarity.EPIC, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onMove(PlayerMoveEvent event, Player player, int level) {
        if (!isEnabled() || !event.hasChangedBlock()) return;
        var block = player.getLocation().getBlock().getRelative(0, -1, 0);
        if (block.getType() != Material.GRAVEL) return;
        if (!plugin.getPlayerDataManager().hasSteppedBlock(player.getUniqueId(), com.vortexrpg.enchantments.util.MathUtil.blockKey(block))) {
            plugin.getPlayerDataManager().markBlockStepped(player.getUniqueId(), com.vortexrpg.enchantments.util.MathUtil.blockKey(block));
            var loc = block.getLocation().clone().add(0.5, 0.5, 0.5);
            int pow = cfgi("explosion_power", level);
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                loc.getWorld().createExplosion(loc, pow, false, false, player);
            }, 60L);
        }
    }

    @Override public String getDescription() { return "Stepping on gravel sets a delayed explosion."; }
    @Override public String getDescription(int level) {
        return "§7Step on gravel: triggers §aexplosion§7 in 3s (power §a" + level + "§7)."; }
}
