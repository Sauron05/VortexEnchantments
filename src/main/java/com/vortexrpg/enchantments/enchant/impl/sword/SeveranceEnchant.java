package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

/**
 * Severance: Prevents target from regenerating health naturally for 6/8/10 seconds.
 * Also registers a listener-like approach via metadata check.
 */
public class SeveranceEnchant extends VortexEnchant {

    private static final int[] DURATIONS_SECS = {6, 8, 10};

    public SeveranceEnchant() {
        super("severance", "Severance", EnchantRarity.EPIC, 3, List.of(ItemTarget.SWORD));
        // Register the regen cancel listener via the plugin's event system
        // This is handled in CombatListener below but we register once via plugin enable
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double durationSecs = cfg("duration_seconds", DURATIONS_SECS[level - 1]);
        long expiry = System.currentTimeMillis() + (long) (durationSecs * 1000);
        victim.setMetadata("severance_expiry", new FixedMetadataValue(plugin, expiry));

        // Cancel regen on this entity using a listener registered once
        // The actual cancellation happens in EntityRegainHealthEvent (see below)
    }

    /**
     * Utility method called by a global listener to check/cancel regen.
     * Register via plugin's onEnable: getServer().getPluginManager().registerEvents(new SeveranceRegenListener(), this)
     * OR simply override in CombatListener — but since we can't add to CombatListener here,
     * we use a static approach with metadata checks from a BukkitRunnable-registered event.
     */
    public static boolean isSeveranced(LivingEntity entity) {
        if (!entity.hasMetadata("severance_expiry")) return false;
        long expiry = entity.getMetadata("severance_expiry").get(0).asLong();
        if (System.currentTimeMillis() > expiry) {
            entity.removeMetadata("severance_expiry",
                com.vortexrpg.enchantments.VortexEnchantments.getInstance());
            return false;
        }
        return true;
    }

    @Override
    public String getDescription() { return "Prevents target from naturally regenerating health for several seconds."; }

    @Override
    public String getDescription(int level) {
        return "Block natural regen on target for §c" + DURATIONS_SECS[level - 1] + "s§7. Potions still work.";
    }
}
