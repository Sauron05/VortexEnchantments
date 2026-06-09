package com.vortexrpg.enchantments.enchant.impl.hammer;

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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Warden: Critical hits apply Darkness for 3 seconds + deal sonic damage that ignores armor.
 * Inspired by the Warden's sonic boom.
 */
public class WardenEnchant extends VortexEnchant {

    public WardenEnchant() {
        super("warden", "Warden", EnchantRarity.EPIC, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        // Check critical hit: falling + not on ground
        if (attacker.getFallDistance() <= 0) return;

        int darkDuration = cfgi("darkness_duration", 60);
        double sonicDmg = cfgd("sonic_damage", 2.0 + level * 2.0);

        victim.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, darkDuration, 0, false, true));

        // Sonic damage bypasses armor
        double health = victim.getHealth();
        double newHealth = Math.max(0, health - sonicDmg);
        victim.setHealth(newHealth);

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SONIC_BOOM, 1, 0.1);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 0.6f, 1.0f);
    }

    @Override
    public String getDescription(int level) {
        double d = 2 + level * 2;
        return "§7Crit: §8Darkness 3s §7+ §c" + d + " sonic dmg §8(ignores armor).";
    }
}
