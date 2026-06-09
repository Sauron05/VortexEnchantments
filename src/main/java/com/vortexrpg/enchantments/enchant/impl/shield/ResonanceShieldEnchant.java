package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Resonance Shield: Consecutive blocks amplify damage reduction. */
public class ResonanceShieldEnchant extends VortexEnchant {

    public ResonanceShieldEnchant() { super("resonance_shield", "Resonance Shield", EnchantRarity.EPIC, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        String countKey = "resonance_count";
        String timeKey = "resonance_time";
        long now = System.currentTimeMillis();
        long lastBlock = plugin.getPlayerDataManager().getLong(player.getUniqueId(), timeKey, 0L);
        int stacks;
        if (now - lastBlock < 3000) {
            stacks = Math.min(plugin.getPlayerDataManager().getInt(player.getUniqueId(), countKey) + 1,
                    cfgi("max-stacks", 3 + level));
        } else {
            stacks = 1;
        }
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), countKey, stacks);
        plugin.getPlayerDataManager().setLong(player.getUniqueId(), timeKey, now);
        double reductionPerStack = cfg("reduction-per-stack", 0.05 + level * 0.02);
        double totalReduction = stacks * reductionPerStack;
        event.setDamage(event.getDamage() * (1.0 - totalReduction));
    }

    @Override public String getDescription() { return "Consecutive blocks amplify reduction."; }
    @Override public String getDescription(int level) {
        return "§7Block: §a+" + (int)((0.05 + level * 0.02) * 100) + "%§7 reduction per consecutive block (max " + (3 + level) + ")."; }
}
