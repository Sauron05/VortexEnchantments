package com.vortexrpg.enchantments.enchant.impl.crossbow;

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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Blackout: Bolt creates a Darkness sphere (4/6/8 blocks) for 3 seconds.
 * All entities inside get Darkness effect — total visual shutdown.
 */
public class BlackoutEnchant extends VortexEnchant {

    public BlackoutEnchant() {
        super("blackout", "Blackout", EnchantRarity.RARE, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(shooter)) return;

        double radius = cfgd("radius", 2.0 + level * 2.0);
        int duration = cfgi("duration", 60);
        Location center = victim.getLocation();

        for (LivingEntity entity : MathUtil.getNearbyLiving(center, radius)) {
            if (entity.equals(shooter)) continue;
            entity.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, duration, 0, false, false));
        }

        ParticleUtil.drawCircle(center, radius, 20, Particle.SMOKE);
        ParticleUtil.spawn(center, Particle.LARGE_SMOKE, 15, radius * 0.5);
        SoundUtil.play(center, Sound.ENTITY_WARDEN_AMBIENT, 0.7f, 0.5f);

        setCooldownFromConfig(shooter, "cooldown", 8.0);
    }

    @Override
    public String getDescription(int level) {
        int r = (int) (2 + level * 2);
        return "§7Bolt: §8§lDARKNESS §7sphere §e" + r + " blocks §7for 3s. 8s CD.";
    }
}
