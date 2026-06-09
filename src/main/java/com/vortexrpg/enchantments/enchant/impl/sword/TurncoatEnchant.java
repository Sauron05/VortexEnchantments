package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Turncoat: On hit, chance to make target hostile toward its own kind for 8s.
 */
public class TurncoatEnchant extends VortexEnchant {

    private static final double[] CHANCES = {5.0, 8.0, 12.0};

    public TurncoatEnchant() {
        super("turncoat", "Turncoat", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double chance = CHANCES[level - 1];
        double durationSecs = cfg("duration_seconds", 8.0);

        if (!MathUtil.chance(chance)) return;
        if (!(victim instanceof Mob mob)) return;

        // Find nearest same-type mob and set as target
        EntityType victimType = victim.getType();
        LivingEntity nearestSameType = victim.getWorld()
            .getNearbyLivingEntities(victim.getLocation(), 12.0).stream()
            .filter(e -> e != victim && e.getType() == victimType && e instanceof Mob)
            .min((a, b) -> Double.compare(
                a.getLocation().distanceSquared(victim.getLocation()),
                b.getLocation().distanceSquared(victim.getLocation())))
            .orElse(null);

        if (nearestSameType != null) {
            mob.setTarget(nearestSameType);
            // Reset after duration
            long durationTicks = MathUtil.secondsToTicks(durationSecs);
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (mob.isValid() && !mob.isDead()) {
                    mob.setTarget(null);
                }
            }, durationTicks);
            ParticleUtil.spawn(victim.getLocation().add(0, 2, 0), Particle.ANGRY_VILLAGER, 3, 0.3);
        }
    }

    @Override
    public String getDescription() { return "Chance on hit to turn target hostile toward its own kind."; }

    @Override
    public String getDescription(int level) {
        int pct = (int) CHANCES[level - 1];
        return "§e" + pct + "% §7chance to make target attack its own kind for §e8s§7.";
    }
}
