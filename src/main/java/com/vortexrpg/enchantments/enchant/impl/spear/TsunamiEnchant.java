package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Tsunami: Thrown hit creates a 3/5/7 block wide wave that knocks back
 * all nearby entities 4 blocks and deals 2 bonus damage.
 */
public class TsunamiEnchant extends VortexEnchant {

    public TsunamiEnchant() {
        super("tsunami", "Tsunami", EnchantRarity.EPIC, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(shooter)) return;

        double radius = cfgd("radius", 1.0 + level * 2.0);
        double bonusDmg = cfgd("bonus_damage", 2.0);

        Location impact = victim.getLocation();
        ParticleUtil.drawCircle(impact, radius, 24, Particle.SPLASH);
        ParticleUtil.spawn(impact, Particle.SPLASH, 40, radius * 0.5);
        SoundUtil.play(impact, Sound.ENTITY_PLAYER_SPLASH_HIGH_SPEED, 1.0f, 0.6f);

        for (LivingEntity le : MathUtil.getNearbyLiving(impact, radius)) {
            if (le.equals(shooter)) continue;
            le.damage(bonusDmg, shooter);

            Vector push = le.getLocation().toVector().subtract(impact.toVector()).normalize()
                    .multiply(1.2).setY(0.5);
            le.setVelocity(push);
        }

        setCooldownFromConfig(shooter, "cooldown", 12);
    }

    @Override
    public String getDescription(int level) {
        int radius = 1 + level * 2;
        return "§7Thrown hit: §btidal wave §7(" + radius + " blocks), knockback + §c2 damage§7.";
    }
}
