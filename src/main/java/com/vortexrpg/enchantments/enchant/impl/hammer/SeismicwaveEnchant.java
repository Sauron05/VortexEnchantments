package com.vortexrpg.enchantments.enchant.impl.hammer;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Seismicwave: Right-click to slam the ground, creating a fissure line forward dealing damage + Slowness.
 * 10-second cooldown.
 */
public class SeismicwaveEnchant extends VortexEnchant {

    public SeismicwaveEnchant() {
        super("seismicwave", "Seismicwave", EnchantRarity.RARE, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;

        double length = cfgd("length", 5.0 + level * 2.0);
        double damage = cfgd("damage", 2.0 + level);
        int slowDur = cfgi("slow_duration", 40 + level * 20);

        Vector dir = player.getLocation().getDirection().setY(0).normalize();
        Location start = player.getLocation().clone();

        for (double d = 1; d <= length; d += 1.0) {
            Location point = start.clone().add(dir.clone().multiply(d));
            ParticleUtil.spawn(point, Particle.DUST_PLUME, 4, 0.3);

            for (var entity : point.getWorld().getNearbyEntities(point, 1.0, 1.5, 1.0)) {
                if (entity.equals(player)) continue;
                if (entity instanceof LivingEntity living) {
                    living.damage(damage, player);
                    living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, slowDur, 1, false, false));
                }
            }
        }

        SoundUtil.play(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1.5f);
        setCooldownFromConfig(player, "cooldown", 10);
    }

    @Override
    public String getDescription(int level) {
        double len = 5 + level * 2;
        return "§7Right-click: fissure line §e" + (int) len + " blocks §7forward. §8(10s CD)";
    }
}
