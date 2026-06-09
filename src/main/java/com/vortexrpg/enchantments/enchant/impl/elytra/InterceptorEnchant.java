package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Interceptor — Elytra (Epic, Max 3)
 * While gliding, pressing sneak triggers a dash straight toward your look target.
 */
public class InterceptorEnchant extends VortexEnchant {

    public InterceptorEnchant() {
        super("interceptor", "Interceptor", "elytra");
    }

    @Override
    public String getTier() { return "EPIC"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        return "While gliding, §esneak §7to dash forward at incredible speed (cooldown: §e" + (8 - level) + "s§7).";
    }

    @Override
    public void onToggleSneak(Player player, boolean isSneaking, int level) {
        if (!isSneaking || !player.isGliding()) return;
        if (isOnCooldown(player)) return;

        double[] speeds = {1.5, 2.0, 2.8};
        double speed = cfgd("dash_speed", speeds[level - 1]);
        Vector dir = player.getLocation().getDirection().normalize().multiply(speed);
        player.setVelocity(dir);
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation().add(0, 1, 0), 8, 0.3, 0.3, 0.3, 0);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 0.6f, 1.5f);

        int[] cooldowns = {7, 6, 5};
        setCooldownSeconds(player, cfgi("cooldown", cooldowns[level - 1]));
    }
}
