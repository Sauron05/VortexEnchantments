package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Rend: On hit, apply stacking bleed (0.5♥/2s per stack). Max 3/4/5 stacks. Stacks add.
 */
public class RendEnchant extends VortexEnchant {

    private static final int[] MAX_STACKS = {3, 4, 5};

    public RendEnchant() {
        super("rend", "Rend", EnchantRarity.EPIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        var pdm = plugin.getPlayerDataManager();
        int maxStacks = cfgi("max_stacks", MAX_STACKS[level - 1]);
        int current = pdm.getBleedStacks(victim.getUniqueId());

        if (current >= maxStacks) return;

        int newStacks = pdm.addBleedStack(victim.getUniqueId());
        double damagePerStack = cfg("damage_per_stack", 1.0); // 0.5 hearts = 1.0 raw
        long tickInterval = cfgi("tick_interval", 40); // 2 seconds
        long durationTicks = cfgi("duration_seconds", 10) * 20L;

        // Start bleed task only on first stack
        if (newStacks == 1) {
            long[] elapsed = {0};
            plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
                elapsed[0] += tickInterval;
                if (!victim.isValid() || victim.isDead() || elapsed[0] > durationTicks) {
                    pdm.clearBleedStacks(victim.getUniqueId());
                    task.cancel();
                    return;
                }
                int stacks = pdm.getBleedStacks(victim.getUniqueId());
                if (stacks <= 0) { task.cancel(); return; }
                victim.damage(damagePerStack * stacks, attacker);
            }, tickInterval, tickInterval);
        }
    }

    @Override
    public String getDescription() { return "Applies stacking bleed that deals damage every 2 seconds."; }

    @Override
    public String getDescription(int level) {
        return "§cBleed stacks§7 (max §e" + MAX_STACKS[level-1] + "§7): deal §c0.5♥§7 per stack every §e2s§7.";
    }
}
