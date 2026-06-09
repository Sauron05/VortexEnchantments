package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.List;

/** Zephyr: Temporary damage immunity while gliding on sneak activation. */
public class ZephyrEnchant extends VortexEnchant {

    public ZephyrEnchant() { super("zephyr", "Zephyr", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled() || !player.isGliding() || !event.isSneaking()) return;
        if (isOnCooldown(player)) return;
        setCooldownSeconds(player, cfgi("cooldown", Math.max(30, 70 - level * 10)));
        int duration = cfgi("duration", 3 + level) * 20;
        player.setInvulnerable(true);
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 30, 1, 1, 1, 0.05);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.8f, 1.5f);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.setInvulnerable(false), duration);
    }

    @Override public String getDescription() { return "Brief invulnerability while gliding."; }
    @Override public String getDescription(int level) {
        return "§7Sneak: §a" + (3 + level) + "s§7 invulnerability while gliding (§e" + Math.max(30, 70 - level * 10) + "s§7 cd)."; }
}
