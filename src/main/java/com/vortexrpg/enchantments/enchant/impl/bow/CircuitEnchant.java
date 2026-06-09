package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Circuit: Hit same target twice within 3/4/5s → lightning chain dealing 2/3/4♥ to everything between.
 */
public class CircuitEnchant extends VortexEnchant {

    private static final int[] WINDOW_SECS = {3, 4, 5};
    private static final double[] CHAIN_DAMAGE = {4.0, 6.0, 8.0};

    public CircuitEnchant() {
        super("circuit", "Circuit", EnchantRarity.EPIC, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        var pdm = plugin.getPlayerDataManager();
        long windowMs = cfgi("window_seconds", WINDOW_SECS[level - 1]) * 1000L;
        long lastHit = pdm.getCircuitLastHitTime(shooter.getUniqueId(), victim.getUniqueId());
        var lastLoc = pdm.getCircuitLastHitLoc(shooter.getUniqueId(), victim.getUniqueId());

        long now = System.currentTimeMillis();
        if (lastHit > 0 && (now - lastHit) < windowMs && lastLoc != null) {
            // Chain!
            double chainDmg = cfg("chain_damage", CHAIN_DAMAGE[level - 1]);
            victim.getWorld().strikeLightningEffect(victim.getLocation());
            victim.getWorld().strikeLightningEffect(lastLoc);

            // Damage entities between the two locations
            var midpoint = lastLoc.clone().add(victim.getLocation()).multiply(0.5);
            for (LivingEntity e : MathUtil.getNearbyLiving(midpoint, lastLoc.distance(victim.getLocation()) + 1.0)) {
                if (!e.equals(shooter)) e.damage(chainDmg, shooter);
            }
            pdm.recordCircuitHit(shooter.getUniqueId(), victim.getUniqueId(), victim.getLocation());
        } else {
            pdm.recordCircuitHit(shooter.getUniqueId(), victim.getUniqueId(), victim.getLocation());
        }
    }

    @Override
    public String getDescription() { return "Hitting the same target twice creates a deadly lightning chain."; }

    @Override
    public String getDescription(int level) {
        return "§7Two hits on same target within §e" + WINDOW_SECS[level-1] + "s§7: §elightning chain§7 deals §c" + (int)(CHAIN_DAMAGE[level-1]/2) + "♥§7.";
    }
}
