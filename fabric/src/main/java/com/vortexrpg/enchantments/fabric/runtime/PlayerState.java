package com.vortexrpg.enchantments.fabric.runtime;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Transient per-player runtime state used by enchants (kill streaks, idle timers, flags).
 * Mirrors the Paper edition's PlayerDataManager. Cleared on disconnect.
 */
public final class PlayerState {

    private static final Map<UUID, PlayerState> STATES = new ConcurrentHashMap<>();

    private long lastSwingMillis = 0L;
    private final Deque<Long> killTimestamps = new ArrayDeque<>();
    private final Map<String, Integer> ints = new HashMap<>();

    public static PlayerState of(UUID uuid) {
        return STATES.computeIfAbsent(uuid, u -> new PlayerState());
    }

    public static void clear(UUID uuid) {
        STATES.remove(uuid);
    }

    // ── Swing / idle ──────────────────────────────────────────────────────
    public void recordSwing() { lastSwingMillis = System.currentTimeMillis(); }

    public boolean isIdle(long thresholdMillis) {
        if (lastSwingMillis == 0L) return true;
        return System.currentTimeMillis() - lastSwingMillis >= thresholdMillis;
    }

    // ── Kill streaks ──────────────────────────────────────────────────────
    public void recordKill() { killTimestamps.addLast(System.currentTimeMillis()); }

    public int recentKillCount(long windowMillis) {
        long cutoff = System.currentTimeMillis() - windowMillis;
        while (!killTimestamps.isEmpty() && killTimestamps.peekFirst() < cutoff) {
            killTimestamps.pollFirst();
        }
        return killTimestamps.size();
    }

    // ── Generic int flags ─────────────────────────────────────────────────
    public void setInt(String key, int value) { ints.put(key, value); }
    public int getInt(String key) { return ints.getOrDefault(key, 0); }
}
