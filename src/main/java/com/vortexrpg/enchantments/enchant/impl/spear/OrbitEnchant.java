package com.vortexrpg.enchantments.enchant.impl.spear;

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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Orbit: Right-click to make the spear's aura orbit around you for 4/6/8
 * seconds, striking all nearby entities every 0.5s for 3 damage.
 */
public class OrbitEnchant extends VortexEnchant {

    public OrbitEnchant() {
        super("orbit", "Orbit", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;

        int durationTicks = cfgi("duration_ticks", (2 + level * 2) * 20);
        double damage = cfgd("tick_damage", 3.0);
        double radius = cfgd("radius", 3.0);

        SoundUtil.play(player.getLocation(), Sound.ITEM_TRIDENT_RETURN, 1.0f, 1.5f);

        new BukkitRunnable() {
            int ticks = 0;
            double angle = 0;

            @Override
            public void run() {
                ticks += 5;
                if (ticks > durationTicks || player.isDead() || !player.isValid()) {
                    cancel();
                    return;
                }

                angle += Math.PI / 5;
                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;
                var orbitLoc = player.getLocation().add(x, 1, z);

                ParticleUtil.spawn(orbitLoc, Particle.ENCHANTED_HIT, 5, 0.2);
                ParticleUtil.spawn(orbitLoc, Particle.END_ROD, 2, 0.1);

                if (ticks % 10 == 0) {
                    for (LivingEntity le : MathUtil.getNearbyLiving(player.getLocation(), radius + 1)) {
                        if (le.equals(player)) continue;
                        le.damage(damage, player);
                    }
                    SoundUtil.play(player.getLocation(), Sound.ITEM_TRIDENT_HIT_GROUND, 0.5f, 1.5f);
                }
            }
        }.runTaskTimer(JavaPlugin.getProvidingPlugin(getClass()), 0, 5);

        setCooldownFromConfig(player, "cooldown", 20);
    }

    @Override
    public String getDescription(int level) {
        int secs = 2 + level * 2;
        return "§7Right-click: orbiting spear aura for §e" + secs + "s §7hitting nearby foes.";
    }
}
