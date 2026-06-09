package com.vortexrpg.enchantments.data;

import com.vortexrpg.enchantments.VortexEnchantments;
import org.bukkit.Location;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks per-player runtime state for enchantments that need counters, streaks, timestamps, etc.
 * All data is in-memory only (not persisted across server restarts unless you add file I/O).
 */
public class PlayerDataManager {

    @SuppressWarnings("unused") // Available for data persistence in future
    private final VortexEnchantments plugin;

    // Kill streak tracking (Thirst)
    private final Map<UUID, List<Long>> killTimestamps = new ConcurrentHashMap<>();

    // Swing timestamps (Dormant)
    private final Map<UUID, Long> lastSwingTime = new ConcurrentHashMap<>();

    // Last melee damage received timestamp (Riposte Shot)
    private final Map<UUID, Long> lastMeleeDamageTime = new ConcurrentHashMap<>();

    // Damage received from specific attackers (Grudge) UUID attacker -> damage
    private final Map<UUID, Map<UUID, Double>> grudgeDamage = new ConcurrentHashMap<>();

    // Hit counters per attacker -> target (Phase)
    private final Map<UUID, Map<UUID, Integer>> hitCounters = new ConcurrentHashMap<>();

    // Tether targets (attacker -> tethered entity UUID)
    private final Map<UUID, UUID> tetherTargets = new ConcurrentHashMap<>();

    // Bleed stacks per entity (Rend)
    private final Map<UUID, Integer> bleedStacks = new ConcurrentHashMap<>();

    // Weight stacks per player (Weight axe)
    private final Map<UUID, Integer> weightStacks = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastKillTime = new ConcurrentHashMap<>();

    // Entropy stacks per player
    private final Map<UUID, Integer> entropyStacks = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastSwingEntropyTime = new ConcurrentHashMap<>();

    // Epitaph kill log per item (stored in PDC, tracked here for runtime)
    // Backswing miss flag
    private final Map<UUID, Boolean> backswingReady = new ConcurrentHashMap<>();
    private final Map<UUID, Long> backswingExpiry = new ConcurrentHashMap<>();

    // Crossbow shot counters (Binary)
    private final Map<UUID, Integer> crossbowShotCounter = new ConcurrentHashMap<>();

    // Discharge: first-shot-after-reload flag
    private final Map<UUID, Boolean> firstShotReady = new ConcurrentHashMap<>();

    // Deadeye: crouch start time
    private final Map<UUID, Long> crouchStartTime = new ConcurrentHashMap<>();

    // Veneer marked entities: entity UUID -> expire time
    private final Map<UUID, Long> veneerMarked = new ConcurrentHashMap<>();

    // Circuit: last hit time/location per target per attacker
    private final Map<UUID, Map<UUID, Long>> circuitLastHitTime = new ConcurrentHashMap<>();
    private final Map<UUID, Map<UUID, Location>> circuitLastHitLoc = new ConcurrentHashMap<>();

    // Stride stacks + time
    private final Map<UUID, Integer> strideStacks = new ConcurrentHashMap<>();
    private final Map<UUID, Long> strideLastUpdate = new ConcurrentHashMap<>();

    // Obelisk standing time
    private final Map<UUID, Long> standingStartTime = new ConcurrentHashMap<>();
    private final Map<UUID, Location> lastKnownLocation = new ConcurrentHashMap<>();

    // Nomad distance tracking
    private final Map<UUID, Double> totalDistanceTraveled = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastMovementTime = new ConcurrentHashMap<>();

    // Pathfinder: stepped blocks
    private final Map<UUID, Set<Long>> steppedBlocks = new ConcurrentHashMap<>();

    // Static boots charge distance
    private final Map<UUID, Double> staticChargeDistance = new ConcurrentHashMap<>();

    // Fortify stoughness stacks
    private final Map<UUID, Integer> fortifyStacks = new ConcurrentHashMap<>();
    private final Map<UUID, Long> fortifyLastMoveTime = new ConcurrentHashMap<>();

    // Scar tissue: resist per damage type
    private final Map<UUID, Map<String, Integer>> scarResist = new ConcurrentHashMap<>();

    // Reservoir: stored overheal
    private final Map<UUID, Double> reservoirStored = new ConcurrentHashMap<>();

