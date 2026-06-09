package com.vortexrpg.enchantments.enchant.impl.boots;

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
 * VoidStep: Sneak to create a void field, damaging and pulling enemies.
 */
public class VoidStepEnchant extends VortexEnchant {
    public VoidStepEnchant() { super("void_step", "Void Step", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.isSneaking()) return;
        if (isOnCooldown(player)) return;

        double radius = cfgd("radius", 6.0 + level);
        double dmg = cfgd("damage", 3.0 * level);
        Location center = player.getLocation().clone();

        for (LivingEntity e : MathUtil.getNearbyLiving(center, radius)) {
            if (e.equals(player)) continue;
            e.damage(dmg, player);
            org.bukkit.util.Vector pull = center.toVector().subtract(e.getLocation().toVector()).normalize().multiply(0.8);
            e.setVelocity(pull);
            e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40 + level * 10, level - 1, true, false, true));
        }

        ParticleUtil.drawCircle(center.add(0, 0.5, 0), radius, 30, Particle.DRAGON_BREATH);
        ParticleUtil.burst(center, Particle.PORTAL, 25, radius);
        SoundUtil.play(center, Sound.ENTITY_WITHER_SPAWN, 0.3f, 1.8f);
        setCooldownFromConfig(player, "cooldown", 35.0);
    }

    @Override public String getDescription(int level) {
        return "§7Sneak: §5void field §7pulling + damaging enemies in " + (6 + level) + " blocks. §835s CD.";
    }
}
