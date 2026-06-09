package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

/**
 * WindStride: Gain bonus speed the longer you sprint without stopping.
 */
public class WindStrideEnchant extends VortexEnchant {
    private static final java.util.Map<java.util.UUID, Integer> STACKS = new java.util.HashMap<>();

    public WindStrideEnchant() { super("wind_stride", "Wind Stride", EnchantRarity.RARE, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onMove(PlayerMoveEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isSprinting()) {
            STACKS.remove(player.getUniqueId());
            return;
        }
        int stacks = STACKS.getOrDefault(player.getUniqueId(), 0) + 1;
        int maxStacks = cfgi("max_stacks", 5 + level * 2);
        stacks = Math.min(stacks, maxStacks);
        STACKS.put(player.getUniqueId(), stacks);
        float speedBonus = (float)(stacks * cfgd("speed_per_stack", 0.005 * level));
        player.setWalkSpeed(Math.min(0.2f + speedBonus, 1.0f));
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isSprinting()) {
            player.setWalkSpeed(0.2f);
            STACKS.remove(player.getUniqueId());
        }
    }

    @Override public String getDescription(int level) {
        return "§7Sprinting builds speed, up to " + (5 + level * 2) + " stacks.";
    }
}
