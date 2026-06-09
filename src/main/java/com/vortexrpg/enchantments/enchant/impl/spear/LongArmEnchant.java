package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Long Arm: Right-click to thrust forward, damaging the first entity
 * in an extended reach of 5/6/7 blocks (beyond normal melee).
 */
public class LongArmEnchant extends VortexEnchant {

    public LongArmEnchant() {
        super("longarm", "Long Arm", EnchantRarity.COMMON, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;

        double range = cfgd("range", 4.0 + level);
        double damage = cfgd("damage", 2.0 + level);

        Location eye = player.getEyeLocation();
        Vector dir = eye.getDirection().normalize();

        LivingEntity target = null;
        double closest = range;

        for (var entity : player.getNearbyEntities(range, range, range)) {
            if (!(entity instanceof LivingEntity le)) continue;
            if (le.equals(player)) continue;

            Vector toEntity = le.getLocation().add(0, 1, 0).toVector().subtract(eye.toVector());
            double dist = toEntity.length();
            if (dist > range) continue;

            double dot = toEntity.normalize().dot(dir);
            if (dot > 0.95 && dist < closest) {
                closest = dist;
                target = le;
            }
        }

        if (target != null) {
            target.damage(damage, player);
            ParticleUtil.drawLine(eye.add(dir.multiply(0.5)), target.getLocation().add(0, 1, 0),
                    Particle.ENCHANTED_HIT, 0.3);
            setCooldownFromConfig(player, "cooldown", 2);
        }
    }

    @Override
    public String getDescription(int level) {
        return "§7Right-click to thrust at targets up to §e" + (4 + level) + " blocks §7away.";
    }
}
