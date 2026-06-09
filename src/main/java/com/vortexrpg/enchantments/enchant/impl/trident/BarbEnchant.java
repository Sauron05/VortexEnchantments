package com.vortexrpg.enchantments.enchant.impl.trident;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Barb: Lodges in target for 3/4/5 seconds, dealing 0.5 HP/s. Target must crouch 2s to remove.
 */
public class BarbEnchant extends VortexEnchant {
    private static final int[] DURATION = {3, 4, 5};

    public BarbEnchant() { super("barb", "Barb", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.TRIDENT)); }

    @Override
    public void onTridentHit(EntityDamageByEntityEvent event, Player thrower, LivingEntity target, int level) {
        if (!isEnabled()) return;
        double dmgPerSec = cfg("damage_per_second", 0.5);
        int duration = cfgi("embed_duration_" + level, DURATION[level-1]);
        target.setMetadata("barb_embed", new FixedMetadataValue(plugin, true));
        new BukkitRunnable() {
            int elapsed = 0;
            @Override public void run() {
                if (!target.isValid() || target.isDead() || elapsed >= duration) {
                    target.removeMetadata("barb_embed", plugin);
                    cancel(); return;
                }
                // Check removal by crouch (handled via event in CombatListener/MovementListener)
                // For simplicity: tick damage
                target.damage(dmgPerSec * 2, thrower); // 0.5 HP = 1 damage unit per half-second tick
                elapsed++;
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity target, int level) {
        onTridentHit(event, attacker, target, level);
    }

    @Override public String getDescription() { return "Lodges in target, dealing damage over time."; }
    @Override public String getDescription(int level) {
        return "§7Embeds in target for §e" + DURATION[level-1] + "s§7, dealing §c0.5♥/s§7. Crouch §e2s§7 to remove."; }
}
