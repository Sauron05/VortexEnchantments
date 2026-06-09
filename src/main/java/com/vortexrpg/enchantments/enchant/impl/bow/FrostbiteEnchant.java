package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Frostbite: Arrow triggers progressive freeze — Slowness II, freeze ticks,
 * and ice blocks form at the target's feet for 2/3/4 seconds.
 */
public class FrostbiteEnchant extends VortexEnchant {

    public FrostbiteEnchant() {
        super("frostbite", "Frostbite", EnchantRarity.EPIC, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(shooter)) return;

        int duration = cfgi("duration", (1 + level)) * 20;
        int freezeTicks = cfgi("freeze_ticks", 60 + level * 20);

        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 1, false, false));
        victim.setFreezeTicks(freezeTicks);

        Location feet = victim.getLocation();
        Location ice1 = feet.clone();
        Location ice2 = feet.clone().add(1, 0, 0);
        Location ice3 = feet.clone().add(0, 0, 1);

        if (ice1.getBlock().getType() == Material.AIR) ice1.getBlock().setType(Material.ICE);
        if (ice2.getBlock().getType() == Material.AIR) ice2.getBlock().setType(Material.ICE);
        if (ice3.getBlock().getType() == Material.AIR) ice3.getBlock().setType(Material.ICE);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (ice1.getBlock().getType() == Material.ICE) ice1.getBlock().setType(Material.AIR);
                if (ice2.getBlock().getType() == Material.ICE) ice2.getBlock().setType(Material.AIR);
                if (ice3.getBlock().getType() == Material.ICE) ice3.getBlock().setType(Material.AIR);
            }
        }.runTaskLater(plugin, duration);

        ParticleUtil.spawn(feet.add(0, 1, 0), Particle.SNOWFLAKE, 15, 0.6);
        SoundUtil.play(feet, Sound.BLOCK_GLASS_BREAK, 0.8f, 1.5f);

        setCooldownFromConfig(shooter, "cooldown", 6.0);
    }

    @Override
    public String getDescription(int level) {
        int dur = 1 + level;
        return "§7Arrow §b§lFREEZES §7target + ice at feet for §e" + dur + "s§7. 6s CD.";
    }
}
