package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Banshee: On kill, emit a scream that applies Darkness and Slowness
 * to all hostiles within 8/10/12 blocks for 3 seconds.
 */
public class BansheeEnchant extends VortexEnchant {

    public BansheeEnchant() {
        super("banshee", "Banshee", EnchantRarity.EPIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity killed, int level) {
        if (!isEnabled()) return;

        double radius = cfgd("radius", 8.0) + (level - 1) * 2.0;
        int durationTicks = cfgi("effect_duration_ticks", 60);
        int slowLevel = cfgi("slow_amplifier", level - 1);

        ParticleUtil.drawCircle(killed.getLocation(), radius, 30, Particle.SCULK_SOUL);
        ParticleUtil.spawn(killed.getLocation().add(0, 1, 0), Particle.SONIC_BOOM, 1, 0.1);
        SoundUtil.play(killed.getLocation(), Sound.ENTITY_WARDEN_ROAR, 1.0f, 2.0f);

        int affected = 0;
        for (Entity e : killed.getNearbyEntities(radius, radius, radius)) {
            if (e.equals(killer) || !(e instanceof LivingEntity le)) continue;
            le.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, durationTicks, 0, false, false));
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, durationTicks, slowLevel, false, false));
            affected++;
        }

        if (affected > 0) {
            killer.sendMessage("§5[Banshee] §7Scream terrified §e" + affected + "§7 enemies!");
        }
    }

    @Override
    public String getDescription(int level) {
        int radius = 8 + (level - 1) * 2;
        return "§7On kill: §5banshee scream§7 applies Darkness + Slowness in §e" + radius + " blocks§7.";
    }
}
