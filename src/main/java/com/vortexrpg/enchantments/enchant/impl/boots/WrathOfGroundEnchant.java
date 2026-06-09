package com.vortexrpg.enchantments.enchant.impl.boots;

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

import java.util.List;

/**
 * WrathOfGround: Landing from a big fall deals massive AOE + stun.
 */
public class WrathOfGroundEnchant extends VortexEnchant {
    public WrathOfGroundEnchant() { super("wrath_of_ground", "Wrath of Ground", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (event.getDamage() < cfgd("min_fall_damage", 5.0)) return;

        double radius = cfgd("radius", 6.0 + level);
        double dmg = cfgd("aoe_damage", 3.0 * level);
        int stunDur = cfgi("stun_duration", 30 + level * 10);
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (e.equals(player)) continue;
            e.damage(dmg, player);
            e.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SLOWNESS, stunDur, 3, true, false, true));
            e.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.BLINDNESS, stunDur, 0, true, false, true));
        }
        event.setDamage(event.getDamage() * cfgd("self_reduction", 0.5));
        ParticleUtil.burst(player.getLocation(), Particle.CAMPFIRE_COSY_SMOKE, 30, radius);
        SoundUtil.play(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.8f, 0.5f);
    }

    @Override public String getDescription(int level) {
        return "§7Big falls deal §c" + (3 * level) + " §7AOE + stun. Self fall damage halved.";
    }
}
