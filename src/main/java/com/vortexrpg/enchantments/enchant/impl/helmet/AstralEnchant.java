package com.vortexrpg.enchantments.enchant.impl.helmet;

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
 * Astral: Sneak to project an astral form 10-30 blocks forward (scouting).
 * Returns after 3s. 20s CD.
 */
public class AstralEnchant extends VortexEnchant {
    public AstralEnchant() { super("astral", "Astral", EnchantRarity.EPIC, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.isSneaking()) return;
        if (isOnCooldown(player)) return;

        double distance = cfgd("distance", 10.0 + level * 10.0);
        org.bukkit.Location origin = player.getLocation().clone();
        org.bukkit.Location dest = origin.clone().add(origin.getDirection().normalize().multiply(distance));
        dest.setY(player.getWorld().getHighestBlockYAt(dest) + 1);

        if (!dest.getBlock().isPassable()) { dest.add(0, 1, 0); }

        ParticleUtil.spawn(origin, Particle.PORTAL, 30, 0.5);
        player.teleport(dest);
        SoundUtil.play(dest, Sound.ENTITY_ENDERMAN_TELEPORT, 0.6f, 1.8f);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            ParticleUtil.spawn(player.getLocation(), Particle.PORTAL, 30, 0.5);
            player.teleport(origin);
            SoundUtil.play(origin, Sound.ENTITY_ENDERMAN_TELEPORT, 0.6f, 1.8f);
        }, 60L);

        setCooldownFromConfig(player, "cooldown", 20.0);
    }

    @Override public String getDescription(int level) {
        return "§7Sneak: project forward §a" + (int)(10 + level * 10) + " §7blocks for 3s. §820s CD.";
    }
}
