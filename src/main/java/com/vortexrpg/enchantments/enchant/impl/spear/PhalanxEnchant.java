package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Phalanx: While sneaking, allies within 5/7/9 blocks gain damage resistance
 * (10/15/20%). Emits a shield aura effect.
 */
public class PhalanxEnchant extends VortexEnchant {

    public PhalanxEnchant() {
        super("phalanx", "Phalanx", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.isSneaking()) return;

        double radius = cfgd("radius", 3.0 + level * 2.0);
        int amplifier = level - 1; // Resistance I/II/III
        int durationTicks = cfgi("duration_ticks", 60);

        for (var entity : player.getNearbyEntities(radius, radius, radius)) {
            if (!(entity instanceof Player ally)) continue;
            ally.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, durationTicks, amplifier, false, true));
        }
        // Also apply to self
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, durationTicks, amplifier, false, true));

        ParticleUtil.drawCircle(player.getLocation(), radius, 24, Particle.ENCHANTED_HIT);
    }

    @Override
    public String getDescription(int level) {
        int radius = 3 + level * 2;
        return "§7Sneak to grant §bResistance " + level + " §7to allies within §e" + radius + " blocks§7.";
    }
}
