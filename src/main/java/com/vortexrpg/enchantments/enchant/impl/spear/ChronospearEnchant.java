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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Chronospear: Hit creates a time-slow bubble (5 block radius) where all
 * entities get Slowness IV for 2/3/4 seconds. Player is immune to their own effect.
 */
public class ChronospearEnchant extends VortexEnchant {

    public ChronospearEnchant() {
        super("chronospear", "Chronospear", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        double radius = cfgd("radius", 5.0);
        int durationTicks = cfgi("duration_ticks", (level + 1) * 20);

        var center = victim.getLocation();

        for (LivingEntity le : MathUtil.getNearbyLiving(center, radius)) {
            if (le.equals(attacker)) continue;
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, durationTicks, 3, false, true));
        }

        ParticleUtil.drawCircle(center, radius, 24, Particle.END_ROD);
        ParticleUtil.spawn(center.add(0, 1, 0), Particle.REVERSE_PORTAL, 30, radius * 0.5);
        SoundUtil.play(center, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 0.7f, 2.0f);

        attacker.sendMessage("§d[Chronospear] §7Time dilated!");

        setCooldownFromConfig(attacker, "cooldown", 25);
    }

    @Override
    public String getDescription(int level) {
        int secs = level + 1;
        return "§7Hit: §5time-slow bubble §7(5 blocks) — §eSlowness IV §7for §e" + secs + "s§7. §8(25s CD)";
    }
}
