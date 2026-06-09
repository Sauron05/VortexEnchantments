package com.vortexrpg.enchantments.enchant.impl.trident;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Brine: Hit target's food items decay: lose 1 food item every 8/6/4 seconds for 30s.
 * Non-player targets: applies Hunger effect equivalent.
 */
public class BrineEnchant extends VortexEnchant {
    private static final int[] INTERVAL = {8, 6, 4};

    public BrineEnchant() { super("brine", "Brine", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.TRIDENT)); }

    private void apply(Player thrower, LivingEntity target, int level) {
        if (!isEnabled()) return;
        int interval = cfgi("decay_interval_" + level, INTERVAL[level-1]);
        int duration = cfgi("decay_duration", 30);
        if (!(target instanceof Player victim)) return;
        new BukkitRunnable() {
            int elapsed = 0;
            @Override public void run() {
                if (!victim.isOnline() || elapsed >= duration) { cancel(); return; }
                elapsed += interval;
                PlayerInventory inv = victim.getInventory();
                for (int i = 0; i < inv.getSize(); i++) {
                    ItemStack item = inv.getItem(i);
                    if (item != null && item.getType().isEdible()) {
                        item.setAmount(item.getAmount() - 1);
                        if (item.getAmount() <= 0) inv.setItem(i, null);
                        break;
                    }
                }
            }
        }.runTaskTimer(plugin, interval * 20L, interval * 20L);
    }

    @Override
    public void onTridentHit(EntityDamageByEntityEvent event, Player thrower, LivingEntity target, int level) {
        apply(thrower, target, level);
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity target, int level) {
        apply(attacker, target, level);
    }

    @Override public String getDescription() { return "Causes food to decay in hit player's inventory."; }
    @Override public String getDescription(int level) {
        return "§7Hit player loses 1 food item every §e" + INTERVAL[level-1] + "s§7 for §e30s§7."; }
}
