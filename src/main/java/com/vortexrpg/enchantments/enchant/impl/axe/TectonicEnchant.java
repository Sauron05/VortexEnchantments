package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/**
 * Tectonic: Right-click to teleport-swap positions with the entity you're
 * looking at. Both parties are displaced. Range: 8/12/16 blocks.
 */
public class TectonicEnchant extends VortexEnchant {

    public TectonicEnchant() {
        super("tectonic", "Tectonic", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;

        double range = cfgd("range", 6.0 + level * 2.0);

        LivingEntity target = null;
        for (var entity : player.getNearbyEntities(range, range, range)) {
            if (!(entity instanceof LivingEntity le)) continue;
            if (le.isDead()) continue;

            // Ray-cast check: is the player looking at this entity?
            var toEntity = le.getEyeLocation().toVector().subtract(player.getEyeLocation().toVector());
            double distance = toEntity.length();
            if (distance > range) continue;

            double dot = player.getEyeLocation().getDirection().normalize().dot(toEntity.normalize());
            if (dot > 0.96) { // tight cone
                if (target == null || distance < player.getLocation().distance(target.getLocation())) {
                    target = le;
                }
            }
        }

        if (target == null) return;

        Location playerLoc = player.getLocation().clone();
        Location targetLoc = target.getLocation().clone();

        // Preserve yaw/pitch for both
        float playerYaw = playerLoc.getYaw();
        float playerPitch = playerLoc.getPitch();
        float targetYaw = targetLoc.getYaw();
        float targetPitch = targetLoc.getPitch();

        targetLoc.setYaw(playerYaw);
        targetLoc.setPitch(playerPitch);
        playerLoc.setYaw(targetYaw);
        playerLoc.setPitch(targetPitch);

        player.teleport(targetLoc);
        target.teleport(playerLoc);

        // Particles at both locations
        ParticleUtil.spawn(playerLoc, Particle.PORTAL, 30, 1.0);
        ParticleUtil.spawn(targetLoc, Particle.PORTAL, 30, 1.0);
        ParticleUtil.spawn(playerLoc, Particle.REVERSE_PORTAL, 15, 0.5);
        ParticleUtil.spawn(targetLoc, Particle.REVERSE_PORTAL, 15, 0.5);

        SoundUtil.play(playerLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 1.2f);
        SoundUtil.play(targetLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 1.2f);

        if (target instanceof Player p) {
            p.sendMessage("§5[Tectonic] §7You were displaced!");
        }

        setCooldownFromConfig(player, "cooldown", 8);
    }

    @Override
    public String getDescription(int level) {
        int r = (int) (6 + level * 2);
        return "§7Right-click to §5swap positions §7with the target within §e" + r + " blocks§7.";
    }
}
