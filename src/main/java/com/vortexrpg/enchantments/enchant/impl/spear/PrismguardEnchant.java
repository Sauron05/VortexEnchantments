package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;

/**
 * Prismguard: Right-click to deploy a prism shield at your location that
 * reflects the next 1/2/3 projectiles back at their source. Lasts 8s.
 */
public class PrismguardEnchant extends VortexEnchant {

    private static final Map<UUID, Integer> ACTIVE_SHIELDS = new HashMap<>();

    public PrismguardEnchant() {
        super("prismguard", "Prismguard", EnchantRarity.RARE, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;

        int charges = cfgi("charges", level);
        ACTIVE_SHIELDS.put(player.getUniqueId(), charges);

        Location loc = player.getLocation();
        ParticleUtil.drawCircle(loc, 2.0, 16, Particle.END_ROD);
        SoundUtil.play(loc, Sound.BLOCK_BEACON_ACTIVATE, 0.7f, 2.0f);

        // Auto-expire after 8 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                ACTIVE_SHIELDS.remove(player.getUniqueId());
            }
        }.runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), 160);

        setCooldownFromConfig(player, "cooldown", 20);
    }

    @Override
    public void onDamaged(org.bukkit.event.entity.EntityDamageByEntityEvent event, Player victim, org.bukkit.entity.Entity attacker, int level) {
        if (!isEnabled()) return;

        Integer charges = ACTIVE_SHIELDS.get(victim.getUniqueId());
        if (charges == null || charges <= 0) return;

        if (attacker instanceof org.bukkit.entity.Projectile proj) {
            event.setCancelled(true);
            proj.remove();

            if (proj.getShooter() instanceof org.bukkit.entity.LivingEntity source) {
                source.damage(4.0, victim);
            }

            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.END_ROD, 15, 0.5);
            SoundUtil.play(victim.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.5f);

            charges--;
            if (charges <= 0) {
                ACTIVE_SHIELDS.remove(victim.getUniqueId());
            } else {
                ACTIVE_SHIELDS.put(victim.getUniqueId(), charges);
            }
        }
    }

    @Override
    public String getDescription(int level) {
        return "§7Right-click: deploy shield reflecting §e" + level + " §7projectiles. §8(20s CD)";
    }
}
