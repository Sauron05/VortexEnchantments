package com.vortexrpg.enchantments.enchant.impl.sword;

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
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mirror Dimension: Right-click to create a 10-block radius mirror zone for 5/7/10s.
 * All damage dealt by enemies inside the zone is reflected back to them.
 */
public class MirrorDimensionEnchant extends VortexEnchant {

    private final ConcurrentHashMap<UUID, Location> mirrorZones = new ConcurrentHashMap<>();

    public MirrorDimensionEnchant() {
        super("mirror_dimension", "Mirror Dimension", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SWORD));
    }

    public Location getMirrorZone(UUID playerUuid) {
        return mirrorZones.get(playerUuid);
    }

    public double getMirrorRadius() {
        return cfgd("radius", 10.0);
    }

    public double getReflectRatio(int level) {
        return cfgd("reflect_ratio", 0.3 + level * 0.1);
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, ItemStack item, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;

        double cooldown = cfgd("cooldown_seconds", 30.0);
        double radius = getMirrorRadius();
        int durationTicks = cfgi("duration_ticks", 100) + (level - 1) * 40;

        setCooldownSeconds(player, cooldown);

        Location center = player.getLocation().clone();
        mirrorZones.put(player.getUniqueId(), center);

        SoundUtil.play(center, Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 0.5f);
        ParticleUtil.drawCircle(center, radius, 40, Particle.END_ROD);

        BukkitTask[] task = new BukkitTask[1];
        final int[] ticks = {0};

        task[0] = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (ticks[0]++ >= durationTicks / 4) {
                mirrorZones.remove(player.getUniqueId());
                SoundUtil.play(center, Sound.BLOCK_BEACON_DEACTIVATE, 0.8f, 0.5f);
                task[0].cancel();
                return;
            }
            ParticleUtil.drawCircle(center.clone().add(0, 0.2, 0), radius, 20, Particle.END_ROD);
        }, 0L, 4L);

        player.sendMessage("§5[Mirror Dimension] §7Mirror zone active! Damage is reflected!");
    }

    @Override
    public String getDescription(int level) {
        int secs = 5 + (level - 1) * 2 + (level == 3 ? 1 : 0);
        return "§7Right-click: create a §5mirror zone§7 for §e" + secs + "s§7. Enemy damage is reflected.";
    }
}