    // Ironheart absorption time tracking
    private final Map<UUID, Long> ironheartWearStart = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> ironheartAbsorption = new ConcurrentHashMap<>();

    // Elytra: flight distance tracking for Sonic
    private final Map<UUID, Double> flightDistance = new ConcurrentHashMap<>();
    private final Map<UUID, Location> lastFlightLocation = new ConcurrentHashMap<>();

    // Capacitor charge
    private final Map<UUID, Integer> capacitorCharge = new ConcurrentHashMap<>();
    private final Map<UUID, Double> walkDistanceForCapacitor = new ConcurrentHashMap<>();

    // Thirst damage multiplier stacks
    private final Map<UUID, Integer> thirstStacks = new ConcurrentHashMap<>();

    // Recent attackers on entity (Verdict axe)
    private final Map<UUID, Map<UUID, Long>> entityRecentAttackers = new ConcurrentHashMap<>();

    // Harvest: saturation bonuses stored on food items tracked by PDC
    // General generic integer counters
    private final Map<UUID, Map<String, Integer>> genericIntCounters = new ConcurrentHashMap<>();
    private final Map<UUID, Map<String, Long>> genericLongCounters = new ConcurrentHashMap<>();
    private final Map<UUID, Map<String, Double>> genericDoubleCounters = new ConcurrentHashMap<>();

    // Fishing: biomes fished from this session
    private final Map<UUID, Set<String>> fishedBiomes = new ConcurrentHashMap<>();

    // Pulse: hit counter for leggings
    private final Map<UUID, Integer> pulseHitCounter = new ConcurrentHashMap<>();

    public PlayerDataManager(VortexEnchantments plugin) {
        this.plugin = plugin;
    }

    public void saveAll() {
        // Future: persist data to disk if needed
    }

    // ─── Generic typed counter helpers ───────────────────────────────────────

    public int getInt(UUID uuid, String key) {
        return genericIntCounters.getOrDefault(uuid, Collections.emptyMap()).getOrDefault(key, 0);
    }

    public int getInt(UUID uuid, String key, int defaultValue) {
        return genericIntCounters.getOrDefault(uuid, Collections.emptyMap()).getOrDefault(key, defaultValue);
    }

