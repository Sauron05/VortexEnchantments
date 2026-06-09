package com.vortexrpg.enchantments.enchant.impl.chestplate;

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
 * Guardian Plate: Sneak to grant nearby allies Resistance for X seconds.
 */
public class GuardianPlateEnchant extends VortexEnchant {
    public GuardianPlateEnchant() { super("guardian_plate", "Guardian Plate", EnchantRarity.RARE, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.isSneaking()) return;
        if (isOnCooldown(player)) return;

        double radius = cfgd("radius", 6.0);
        int dur = cfgi("duration", 40 + level * 20);

        for (Player p : player.getWorld().getPlayers()) {
            if (p.equals(player)) continue;
            if (p.getLocation().distanceSquared(player.getLocation()) <= radius * radius) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, dur, 0, true, false, true));
            }
        }
        ParticleUtil.burst(player.getLocation(), Particle.ENCHANT, 20, 2.0);
        SoundUtil.play(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.6f, 1.5f);
        setCooldownFromConfig(player, "cooldown", 15.0);
    }

    @Override public String getDescription(int level) {
        return "§7Sneak: give nearby allies §bResistance I §7for " + (2 + level) + "s. §815s CD.";
    }
}
