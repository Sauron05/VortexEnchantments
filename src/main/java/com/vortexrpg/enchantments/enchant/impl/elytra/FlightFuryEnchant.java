package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** FlightFury: Build rage while gliding; next melee hit is empowered. */
public class FlightFuryEnchant extends VortexEnchant {

    public FlightFuryEnchant() { super("flight_fury", "Flight Fury", EnchantRarity.EPIC, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (player.isGliding()) {
            int stacks = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "ff_stacks", 0);
            int max = cfgi("max_stacks", 10 + level * 5);
            if (stacks < max) {
                plugin.getPlayerDataManager().setInt(player.getUniqueId(), "ff_stacks", stacks + 1);
            }
        }
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player player, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        int stacks = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "ff_stacks", 0);
        if (stacks > 0) {
            double bonusPer = cfgd("damage_per_stack", 0.3 * level);
            event.setDamage(event.getDamage() + stacks * bonusPer);
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "ff_stacks", 0);
        }
    }

    @Override public String getDescription() { return "Build rage in flight; unleash on melee hit."; }
    @Override public String getDescription(int level) {
        return "§7Gliding builds rage (§e1/s§7). Next melee: §c+" + String.format("%.1f", 0.3 * level) + "§7 per stack."; }
}
