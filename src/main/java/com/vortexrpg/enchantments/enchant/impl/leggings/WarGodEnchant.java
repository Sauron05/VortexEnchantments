package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * WarGod: Kills build stacks, each increasing damage and reducing damage taken.
 */
public class WarGodEnchant extends VortexEnchant {
    private static final Map<UUID, int[]> STACKS = new HashMap<>();

    public WarGodEnchant() { super("war_god", "War God", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onKill(EntityDamageByEntityEvent event, Player player, LivingEntity killed, int level) {
        if (!isEnabled()) return;
        int maxStacks = cfgi("max_stacks", 3 + level);
        int[] data = STACKS.computeIfAbsent(player.getUniqueId(), k -> new int[]{0});
        data[0] = Math.min(data[0] + 1, maxStacks);
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        int[] data = STACKS.get(attacker.getUniqueId());
        if (data == null || data[0] == 0) return;
        double bonusPer = cfgd("bonus_per_stack", 0.8 * level);
        event.setDamage(event.getDamage() + data[0] * bonusPer);
    }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        int[] data = STACKS.get(victim.getUniqueId());
        if (data == null || data[0] == 0) return;
        double reductionPer = cfgd("reduction_per_stack", 0.03 * level);
        event.setDamage(event.getDamage() * (1.0 - Math.min(data[0] * reductionPer, 0.5)));
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        // Stacks decay over time
        int[] data = STACKS.get(player.getUniqueId());
        if (data == null || data[0] == 0) return;
        if (Math.random() < cfgd("decay_chance", 0.05)) {
            data[0] = Math.max(0, data[0] - 1);
        }
    }

    @Override public String getDescription(int level) {
        return "§7Kills grant stacks: §c+" + String.format("%.1f", 0.8 * level) + " dmg §7and §a-" + (3 * level) + "% §7taken per stack.";
    }
}
