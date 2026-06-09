package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Sandstorm Shovel: Mining sand creates AoE blindness + damage to mobs. */
public class SandstormShovelEnchant extends VortexEnchant {

    public SandstormShovelEnchant() { super("sandstorm_shovel", "Sandstorm Shovel", EnchantRarity.EPIC, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (mat != Material.SAND && mat != Material.RED_SAND && mat != Material.SOUL_SAND) return;
        if (isOnCooldown(player)) return;
        double radius = cfg("radius", 3.0 + level);
        double damage = cfg("damage", 1.0 + level);
        int blindDuration = cfgi("blind-duration", 40 + level * 20);
        Location loc = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
        ParticleUtil.drawCircle(loc, radius, 30, Particle.CAMPFIRE_COSY_SMOKE);
        SoundUtil.play(loc, Sound.WEATHER_RAIN, 1.0f, 0.5f);
        for (LivingEntity e : MathUtil.getNearbyLiving(loc, radius)) {
            if (e.equals(player)) continue;
            e.damage(damage, player);
            e.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindDuration, 0));
        }
        setCooldownFromConfig(player, "cooldown", 8);
    }

    @Override public String getDescription() { return "Mining sand creates a damaging sandstorm."; }
    @Override public String getDescription(int level) {
        return "§7Mine sand: AoE §c" + (int)(1 + level) + "♥§7 + §5Blindness§7 in " + (int)(3 + level) + "b radius."; }
}
