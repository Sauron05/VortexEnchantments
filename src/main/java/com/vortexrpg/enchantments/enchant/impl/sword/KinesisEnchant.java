package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Kinesis: Converts sprint velocity into bonus damage.
 * Standing still = +0%. Full sprint = +40/50/60%.
 */
public class KinesisEnchant extends VortexEnchant {

    private static final double[] MAX_BONUS = {0.40, 0.50, 0.60};
    // Sprint velocity is approximately 0.28 blocks/tick
    private static final double SPRINT_VELOCITY = 0.28;

    public KinesisEnchant() {
        super("kinesis", "Kinesis", EnchantRarity.RARE, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double threshold = cfg("velocity_threshold", 0.05);
        double maxBonus = MAX_BONUS[level - 1];

        Vector vel = attacker.getVelocity();
        double speed = Math.sqrt(vel.getX() * vel.getX() + vel.getZ() * vel.getZ());

        if (speed < threshold) return;

        double ratio = Math.min(speed / SPRINT_VELOCITY, 1.0);
        double bonus = ratio * maxBonus;
        event.setDamage(event.getDamage() * (1.0 + bonus));
    }

    @Override
    public String getDescription() { return "Sprint velocity converts to bonus damage. Standing still = no bonus."; }

    @Override
    public String getDescription(int level) {
        int pct = (int) (MAX_BONUS[level - 1] * 100);
        return "Full sprint = §a+" + pct + "% §7damage. Scales with movement speed.";
    }
}
