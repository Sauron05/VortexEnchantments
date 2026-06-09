package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.List;

/**
 * Brainstorm: Sneak to charge. After 3s sneaking, release a shockwave
 * that launches nearby mobs into the air.
 */
public class BrainstormEnchant extends VortexEnchant {
    public BrainstormEnchant() { super("brainstorm", "Brainstorm", EnchantRarity.RARE, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.isSneaking()) return;
        if (isOnCooldown(player)) return;

        double radius = cfgd("radius", 4.0 + level);
        double force = cfgd("force", 0.6 + level * 0.3);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!player.isSneaking()) return;
            for (org.bukkit.entity.LivingEntity e :
                    com.vortexrpg.enchantments.util.MathUtil.getNearbyLiving(player.getLocation(), radius)) {
                if (e.equals(player)) continue;
                e.setVelocity(e.getVelocity().add(new org.bukkit.util.Vector(0, force, 0)));
            }
            ParticleUtil.burst(player.getLocation(), Particle.CLOUD, 20, 1.5);
            SoundUtil.play(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 0.5f, 1.5f);
            setCooldownFromConfig(player, "cooldown", 10.0);
        }, 60L);
    }

    @Override public String getDescription(int level) {
        return "§7Sneak 3s: §eshockwave §7launches mobs upward. 10s CD.";
    }
}
