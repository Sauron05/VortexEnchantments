package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Phase: Every Nth hit passes through armor/shields entirely (pure HP damage).
 * N = 4/3/2 per level.
 */
public class PhaseEnchant extends VortexEnchant {

    private static final int[] HITS_REQUIRED = {4, 3, 2};

    public PhaseEnchant() {
        super("phase", "Phase", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        int required = cfgi("hits_required", HITS_REQUIRED[level - 1]);
        int count = plugin.getPlayerDataManager().incrementHitCount(attacker.getUniqueId(), victim.getUniqueId());

        if (count >= required) {
            plugin.getPlayerDataManager().resetHitCount(attacker.getUniqueId(), victim.getUniqueId());
            // Bypass armor by dealing custom damage after cancelling this event
            double rawDamage = event.getDamage();
            event.setCancelled(true);
            // Schedule on next tick to avoid event recursion
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (victim.isValid() && !victim.isDead()) {
                    // Direct health modification bypasses armor
                    victim.setHealth(Math.max(0.0, victim.getHealth() - rawDamage));
                    if (victim instanceof Player p) {
                        p.sendMessage("§5[Phase] §7Your armor was bypassed!");
                    }
                }
            });
        }
    }

    @Override
    public String getDescription() { return "Every Nth hit bypasses armor and shields for pure damage."; }

    @Override
    public String getDescription(int level) {
        return "§7Every §e" + HITS_REQUIRED[level - 1] + "th§7 hit §cpasses through armor§7 (true damage).";
    }
}
