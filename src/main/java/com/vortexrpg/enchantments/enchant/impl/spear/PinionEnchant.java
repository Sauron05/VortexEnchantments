package com.vortexrpg.enchantments.enchant.impl.spear;

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
 * Pinion: Roots the target in place (extreme Slowness) for 1/1.5/2 seconds,
 * preventing all movement like a spear pinning them to the ground.
 */
public class PinionEnchant extends VortexEnchant {

    public PinionEnchant() {
        super("pinion", "Pinion", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        int durationTicks = (int) (cfgd("duration", 0.5 + level * 0.5) * 20);

        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, durationTicks, 9, false, false));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, durationTicks, 128, false, false));

        ParticleUtil.drawCircle(victim.getLocation(), 0.8, 12, Particle.ENCHANTED_HIT);
        SoundUtil.play(victim.getLocation(), Sound.BLOCK_CHAIN_PLACE, 1.0f, 0.5f);

        setCooldownFromConfig(attacker, "cooldown", 8);
    }

    @Override
    public String getDescription(int level) {
        String dur = String.format("%.1f", 0.5 + level * 0.5);
        return "§7Pins target in place for §e" + dur + "s§7.";
    }
}
