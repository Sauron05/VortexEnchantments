package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;

import java.util.List;

/**
 * Catalyst Helm: Each consecutive hit you take from the SAME attacker
 * reduces that attacker's damage by stacking X%.
 */
public class CatalystHelmEnchant extends VortexEnchant {
    private static final Map<String, Integer> STACKS = new HashMap<>();

    public CatalystHelmEnchant() { super("catalyst_helm", "Catalyst Helm", EnchantRarity.RARE, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        String key = victim.getUniqueId() + ":" + attacker.getUniqueId();
        int stacks = STACKS.merge(key, 1, (a, b) -> a + b);
        int maxStacks = cfgi("max_stacks", 5);
        stacks = Math.min(stacks, maxStacks);
        double perStack = cfgd("reduce_per_stack", 0.04 + level * 0.02);
        double totalReduce = stacks * perStack;
        event.setDamage(event.getDamage() * (1.0 - totalReduce));
    }

    @Override public String getDescription(int level) {
        int pct = (int) ((0.04 + level * 0.02) * 100);
        return "§7Each consecutive hit from same attacker: §a-" + pct + "%§7 per stack (max 5).";
    }
}
