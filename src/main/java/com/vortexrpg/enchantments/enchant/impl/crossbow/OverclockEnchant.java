package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Overclock: After hitting a target, crossbow reload speed is doubled for 3s.
 * Grants Haste II temporarily for faster follow-up shots.
 */
public class OverclockEnchant extends VortexEnchant {

    public OverclockEnchant() {
        super("overclock", "Overclock", EnchantRarity.RARE, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int duration = cfgi("duration", (1 + level)) * 20;
        int amp = cfgi("haste_amp", 1);
        shooter.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, duration, amp, false, true));

        ParticleUtil.spawn(shooter.getLocation(), Particle.ELECTRIC_SPARK, 6, 0.3);
    }

    @Override
    public String getDescription(int level) {
        int dur = 1 + level;
        return "§7Hit: §eHaste II §7for §e" + dur + "s §7— faster reload.";
    }
}
