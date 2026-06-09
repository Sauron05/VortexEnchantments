package com.vortexrpg.enchantments.enchant.impl.leggings;

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
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Oblivion: Double-sneak to create a void zone that damages and slows all enemies inside.
 */
public class OblivionEnchant extends VortexEnchant {
    public OblivionEnchant() { super("oblivion", "Oblivion", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.isSneaking()) return;
        if (isOnCooldown(player)) return;

        double radius = cfgd("radius", 5.0 + level);
        double dmg = cfgd("damage_per_tick", 1.5 * level);
        int dur = cfgi("zone_ticks", 60 + level * 20);
        int slowDur = cfgi("slow_duration", 40);
        Location center = player.getLocation().clone();

        plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
            if (dur <= 0 || !player.isOnline()) {
                task.cancel();
                return;
            }
            for (LivingEntity e : MathUtil.getNearbyLiving(center, radius)) {
                if (e.equals(player)) continue;
                e.damage(dmg, player);
                e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, slowDur, 1, true, false, true));
            }
            ParticleUtil.drawCircle(center.clone().add(0, 0.1, 0), radius, 25, Particle.DRAGON_BREATH);
        }, 0L, 20L);

        SoundUtil.play(center, Sound.ENTITY_WITHER_SPAWN, 0.4f, 1.5f);
        setCooldownFromConfig(player, "cooldown", 45.0);
    }

    @Override public String getDescription(int level) {
        return "§7Sneak: §5void zone §7damaging + slowing enemies for " + (3 + level) + "s. §845s CD.";
    }
}
