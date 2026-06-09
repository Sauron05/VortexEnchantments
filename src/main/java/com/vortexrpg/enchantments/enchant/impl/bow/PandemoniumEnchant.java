package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Pandemonium: Arrow explodes into 8/12/16 mini-arrows in all directions on impact.
 * Each mini-arrow deals 30% damage. Absolute chaos.
 */
public class PandemoniumEnchant extends VortexEnchant {

    public PandemoniumEnchant() {
        super("pandemonium", "Pandemonium", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(shooter)) return;

        int count = cfgi("arrow_count", 4 + level * 4);
        double dmgPct = cfgd("mini_damage_pct", 0.30);
        double baseDmg = event.getDamage() * dmgPct;
        Location center = victim.getLocation().add(0, 1, 0);

        for (int i = 0; i < count; i++) {
            double yaw = ThreadLocalRandom.current().nextDouble(360);
            double pitch = ThreadLocalRandom.current().nextDouble(-30, 30);
            double radYaw = Math.toRadians(yaw);
            double radPitch = Math.toRadians(pitch);

            Vector dir = new Vector(
                    Math.cos(radPitch) * Math.sin(radYaw),
                    Math.sin(radPitch),
                    Math.cos(radPitch) * Math.cos(radYaw)
            ).normalize().multiply(1.2);

            Arrow mini = center.getWorld().spawn(center, Arrow.class);
            mini.setShooter(shooter);
            mini.setVelocity(dir);
            mini.setDamage(baseDmg);
            mini.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        }

        ParticleUtil.burst(center, Particle.EXPLOSION, 3, 1.0);
        SoundUtil.play(center, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 0.5f);

        setCooldownFromConfig(shooter, "cooldown", 10.0);
    }

    @Override
    public String getDescription(int level) {
        int count = 4 + level * 4;
        return "§7Arrow: §c§lPANDEMONIUM §7— §e" + count + " mini-arrows §7in all directions. 10s CD.";
    }
}
