package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

/**
 * Deadeye: Crouching 2/2.5/3s before firing grants +30/40/50% damage and removes gravity on the bolt.
 */
public class DeadeyeEnchant extends VortexEnchant {
    private static final double[] CROUCH_THRESHOLD = {2.0, 2.5, 3.0};
    private static final double[] DAMAGE_BONUS = {0.30, 0.40, 0.50};

    public DeadeyeEnchant() { super("deadeye", "Deadeye", EnchantRarity.EPIC, 3, List.of(ItemTarget.CROSSBOW)); }

    @Override
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;
        long crouchMs = plugin.getPlayerDataManager().getCrouchDurationMs(shooter.getUniqueId());
        double threshold = cfg("crouch_threshold_" + level, CROUCH_THRESHOLD[level-1]);
        if (crouchMs >= threshold * 1000) {
            if (event.getProjectile() instanceof AbstractArrow arrow) {
                arrow.setGravity(false);
                arrow.setMetadata("deadeye_bonus", new FixedMetadataValue(plugin, DAMAGE_BONUS[level-1]));
            }
        }
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity target, int level) {
        if (!isEnabled()) return;
        if (!(event.getDamager() instanceof AbstractArrow arrow)) return;
        if (!arrow.hasMetadata("deadeye_bonus")) return;
        double bonus = (double) arrow.getMetadata("deadeye_bonus").get(0).value();
        event.setDamage(event.getDamage() * (1.0 + bonus));
    }

    @Override public String getDescription() { return "Crouch before firing for a deadly precision shot."; }
    @Override public String getDescription(int level) {
        return "§7Crouch §e" + CROUCH_THRESHOLD[level-1] + "s§7 before firing: §c+" + (int)(DAMAGE_BONUS[level-1]*100)
               + "%§7 damage, §bzero gravity§7 bolt."; }
}
