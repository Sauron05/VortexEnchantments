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
 * Duskfire: During dusk/dawn (11500-13500 or 22500-24000), bolt deals 2x damage
 * and applies both Fire and Wither.
 */
public class DuskfireEnchant extends VortexEnchant {

    public DuskfireEnchant() {
        super("duskfire", "Duskfire", EnchantRarity.EPIC, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        long time = shooter.getWorld().getTime();
        boolean isDusk = time >= 11500 && time <= 13500;
        boolean isDawn = time >= 22500 || time <= 500;
        if (!isDusk && !isDawn) return;

        double multiplier = cfgd("multiplier", 1.0 + level * 0.5);
        event.setDamage(event.getDamage() * multiplier);

        int fireTicks = cfgi("fire_ticks", 60);
        int witherDuration = cfgi("wither_duration", 40 + level * 20);

        victim.setFireTicks(fireTicks);
        victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, witherDuration, 0, false, true));

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SOUL_FIRE_FLAME, 12, 0.4);
    }

    @Override
    public String getDescription(int level) {
        double mult = 1.0 + level * 0.5;
        return "§7Dusk/dawn: §6" + mult + "x damage §7+ §6fire §7+ §8wither§7.";
    }
}
