package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

/** Stride: Build stacks while moving (up to 5); each stack = +3/4/5% to attack damage. Resets when idle. */
public class StrideEnchant extends VortexEnchant {
    private static final double[] PER_STACK = {0.03, 0.04, 0.05};

    public StrideEnchant() { super("stride", "Stride", EnchantRarity.RARE, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onMove(PlayerMoveEvent event, Player player, int level) {
        if (!isEnabled() || !event.hasChangedBlock()) return;
        int stacks = Math.min(plugin.getPlayerDataManager().getStrideStacks(player.getUniqueId()) + 1, 5);
        plugin.getPlayerDataManager().setStrideStacks(player.getUniqueId(), stacks);
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player player, org.bukkit.entity.LivingEntity target, int level) {
        if (!isEnabled()) return;
        int stacks = plugin.getPlayerDataManager().getStrideStacks(player.getUniqueId());
        if (stacks == 0) return;
        double bonus = cfg("per_stack", PER_STACK[level-1]) * stacks;
        event.setDamage(event.getDamage() * (1.0 + bonus));
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        // Idle check once per second
        long tick = plugin.getServer().getCurrentTick();
        if (tick % 20 == 0 && plugin.getPlayerDataManager().isIdle(player.getUniqueId(), 2000L)) {
            plugin.getPlayerDataManager().setStrideStacks(player.getUniqueId(), 0);
        }
    }

    @Override public String getDescription() { return "Move to stack bonus attack power."; }
    @Override public String getDescription(int level) {
        return "§7Moving builds §a+"+  (int)(PER_STACK[level-1]*100) + "§a%§7 dmg/stack (max 5)."; }
}
