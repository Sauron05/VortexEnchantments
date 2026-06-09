package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/** GravityWell: High-speed landing creates a gravity pull zone. */
public class GravityWellEnchant extends VortexEnchant {

    public GravityWellEnchant() { super("gravity_well", "Gravity Well", EnchantRarity.EPIC, 3, List.of(ItemTarget.ELYTRA)); }

    @SuppressWarnings("deprecation")
    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        boolean wasGliding = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "gw_gl", 0) == 1;
        boolean isGliding = player.isGliding();
        if (isGliding) {
            plugin.getPlayerDataManager().setDouble(player.getUniqueId(), "gw_speed", player.getVelocity().length());
        }
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "gw_gl", isGliding ? 1 : 0);
        if (wasGliding && !isGliding && player.isOnGround()) {
            double speed = plugin.getPlayerDataManager().getDouble(player.getUniqueId(), "gw_speed", 0);
            if (speed < cfgd("min_speed", 0.8)) return;
            double radius = cfgd("radius", 4.0 + level);
            double force = cfgd("pull_force", 0.5 + level * 0.2);
            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 80, radius, 1, radius, 0.1);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
            for (Entity e : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)) {
                if (e == player || !(e instanceof LivingEntity le)) continue;
                Vector pull = player.getLocation().toVector().subtract(le.getLocation().toVector()).normalize().multiply(force);
                le.setVelocity(le.getVelocity().add(pull));
            }
        }
    }

    @Override public String getDescription() { return "High-speed landing pulls enemies inward."; }
    @Override public String getDescription(int level) {
        return "§7Landing at speed creates a §5gravity pull§7 in §e" + (int)(4.0 + level) + "§7-block radius."; }
}
