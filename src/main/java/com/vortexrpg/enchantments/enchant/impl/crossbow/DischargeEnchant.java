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
 * Discharge: The first bolt after a full reload deals +40/50/60% bonus damage.
 */
public class DischargeEnchant extends VortexEnchant {
    private static final double[] BONUS = {0.40, 0.50, 0.60};

    public DischargeEnchant() { super("discharge", "Discharge", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.CROSSBOW)); }

    @Override
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;
        if (!plugin.getPlayerDataManager().isFirstShotReady(shooter.getUniqueId())) return;
        plugin.getPlayerDataManager().setFirstShotReady(shooter.getUniqueId(), false);
        if (event.getProjectile() instanceof AbstractArrow arrow) {
            arrow.setMetadata("discharge_bonus", new FixedMetadataValue(plugin, BONUS[level-1]));
        }
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity target, int level) {
        if (!isEnabled()) return;
        if (!(event.getDamager() instanceof AbstractArrow arrow)) return;
        if (!arrow.hasMetadata("discharge_bonus")) return;
        double bonus = (double) arrow.getMetadata("discharge_bonus").get(0).value();
        event.setDamage(event.getDamage() * (1.0 + bonus));
    }

    @Override public String getDescription() { return "First bolt after full reload hits harder."; }
    @Override public String getDescription(int level) {
        return "§7First bolt after full reload: §c+" + (int)(BONUS[level-1]*100) + "%§7 damage."; }
}
