package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

/** Flux: Every 20 blocks traveled, boost next attack by 15%. Counter resets on attack. */
public class FluxEnchant extends VortexEnchant {
    private static final double BONUS = 0.15;
    private static final double BLOCKS_REQUIRED = 20.0;

    public FluxEnchant() { super("flux", "Flux", EnchantRarity.RARE, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onMove(PlayerMoveEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.hasChangedBlock()) return;
        plugin.getPlayerDataManager().addDistanceTraveled(player.getUniqueId(), 1.0);
        double dist = plugin.getPlayerDataManager().getTotalDistanceTraveled(player.getUniqueId());
        if (dist >= BLOCKS_REQUIRED * level) {
            plugin.getPlayerDataManager().resetDistance(player.getUniqueId());
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "flux_ready", 1);
        }
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player player, org.bukkit.entity.LivingEntity target, int level) {
        if (!isEnabled()) return;
        if (plugin.getPlayerDataManager().getInt(player.getUniqueId(), "flux_ready") == 1) {
            event.setDamage(event.getDamage() * (1.0 + BONUS * level));
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "flux_ready", 0);
        }
    }

    @Override public String getDescription() { return "Distance traveled charges powerful strikes."; }
    @Override public String getDescription(int level) {
        return "§7Every §a" + (int)(BLOCKS_REQUIRED*level) + "§7 blocks: next attack §a+" + (int)(BONUS*level*100) + "§a%§7."; }
}
