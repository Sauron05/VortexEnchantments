package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Colossus: On near-death, become invulnerable for a short time and gain massive buffs.
 */
public class ColossusEnchant extends VortexEnchant {
    public ColossusEnchant() { super("colossus", "Colossus", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double hpAfter = player.getHealth() - event.getFinalDamage();
        if (hpAfter > 2.0) return;
        if (hpAfter <= 0) return;
        if (isOnCooldown(player)) return;

        int dur = cfgi("buff_duration", 40 + level * 20);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, dur, Math.min(level, 3), true, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, dur, level - 1, true, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, dur, level - 1, true, false, true));
        player.setNoDamageTicks(cfgi("invuln_ticks", 20));

        ParticleUtil.burst(player.getLocation().add(0, 1, 0), Particle.TOTEM_OF_UNDYING, 30, 2.0);
        SoundUtil.play(player.getLocation(), Sound.ITEM_TOTEM_USE, 1.0f, 0.8f);
        setCooldownFromConfig(player, "cooldown", 180.0);
    }

    @Override public String getDescription(int level) {
        return "§7Near-death: §6invulnerability §7+ massive buffs for " + (2 + level) + "s. §8180s CD.";
    }
}
