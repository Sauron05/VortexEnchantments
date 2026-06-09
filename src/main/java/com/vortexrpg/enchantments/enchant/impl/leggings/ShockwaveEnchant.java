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
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.List;

/**
 * Shockwave: Sneak to stomp the ground, launching nearby enemies upward.
 */
public class ShockwaveEnchant extends VortexEnchant {
    public ShockwaveEnchant() { super("shockwave", "Shockwave", EnchantRarity.RARE, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.isSneaking()) return;
        if (isOnCooldown(player)) return;

        double radius = cfgd("radius", 4.0 + level);
        double launch = cfgd("launch_power", 0.5 + level * 0.3);

        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (e.equals(player)) continue;
            e.setVelocity(e.getVelocity().setY(launch));
        }
        ParticleUtil.drawCircle(player.getLocation().add(0, 0.1, 0), radius, 20, Particle.CAMPFIRE_COSY_SMOKE);
        SoundUtil.play(player.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 0.8f, 0.6f);
        setCooldownFromConfig(player, "cooldown", 12.0);
    }

    @Override public String getDescription(int level) {
        return "§7Sneak: §6stomp §7launching enemies in " + (int)(4 + level) + " blocks. §812s CD.";
    }
}
