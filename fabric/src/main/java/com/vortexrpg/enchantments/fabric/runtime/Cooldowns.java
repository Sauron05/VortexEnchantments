package com.vortexrpg.enchantments.fabric.runtime;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Per-player, per-enchant cooldown tracking. */
public final class Cooldowns {

    private static final Map<UUID, Map<String, Long>> EXPIRY = new ConcurrentHashMap<>();

    private Cooldowns() {}

    public static boolean isOnCooldown(UUID uuid, String id) {
        Map<String, Long> map = EXPIRY.get(uuid);
        if (map == null) return false;
        Long expiry = map.get(id);
        return expiry != null && System.currentTimeMillis() < expiry;
    }

    public static long remainingMillis(UUID uuid, String id) {
        Map<String, Long> map = EXPIRY.get(uuid);
        if (map == null) return 0;
        Long expiry = map.get(id);
        if (expiry == null) return 0;
        return Math.max(0, expiry - System.currentTimeMillis());
    }

    public static void set(UUID uuid, String id, long millis) {
        EXPIRY.computeIfAbsent(uuid, u -> new ConcurrentHashMap<>())
                .put(id, System.currentTimeMillis() + millis);
    }

    public static void clear(UUID uuid) {
        EXPIRY.remove(uuid);
    }
}
