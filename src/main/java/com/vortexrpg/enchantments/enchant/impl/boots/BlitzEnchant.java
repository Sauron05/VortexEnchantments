package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Blitz: Sneak to gain massive short Speed burst.
 */
public class BlitzEnchant extends VortexEnchant {
    public BlitzEnchant() { super("blitz", "Blitz", EnchantRarity.EPIC, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.isSneaking()) return;
        if (isOnCooldown(player)) return;

        int dur = cfgi("boost_duration", 20 + level * 10);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, dur, level, true, false, true));
        ParticleUtil.spawn(player.getLocation(), Particle.CLOUD, 10, 0.3);
        SoundUtil.play(player.getLocation(), Sound.ENTITY_BREEZE_WIND_BURST, 0.7f, 1.5f);
        setCooldownFromConfig(player, "cooldown", 10.0);
    }

    @Override public String getDescription(int level) {
        return "§7Sneak: §bSpeed " + (level + 1) + " §7for " + (1 + level * 0.5) + "s. §810s CD.";
    }
}
