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

import java.util.List;

/**
 * QuickDash: Sneak to dash forward quickly.
 */
public class QuickDashEnchant extends VortexEnchant {
    public QuickDashEnchant() { super("quick_dash", "Quick Dash", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.isSneaking()) return;
        if (isOnCooldown(player)) return;

        double power = cfgd("dash_power", 0.8 + level * 0.3);
        player.setVelocity(player.getLocation().getDirection().multiply(power).setY(0.15));
        ParticleUtil.spawn(player.getLocation(), Particle.CLOUD, 8, 0.3);
        SoundUtil.play(player.getLocation(), Sound.ENTITY_BREEZE_WIND_BURST, 0.6f, 1.5f);
        setCooldownFromConfig(player, "cooldown", 5.0);
    }

    @Override public String getDescription(int level) {
        return "§7Sneak to §bdash §7forward. §85s CD.";
    }
}
