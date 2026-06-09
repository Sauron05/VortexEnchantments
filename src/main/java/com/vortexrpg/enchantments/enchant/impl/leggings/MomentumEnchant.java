package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Momentum: Build speed stacks from kills (up to 3/4/5); each stack = +2% damage. Resets on death. */
public class MomentumEnchant extends VortexEnchant {
    private static final int[] MAX_STACKS = {3, 4, 5};
    private static final double PER_STACK = 0.02;

    public MomentumEnchant() { super("momentum", "Momentum", EnchantRarity.RARE, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onKill(EntityDamageByEntityEvent event, Player player, org.bukkit.entity.LivingEntity killed, int level) {
        if (!isEnabled()) return;
        int stacks = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "momentum_stacks");
        int max = cfgi("max_stacks", MAX_STACKS[level-1]);
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "momentum_stacks", Math.min(stacks + 1, max));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player player, org.bukkit.entity.LivingEntity target, int level) {
        if (!isEnabled()) return;
        int stacks = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "momentum_stacks");
        if (stacks > 0) event.setDamage(event.getDamage() * (1.0 + PER_STACK * stacks));
    }

    @Override
    public void onRespawn(Player player, int level) {
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "momentum_stacks", 0);
    }

    @Override public String getDescription() { return "Kills build momentum for bonus damage."; }
    @Override public String getDescription(int level) {
        return "§7Kills: +§a2%§7 dmg/stack (max §a" + MAX_STACKS[level-1] + "§7 stacks, resets on death)."; }
}
