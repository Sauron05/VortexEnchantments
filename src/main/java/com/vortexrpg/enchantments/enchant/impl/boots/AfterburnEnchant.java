package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

/** Afterburn: Running for 3s straight sets your boots on fire — deal bonus fire damage for 5s. */
public class AfterburnEnchant extends VortexEnchant {
    public AfterburnEnchant() { super("afterburn", "Afterburn", EnchantRarity.RARE, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onMove(PlayerMoveEvent event, Player player, int level) {
        if (!isEnabled() || !event.hasChangedBlock()) return;
        if (!player.isSprinting()) {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "afterburn_ticks", 0);
            return;
        }
        int ticks = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "afterburn_ticks") + 1;
        if (ticks >= 60) { // ~3 seconds
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "afterburn_ticks", 0);
            player.setFireTicks(100 * level);
        } else {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "afterburn_ticks", ticks);
        }
    }

    @Override
    public void onAttack(org.bukkit.event.entity.EntityDamageByEntityEvent event, Player player, org.bukkit.entity.LivingEntity target, int level) {
        if (!isEnabled()) return;
        if (player.getFireTicks() > 0) {
            event.setDamage(event.getDamage() + 1.0 * level);
            target.setFireTicks(40 * level);
        }
    }

    @Override public String getDescription() { return "Sustained sprinting causes attacks to ignite."; }
    @Override public String getDescription(int level) {
        return "§7Sprint 3s: next attacks deal §a+1§7 fire dmg and ignite (§a" + level + "x§7 power)."; }
}
