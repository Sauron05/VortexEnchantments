package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.List;

/** AetherDash: Dash forward while gliding on sneak. */
public class AetherDashEnchant extends VortexEnchant {

    public AetherDashEnchant() { super("aether_dash", "Aether Dash", EnchantRarity.EPIC, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled() || !player.isGliding() || !event.isSneaking()) return;
        if (isOnCooldown(player)) return;
        setCooldownSeconds(player, cfgi("cooldown", Math.max(5, 12 - level * 2)));
        double distance = cfgd("distance", 6.0 + level * 2);
        Vector dir = player.getLocation().getDirection().multiply(distance * 0.15);
        player.setVelocity(dir);
        player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 20, 0.3, 0.3, 0.3, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.7f, 1.5f);
    }

    @Override public String getDescription() { return "Dash forward mid-glide on sneak."; }
    @Override public String getDescription(int level) {
        return "§7Sneak to §bdash§7 forward while gliding (§e" + Math.max(5, 12 - level * 2) + "s§7 cooldown)."; }
}
