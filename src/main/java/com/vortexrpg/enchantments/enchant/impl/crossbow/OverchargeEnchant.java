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
 * Overcharge: The longer the player holds the crossbow after loading, the more damage is dealt.
 * +20/22/25% damage per second held, tracked via PDM hold start time.
 */
public class OverchargeEnchant extends VortexEnchant {
    private static final double[] BONUS_PER_SEC = {0.20, 0.22, 0.25};
    public OverchargeEnchant() { super("overcharge", "Overcharge", EnchantRarity.RARE, 3, List.of(ItemTarget.CROSSBOW)); }

    @Override
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;
        long holdStart = plugin.getPlayerDataManager().getLong(shooter.getUniqueId(), "overcharge_hold_start");
        if (holdStart <= 0) {
            plugin.getPlayerDataManager().setLong(shooter.getUniqueId(), "overcharge_hold_start", System.currentTimeMillis());
            return;
        }
        double elapsedSec = (System.currentTimeMillis() - holdStart) / 1000.0;
        double multiplier = 1.0 + Math.min(BONUS_PER_SEC[level-1] * elapsedSec, cfg("max_bonus_" + level, 1.5));
        if (event.getProjectile() instanceof AbstractArrow arrow) {
            arrow.setMetadata("overcharge_mult", new FixedMetadataValue(plugin, multiplier));
        }
        plugin.getPlayerDataManager().setLong(shooter.getUniqueId(), "overcharge_hold_start", 0L);
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity target, int level) {
        if (!isEnabled()) return;
        if (!(event.getDamager() instanceof AbstractArrow arrow)) return;
        if (!arrow.hasMetadata("overcharge_mult")) return;
        double mult = (double) arrow.getMetadata("overcharge_mult").get(0).value();
        event.setDamage(event.getDamage() * mult);
    }

    @Override public String getDescription() { return "Hold after loading to overcharge your bolt."; }
    @Override public String getDescription(int level) {
        return "§7Hold after loading: §a+" + (int)(BONUS_PER_SEC[level-1]*100) + "%§7 damage per second charged."; }
}