    public void setInt(UUID uuid, String key, int value) {
        genericIntCounters.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).put(key, value);
    }

    public int incrementInt(UUID uuid, String key) {
        int val = getInt(uuid, key) + 1;
        setInt(uuid, key, val);
        return val;
    }

    public long getLong(UUID uuid, String key) {
        return genericLongCounters.getOrDefault(uuid, Collections.emptyMap()).getOrDefault(key, 0L);
    }

    public long getLong(UUID uuid, String key, long defaultValue) {
        return genericLongCounters.getOrDefault(uuid, Collections.emptyMap()).getOrDefault(key, defaultValue);
    }

    public void setLong(UUID uuid, String key, long value) {
        genericLongCounters.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).put(key, value);
    }

    public double getDouble(UUID uuid, String key) {
        return genericDoubleCounters.getOrDefault(uuid, Collections.emptyMap()).getOrDefault(key, 0.0);
    }

    public double getDouble(UUID uuid, String key, double defaultValue) {
        return genericDoubleCounters.getOrDefault(uuid, Collections.emptyMap()).getOrDefault(key, defaultValue);
    }

    public void setDouble(UUID uuid, String key, double value) {
        genericDoubleCounters.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).put(key, value);
    }

    public void addDouble(UUID uuid, String key, double amount) {
        setDouble(uuid, key, getDouble(uuid, key) + amount);
    }

    // ─── Kill timestamps (Thirst) ─────────────────────────────────────────────

    public void recordKill(UUID uuid) {
        killTimestamps.computeIfAbsent(uuid, k -> new ArrayList<>()).add(System.currentTimeMillis());
        lastKillTime.put(uuid, System.currentTimeMillis());
    }

    public int getRecentKillCount(UUID uuid, long windowMs) {
        List<Long> times = killTimestamps.get(uuid);
        if (times == null) return 0;
        long threshold = System.currentTimeMillis() - windowMs;
        times.removeIf(t -> t < threshold);
        return times.size();
    }

    // ─── Swing time (Dormant) ────────────────────────────────────────────────

    public void recordSwing(UUID uuid) {
        lastSwingTime.put(uuid, System.currentTimeMillis());
    }

    public long getLastSwingTime(UUID uuid) {
        return lastSwingTime.getOrDefault(uuid, 0L);
    }

    public boolean isIdle(UUID uuid, long idleThresholdMs) {
        return System.currentTimeMillis() - getLastSwingTime(uuid) >= idleThresholdMs;
    }
    public boolean isIdle(UUID uuid) { return isIdle(uuid, 2000L); }

    // ─── Last melee damage received (Riposte Shot) ──────────────────────────

    public void recordMeleeDamageReceived(UUID uuid) {
        lastMeleeDamageTime.put(uuid, System.currentTimeMillis());
    }

    public long getLastMeleeDamageTime(UUID uuid) {
        return lastMeleeDamageTime.getOrDefault(uuid, 0L);
    }

    // ─── Grudge damage map ───────────────────────────────────────────────────

    public void addGrudgeDamage(UUID self, UUID attacker, double damage) {
        grudgeDamage.computeIfAbsent(self, k -> new ConcurrentHashMap<>())
            .merge(attacker, damage, (a, b) -> a + b);
    }

    public double getGrudgeDamage(UUID self, UUID attacker) {
        return grudgeDamage.getOrDefault(self, Collections.emptyMap()).getOrDefault(attacker, 0.0);
    }

    public void clearGrudgeDamage(UUID self, UUID attacker) {
        Map<UUID, Double> map = grudgeDamage.get(self);
        if (map != null) map.remove(attacker);
    }

    // ─── Hit counters per target (Phase) ────────────────────────────────────

    public int getHitCount(UUID attacker, UUID target) {
        return hitCounters.getOrDefault(attacker, Collections.emptyMap()).getOrDefault(target, 0);
    }

    public int incrementHitCount(UUID attacker, UUID target) {
        int val = getHitCount(attacker, target) + 1;
        hitCounters.computeIfAbsent(attacker, k -> new ConcurrentHashMap<>()).put(target, val);
        return val;
    }

    public void resetHitCount(UUID attacker, UUID target) {
        Map<UUID, Integer> map = hitCounters.get(attacker);
        if (map != null) map.remove(target);
    }

    // ─── Tether targets ──────────────────────────────────────────────────────

    public void setTetherTarget(UUID attacker, UUID target) { tetherTargets.put(attacker, target); }
    public UUID getTetherTarget(UUID attacker) { return tetherTargets.get(attacker); }
    public void clearTether(UUID attacker) { tetherTargets.remove(attacker); }

    // ─── Bleed stacks ────────────────────────────────────────────────────────

    public int getBleedStacks(UUID target) { return bleedStacks.getOrDefault(target, 0); }
    public void setBleedStacks(UUID target, int stacks) { bleedStacks.put(target, stacks); }
    public int addBleedStack(UUID target) {
        int val = getBleedStacks(target) + 1;
        bleedStacks.put(target, val);
        return val;
    }
    public void clearBleedStacks(UUID target) { bleedStacks.remove(target); }

    // ─── Weight stacks (Axe) ────────────────────────────────────────────────

    public int getWeightStacks(UUID uuid) { return weightStacks.getOrDefault(uuid, 0); }
    public void setWeightStacks(UUID uuid, int stacks) { weightStacks.put(uuid, stacks); }
    public int addWeightStack(UUID uuid) {
        int val = getWeightStacks(uuid) + 1;
        weightStacks.put(uuid, val);
        return val;
    }

    // ─── Entropy stacks ──────────────────────────────────────────────────────

    public int getEntropyStacks(UUID uuid) { return entropyStacks.getOrDefault(uuid, 0); }
    public void setEntropyStacks(UUID uuid, int stacks) { entropyStacks.put(uuid, stacks); }
    public long getLastSwingEntropyTime(UUID uuid) { return lastSwingEntropyTime.getOrDefault(uuid, 0L); }
    public void setLastSwingEntropyTime(UUID uuid, long time) { lastSwingEntropyTime.put(uuid, time); }

    // ─── Backswing ───────────────────────────────────────────────────────────

    public boolean isBackswingReady(UUID uuid) {
        Boolean ready = backswingReady.get(uuid);
        Long expiry = backswingExpiry.get(uuid);
        if (ready == null || !ready) return false;
        if (expiry != null && System.currentTimeMillis() > expiry) {
            backswingReady.put(uuid, false);
            return false;
        }
        return true;
    }
    public void setBackswingReady(UUID uuid, long expiryMs) {
        backswingReady.put(uuid, true);
        backswingExpiry.put(uuid, expiryMs);
    }
    public void clearBackswing(UUID uuid) { backswingReady.put(uuid, false); }

    // ─── Crossbow shot counter ────────────────────────────────────────────────

    public int getCrossbowShotCounter(UUID uuid) { return crossbowShotCounter.getOrDefault(uuid, 0); }
    public int incrementCrossbowShotCounter(UUID uuid) {
        int val = getCrossbowShotCounter(uuid) + 1;
        crossbowShotCounter.put(uuid, val);
        return val;
    }

    // ─── Discharge first shot ────────────────────────────────────────────────

    public boolean isFirstShotReady(UUID uuid) { return firstShotReady.getOrDefault(uuid, false); }
    public void setFirstShotReady(UUID uuid, boolean ready) { firstShotReady.put(uuid, ready); }

    // ─── Crouch start time (Deadeye) ─────────────────────────────────────────

    public void recordCrouchStart(UUID uuid) { crouchStartTime.put(uuid, System.currentTimeMillis()); }
    public long getCrouchDurationMs(UUID uuid) {
        Long start = crouchStartTime.get(uuid);
        return start != null ? System.currentTimeMillis() - start : 0;
    }
    public void clearCrouch(UUID uuid) { crouchStartTime.remove(uuid); }

    // ─── Veneer marks ────────────────────────────────────────────────────────

    public boolean isVeneerMarked(UUID entityUUID) {
        Long expire = veneerMarked.get(entityUUID);
        return expire != null && System.currentTimeMillis() < expire;
    }
    public void setVeneerMark(UUID entityUUID, long durationMs) {
        veneerMarked.put(entityUUID, System.currentTimeMillis() + durationMs);
    }

    // ─── Circuit ────────────────────────────────────────────────────────────

    public void recordCircuitHit(UUID attacker, UUID target, Location loc) {
        circuitLastHitTime.computeIfAbsent(attacker, k -> new ConcurrentHashMap<>()).put(target, System.currentTimeMillis());
        circuitLastHitLoc.computeIfAbsent(attacker, k -> new ConcurrentHashMap<>()).put(target, loc);
    }

    public long getCircuitLastHitTime(UUID attacker, UUID target) {
        return circuitLastHitTime.getOrDefault(attacker, Collections.emptyMap()).getOrDefault(target, 0L);
    }

    public Location getCircuitLastHitLoc(UUID attacker, UUID target) {
        return circuitLastHitLoc.getOrDefault(attacker, Collections.emptyMap()).get(target);
    }

    // ─── Stride ─────────────────────────────────────────────────────────────

    public int getStrideStacks(UUID uuid) { return strideStacks.getOrDefault(uuid, 0); }
    public void setStrideStacks(UUID uuid, int stacks) { strideStacks.put(uuid, stacks); }
    public long getStrideLastUpdate(UUID uuid) { return strideLastUpdate.getOrDefault(uuid, 0L); }
    public void setStrideLastUpdate(UUID uuid, long time) { strideLastUpdate.put(uuid, time); }

    // ─── Standing time (Obelisk) ─────────────────────────────────────────────

    public void setStandingStart(UUID uuid, Location loc) {
        standingStartTime.put(uuid, System.currentTimeMillis());
        lastKnownLocation.put(uuid, loc);
    }
    public long getStandingDurationMs(UUID uuid) {
        Long start = standingStartTime.get(uuid);
        return start != null ? System.currentTimeMillis() - start : 0;
    }
    public Location getLastKnownLocation(UUID uuid) { return lastKnownLocation.get(uuid); }
    public void updateLastKnownLocation(UUID uuid, Location loc) { lastKnownLocation.put(uuid, loc); }

    // ─── Nomad distance ──────────────────────────────────────────────────────

    public void addDistanceTraveled(UUID uuid, double distance) {
        totalDistanceTraveled.merge(uuid, distance, (a, b) -> a + b);
        lastMovementTime.put(uuid, System.currentTimeMillis());
    }
    public double getTotalDistanceTraveled(UUID uuid) { return totalDistanceTraveled.getOrDefault(uuid, 0.0); }
    public void resetDistance(UUID uuid) { totalDistanceTraveled.put(uuid, 0.0); }
    public long getLastMovementTime(UUID uuid) { return lastMovementTime.getOrDefault(uuid, 0L); }

    // ─── Pathfinder stepped blocks ───────────────────────────────────────────

    public boolean hasSteppedBlock(UUID uuid, long blockKey) {
        return steppedBlocks.getOrDefault(uuid, Collections.emptySet()).contains(blockKey);
    }
    public void markBlockStepped(UUID uuid, long blockKey) {
        steppedBlocks.computeIfAbsent(uuid, k -> ConcurrentHashMap.newKeySet()).add(blockKey);
    }

    // ─── Static charge distance ──────────────────────────────────────────────

    public double getStaticChargeDistance(UUID uuid) { return staticChargeDistance.getOrDefault(uuid, 0.0); }
    public void addStaticChargeDistance(UUID uuid, double dist) {
        staticChargeDistance.merge(uuid, dist, (a, b) -> a + b);
    }
    public void resetStaticCharge(UUID uuid) { staticChargeDistance.put(uuid, 0.0); }

    // ─── Fortify stacks ──────────────────────────────────────────────────────

    public int getFortifyStacks(UUID uuid) { return fortifyStacks.getOrDefault(uuid, 0); }
    public void setFortifyStacks(UUID uuid, int stacks) { fortifyStacks.put(uuid, stacks); }
    public void setFortifyLastMoveTime(UUID uuid) { fortifyLastMoveTime.put(uuid, System.currentTimeMillis()); }
    public long getFortifyLastMoveTime(UUID uuid) { return fortifyLastMoveTime.getOrDefault(uuid, 0L); }

    // ─── Scar tissue resist ──────────────────────────────────────────────────

    public int getScarResist(UUID uuid, String damageType) {
        return scarResist.getOrDefault(uuid, Collections.emptyMap()).getOrDefault(damageType, 0);
    }
    public void addScarResist(UUID uuid, String damageType, int amount) {
        scarResist.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).merge(damageType, amount, (a, b) -> a + b);
    }

    /** Accumulated raw damage for ScarTissue enchant. */
    public void addScarResist(UUID uuid, double damageAmount) {
        addDouble(uuid, "scar_damage_total", damageAmount);
    }
    /** Returns ratio of accumulated damage / 10 (1% DR per 10 raw damage). */
    public double getScarResist(UUID uuid) {
        return getDouble(uuid, "scar_damage_total", 0.0) / 10.0;
    }

    // ─── Reservoir stored heal ───────────────────────────────────────────────

    public double getReservoirStored(UUID uuid) { return reservoirStored.getOrDefault(uuid, 0.0); }
    public void setReservoirStored(UUID uuid, double amount) { reservoirStored.put(uuid, amount); }
    public void addReservoirStored(UUID uuid, double amount) { reservoirStored.merge(uuid, amount, (a, b) -> a + b); }

    // ─── Ironheart absorption ────────────────────────────────────────────────

    public void setIronheartWearStart(UUID uuid) { ironheartWearStart.put(uuid, System.currentTimeMillis()); }
    public long getIronheartWearStart(UUID uuid) { return ironheartWearStart.getOrDefault(uuid, System.currentTimeMillis()); }
    public int getIronheartAbsorption(UUID uuid) { return ironheartAbsorption.getOrDefault(uuid, 0); }
    public void setIronheartAbsorption(UUID uuid, int amount) { ironheartAbsorption.put(uuid, amount); }

    // ─── Flight distance (Sonic elytra) ──────────────────────────────────────

    public void addFlightDistance(UUID uuid, double dist) { flightDistance.merge(uuid, dist, (a, b) -> a + b); }
    public double getFlightDistance(UUID uuid) { return flightDistance.getOrDefault(uuid, 0.0); }
    public void resetFlightDistance(UUID uuid) { flightDistance.put(uuid, 0.0); }
    public Location getLastFlightLocation(UUID uuid) { return lastFlightLocation.get(uuid); }
    public void setLastFlightLocation(UUID uuid, Location loc) { lastFlightLocation.put(uuid, loc); }

    // ─── Capacitor charge ────────────────────────────────────────────────────

    public int getCapacitorCharge(UUID uuid) { return capacitorCharge.getOrDefault(uuid, 0); }
    public void setCapacitorCharge(UUID uuid, int charge) { capacitorCharge.put(uuid, charge); }
    public void addWalkDistanceForCapacitor(UUID uuid, double dist) { walkDistanceForCapacitor.merge(uuid, dist, (a, b) -> a + b); }
    public double getWalkDistanceForCapacitor(UUID uuid) { return walkDistanceForCapacitor.getOrDefault(uuid, 0.0); }
    public void resetWalkDistanceForCapacitor(UUID uuid) { walkDistanceForCapacitor.put(uuid, 0.0); }

    // ─── Thirst stacks ───────────────────────────────────────────────────────

    public int getThirstStacks(UUID uuid) { return thirstStacks.getOrDefault(uuid, 0); }
    public void setThirstStacks(UUID uuid, int stacks) { thirstStacks.put(uuid, stacks); }

    // ─── Verdict recent attackers ────────────────────────────────────────────

    public void recordEntityAttacker(UUID entityUUID, UUID attackerUUID) {
        entityRecentAttackers.computeIfAbsent(entityUUID, k -> new ConcurrentHashMap<>())
            .put(attackerUUID, System.currentTimeMillis());
    }

    public int getRecentAttackerCount(UUID entityUUID, long windowMs) {
        Map<UUID, Long> map = entityRecentAttackers.get(entityUUID);
        if (map == null) return 0;
        long threshold = System.currentTimeMillis() - windowMs;
        map.entrySet().removeIf(e -> e.getValue() < threshold);
        return map.size();
    }

    // ─── Fishing biomes ──────────────────────────────────────────────────────

    public boolean hasVisitedBiome(UUID uuid, String biome) {
        return fishedBiomes.getOrDefault(uuid, Collections.emptySet()).contains(biome);
    }
    public void markBiomeVisited(UUID uuid, String biome) {
        fishedBiomes.computeIfAbsent(uuid, k -> ConcurrentHashMap.newKeySet()).add(biome);
    }

    // ─── Pulse hit counter ───────────────────────────────────────────────────

    public int getPulseHitCounter(UUID uuid) { return pulseHitCounter.getOrDefault(uuid, 0); }
    public int incrementPulseHitCounter(UUID uuid) {
        int val = getPulseHitCounter(uuid) + 1;
        pulseHitCounter.put(uuid, val);
        return val;
    }
    public int incrementPulseHitCounter(UUID uuid, int entityId) { return incrementPulseHitCounter(uuid); }
    public void resetPulseCounter(UUID uuid) { pulseHitCounter.put(uuid, 0); }
    public void resetPulseCounter(UUID uuid, int entityId) { resetPulseCounter(uuid); }

    // ─── Cleanup on disconnect ───────────────────────────────────────────────

    public void cleanupPlayer(UUID uuid) {
        // Keep scar tissue and nomad distance across sessions; clean most runtime data
        killTimestamps.remove(uuid);
        lastSwingTime.remove(uuid);
        lastMeleeDamageTime.remove(uuid);
        bleedStacks.remove(uuid);
        weightStacks.remove(uuid);
        entropyStacks.remove(uuid);
        lastSwingEntropyTime.remove(uuid);
        backswingReady.remove(uuid);
        backswingExpiry.remove(uuid);
        crossbowShotCounter.remove(uuid);
        firstShotReady.remove(uuid);
        crouchStartTime.remove(uuid);
        veneerMarked.remove(uuid);
        circuitLastHitTime.remove(uuid);
        circuitLastHitLoc.remove(uuid);
        strideStacks.remove(uuid);
        strideLastUpdate.remove(uuid);
        standingStartTime.remove(uuid);
        entityRecentAttackers.remove(uuid);
        thirstStacks.remove(uuid);
        pulseHitCounter.remove(uuid);
    }
}
