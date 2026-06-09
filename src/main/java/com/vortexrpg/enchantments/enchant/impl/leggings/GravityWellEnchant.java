package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * GravityWell: When taking heavy damage, pull all nearby enemies towards you and slow them.
 */
public class GravityWellEnchant extends VortexEnchant {
    public GravityWellEnchant() { super("gravity_well", "Gravity Well", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double dmgThreshold = cfgd("damage_threshold", 6.0);
        if (event.getFinalDamage() < dmgThreshold) return;
        if (isOnCooldown(player)) return;

        double radius = cfgd("radius", 6.0 + level);
        int dur = cfgi("slow_duration", 40 + level * 20);
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (e.equals(player)) continue;
            org.bukkit.util.Vector pull = player.getLocation().toVector().subtract(e.getLocation().toVector()).normalize().multiply(0.6 + level * 0.2);
            e.setVelocity(pull);
            e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, dur, level - 1, true, false, true));
        }
        ParticleUtil.drawCircle(player.getLocation().add(0, 0.5, 0), radius, 30, Particle.PORTAL);
        SoundUtil.play(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 0.5f);
        setCooldownFromConfig(player, "cooldown", 25.0);
    }

    @Override public String getDescription(int level) {
        return "§7Heavy hit: pull all enemies within " + (6 + level) + " blocks. §825s CD.";
    }
}
