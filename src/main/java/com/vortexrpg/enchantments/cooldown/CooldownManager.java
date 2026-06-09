package com.vortexrpg.enchantments.cooldown;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe cooldown manager. Tracks per-player, per-enchantment cooldowns.
 */
public class CooldownManager {

    // UUID -> (enchantId -> expireTimeMillis)
    private final Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

    public CooldownManager(com.vortexrpg.enchantments.VortexEnchantments plugin) {
        // Start cleanup task every 5 minutes
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::cleanup, 6000L, 6000L);
    }

    public boolean isOnCooldown(Player player, String enchantId) {
        Map<String, Long> map = cooldowns.get(player.getUniqueId());
        if (map == null) return false;
        Long expire = map.get(enchantId);
        return expire != null && System.currentTimeMillis() < expire;
    }

    public long getRemainingMillis(Player player, String enchantId) {
        Map<String, Long> map = cooldowns.get(player.getUniqueId());
        if (map == null) return 0;
        Long expire = map.get(enchantId);
        if (expire == null) return 0;
        return Math.max(0, expire - System.currentTimeMillis());
    }

    public double getRemainingSeconds(Player player, String enchantId) {
        return getRemainingMillis(player, enchantId) / 1000.0;
    }

    public void setCooldown(Player player, String enchantId, long durationMillis) {
        if (durationMillis <= 0) return;
        cooldowns
            .computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>())
            .put(enchantId, System.currentTimeMillis() + durationMillis);
    }

    public void clearCooldown(Player player, String enchantId) {
        Map<String, Long> map = cooldowns.get(player.getUniqueId());
        if (map != null) map.remove(enchantId);
    }

    public void clearAll(Player player) {
        cooldowns.remove(player.getUniqueId());
    }

    private void cleanup() {
        long now = System.currentTimeMillis();
        cooldowns.values().forEach(map -> map.entrySet().removeIf(e -> e.getValue() < now));
        cooldowns.entrySet().removeIf(e -> e.getValue().isEmpty());
    }
}
