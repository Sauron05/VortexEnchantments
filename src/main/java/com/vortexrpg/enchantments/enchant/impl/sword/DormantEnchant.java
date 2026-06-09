package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Dormant: -50% damage normally. After 5s+ idle (no swings), next hit deals 300/350/400% damage.
 */
public class DormantEnchant extends VortexEnchant {

    private static final double[] BONUS_MULTIPLIERS = {3.0, 3.5, 4.0};

    public DormantEnchant() {
        super("dormant", "Dormant", EnchantRarity.RARE, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        long idleThresholdMs = (long) (cfg("idle_threshold_seconds", 5.0) * 1000);
        double normalPenalty = cfg("normal_penalty", 0.50);
        double bonusMult = BONUS_MULTIPLIERS[level - 1];

        boolean isIdle = plugin.getPlayerDataManager().isIdle(attacker.getUniqueId(), idleThresholdMs);

        if (isIdle) {
            event.setDamage(event.getDamage() * bonusMult);
            ParticleUtil.spawn(attacker.getLocation().add(0, 1, 0), Particle.SOUL_FIRE_FLAME, 16, 0.4);
            SoundUtil.play(attacker.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 0.6f, 1.5f);
        } else {
            event.setDamage(event.getDamage() * normalPenalty);
        }

        // Record this swing
        plugin.getPlayerDataManager().recordSwing(attacker.getUniqueId());
    }

    @Override
    public String getDescription() { return "-50% damage normally. After 5s idle, next hit deals massive bonus damage."; }

    @Override
    public String getDescription(int level) {
        int[] pcts = {300, 350, 400};
        return "§c-50% §7damage normally. After §e5s §7idle: §a" + pcts[level - 1] + "% §7damage.";
    }
}
