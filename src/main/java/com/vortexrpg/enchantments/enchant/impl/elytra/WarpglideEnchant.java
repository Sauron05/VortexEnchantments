package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.List;

/** Warpglide: Teleport forward while gliding on sneak. */
public class WarpglideEnchant extends VortexEnchant {

    public WarpglideEnchant() { super("warpglide", "Warpglide", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled() || !player.isGliding() || !event.isSneaking()) return;
        if (isOnCooldown(player)) return;
        setCooldownSeconds(player, cfgi("cooldown", Math.max(10, 25 - level * 5)));
        double dist = cfgd("distance", 10.0 + level * 5);
        Vector dir = player.getLocation().getDirection().normalize().multiply(dist);
        Location dest = player.getLocation().add(dir);
        dest.setY(Math.max(dest.getY(), player.getWorld().getMinHeight() + 1));
        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 40, 0.5, 0.5, 0.5, 0.5);
        player.teleport(dest);
        player.getWorld().spawnParticle(Particle.PORTAL, dest, 40, 0.5, 0.5, 0.5, 0.5);
        player.getWorld().playSound(dest, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
    }

    @Override public String getDescription() { return "Teleport forward while gliding."; }
    @Override public String getDescription(int level) {
        return "§7Sneak to §dteleport§7 §e" + (int)(10.0 + level * 5) + "§7 blocks forward (§e" + Math.max(10, 25 - level * 5) + "s§7 cd)."; }
}
