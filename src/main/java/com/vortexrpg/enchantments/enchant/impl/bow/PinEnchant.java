package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Pin: Target pinned to ground for 1.5/2/2.5s — cannot jump or use elytra.
 */
public class PinEnchant extends VortexEnchant {

    private static final double[] PIN_SECS = {1.5, 2.0, 2.5};

    public PinEnchant() {
        super("pin", "Pin", EnchantRarity.EPIC, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double secs = cfg("pin_duration_seconds", PIN_SECS[level - 1]);
        long expiry = System.currentTimeMillis() + (long)(secs * 1000);
        plugin.getPlayerDataManager().setLong(victim.getUniqueId(), "pin_expiry", expiry);
        if (victim instanceof Player p) {
            p.sendMessage("§c[Pin] §7You're pinned to the ground!");
        }
        // Cancel any upward movement in MovementListener via pin_expiry check
    }

    /** Utility: check if a target is pinned. */
    public static boolean isPinned(LivingEntity entity) {
        long expiry = com.vortexrpg.enchantments.VortexEnchantments.getInstance()
            .getPlayerDataManager().getLong(entity.getUniqueId(), "pin_expiry");
        return System.currentTimeMillis() < expiry;
    }

    @Override
    public String getDescription() { return "Arrow pins the target to the ground briefly."; }

    @Override
    public String getDescription(int level) {
        return "§7Arrow pins target §e" + PIN_SECS[level-1] + "s§7: no jumping, no elytra.";
    }
}
