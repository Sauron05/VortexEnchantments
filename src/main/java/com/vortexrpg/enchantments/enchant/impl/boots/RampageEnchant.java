package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Rampage: Kills build rampage stacks increasing damage dealt.
 */
public class RampageEnchant extends VortexEnchant {
    private static final Map<UUID, int[]> STACKS = new HashMap<>();

    public RampageEnchant() { super("rampage", "Rampage", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onKill(EntityDamageByEntityEvent event, Player player, LivingEntity killed, int level) {
        if (!isEnabled()) return;
        int maxStacks = cfgi("max_stacks", 5 + level);
        int[] data = STACKS.computeIfAbsent(player.getUniqueId(), k -> new int[]{0});
        data[0] = Math.min(data[0] + 1, maxStacks);
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        int[] data = STACKS.get(attacker.getUniqueId());
        if (data == null || data[0] == 0) return;
        double bonusPer = cfgd("bonus_per_stack", 1.0 * level);
        event.setDamage(event.getDamage() + data[0] * bonusPer);
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        int[] data = STACKS.get(player.getUniqueId());
        if (data == null || data[0] == 0) return;
        if (Math.random() < cfgd("decay_chance", 0.08)) {
            data[0] = Math.max(0, data[0] - 1);
        }
    }

    @Override public String getDescription(int level) {
        return "§7Kills grant stacks: §c+" + level + ".0 §7damage per stack (max " + (5 + level) + ").";
    }
}
