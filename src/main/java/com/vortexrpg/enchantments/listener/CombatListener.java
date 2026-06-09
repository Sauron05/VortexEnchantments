package com.vortexrpg.enchantments.listener;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.data.PlayerDataManager;
import com.vortexrpg.enchantments.enchant.EnchantManager;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;

/**
 * Handles combat-specific tracking (swing timestamps, grudge damage, etc.)
 */
public class CombatListener implements Listener {

    @SuppressWarnings("unused") // Reserved for future combat hooks
    private final VortexEnchantments plugin;
    private final PlayerDataManager data;
    @SuppressWarnings("unused") // Reserved for future combat hooks
    private final EnchantManager manager;

    public CombatListener(VortexEnchantments plugin) {
        this.plugin = plugin;
        this.data = plugin.getPlayerDataManager();
        this.manager = plugin.getEnchantManager();
    }

    /** Track arm swing for Dormant, Entropy, and Backswing */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSwing(PlayerAnimationEvent event) {
        if (event.getAnimationType() != PlayerAnimationType.ARM_SWING) return;
        data.recordSwing(event.getPlayer().getUniqueId());
    }

    /** Track damage received for Grudge and Riposte Shot */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamageReceived(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        data.recordMeleeDamageReceived(victim.getUniqueId());

        // Grudge: store damage dealt by each attacker
        if (event.getDamager() instanceof LivingEntity attacker) {
            data.addGrudgeDamage(victim.getUniqueId(), attacker.getUniqueId(), event.getFinalDamage());
        }

        // Verdict: track recent attackers on the entity being hit
        if (event.getDamager() instanceof Player attacker) {
            data.recordEntityAttacker(event.getEntity().getUniqueId(), attacker.getUniqueId());
        }
    }
}
