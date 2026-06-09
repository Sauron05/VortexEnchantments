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

/** VoidWalker: Phase through blocks while gliding on sneak. */
public class VoidWalkerEnchant extends VortexEnchant {

    public VoidWalkerEnchant() { super("void_walker", "Void Walker", EnchantRarity.MYTHIC, 1, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled() || !player.isGliding() || !event.isSneaking()) return;
        if (isOnCooldown(player)) return;
        setCooldownSeconds(player, cfgi("cooldown", 15));
        Vector dir = player.getLocation().getDirection().normalize();
        Location target = player.getLocation().clone();
        // Advance through up to 3 solid blocks
        int maxPhase = cfgi("max_phase_blocks", 3);
        for (int i = 0; i < maxPhase + 5; i++) {
            target.add(dir);
            if (!target.getBlock().getType().isSolid() && !target.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
                break;
            }
        }
        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 40, 0.5, 0.5, 0.5, 0.5);
        player.teleport(target);
        player.getWorld().spawnParticle(Particle.PORTAL, target, 40, 0.5, 0.5, 0.5, 0.5);
        player.getWorld().playSound(target, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.7f);
    }

    @Override public String getDescription() { return "Phase through blocks while gliding."; }
    @Override public String getDescription(int level) {
        return "§7Sneak to §5phase§7 through up to §e3§7 blocks while gliding (§e15s§7 cooldown)."; }
}
