package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Timestop: Arrow suspends target in place — applies extreme Slowness + Mining Fatigue
 * for 0.5/0.75/1.0 seconds. A brief total freeze.
 */
public class TimestopEnchant extends VortexEnchant {

    public TimestopEnchant() {
        super("timestop", "Timestop", EnchantRarity.RARE, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(shooter)) return;

        int freezeTicks = cfgi("freeze_ticks", 8 + level * 5);

        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, freezeTicks, 99, false, false));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, freezeTicks, 99, false, false));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, freezeTicks, 250, false, false));

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.END_ROD, 15, 0.5);
        SoundUtil.play(victim.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 0.7f, 2.0f);

        setCooldownFromConfig(shooter, "cooldown", 8.0);
    }

    @Override
    public String getDescription(int level) {
        double sec = (8 + level * 5) / 20.0;
        return "§7Arrow §dstops time §7— target frozen §e" + sec + "s§7. 8s CD.";
    }
}
