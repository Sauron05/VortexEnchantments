package com.vortexrpg.enchantments.enchant.impl.axe;

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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Mjolnir: Right-click to throw a spectral axe projectile that deals AoE damage
 * on impact and returns to the player. Inspired by Thor's hammer.
 */
public class MjolnirEnchant extends VortexEnchant {

    public MjolnirEnchant() {
        super("mjolnir", "Mjolnir", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;

        double damage = cfgd("throw_damage", 4.0 + level * 3.0);
        double aoeRadius = cfgd("aoe_radius", 2.0 + level * 0.5);
        double range = cfgd("range", 15.0 + level * 5.0);

        Vector direction = player.getEyeLocation().getDirection().normalize();
        Location start = player.getEyeLocation();

        Location hit = null;
        for (double d = 1; d < range; d += 0.5) {
            Location point = start.clone().add(direction.clone().multiply(d));
            ParticleUtil.spawn(point, Particle.ENCHANTED_HIT, 2, 0.1);

            for (LivingEntity target : MathUtil.getNearbyLiving(point, 1.0)) {
                if (target.equals(player)) continue;
                hit = point;
                break;
            }
            if (hit != null) break;

            if (point.getBlock().getType().isSolid()) {
                hit = point;
                break;
            }
        }

        Location impactLoc = hit != null ? hit : start.clone().add(direction.clone().multiply(range));

        for (LivingEntity nearby : MathUtil.getNearbyLiving(impactLoc, aoeRadius)) {
            if (nearby.equals(player)) continue;
            nearby.damage(damage, player);
        }

        impactLoc.getWorld().strikeLightningEffect(impactLoc);
        ParticleUtil.spawn(impactLoc, Particle.EXPLOSION, 1, 0.0);
        SoundUtil.play(impactLoc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.6f, 1.2f);
        SoundUtil.play(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 0.5f, 1.0f);

        setCooldownFromConfig(player, "cooldown", 8);
    }

    @Override
    public String getDescription(int level) {
        double dmg = 4.0 + level * 3.0;
        return "§7Right-click to throw a §9lightning axe §7dealing §c" + dmg + " AoE damage§7.";
    }
}
