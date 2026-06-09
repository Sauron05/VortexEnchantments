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
 * LeapBoot: Sneak to launch yourself upward.
 */
public class LeapBootEnchant extends VortexEnchant {
    public LeapBootEnchant() { super("leap_boot", "Leap Boot", EnchantRarity.RARE, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.isSneaking()) return;
        if (isOnCooldown(player)) return;
        if (player.getFallDistance() > 0) return;

        double power = cfgd("leap_power", 0.6 + level * 0.3);
        player.setVelocity(player.getVelocity().setY(power));
        ParticleUtil.spawn(player.getLocation(), Particle.CLOUD, 10, 0.4);
        SoundUtil.play(player.getLocation(), Sound.ENTITY_BREEZE_JUMP, 0.7f, 1.2f);
        setCooldownFromConfig(player, "cooldown", 6.0);
    }

    @Override public String getDescription(int level) {
        return "§7Sneak to §bleap §7upward. §86s CD.";
    }
}
