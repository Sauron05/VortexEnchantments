package com.vortexrpg.enchantments.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

/**
 * Math and geometry helpers.
 */
public final class MathUtil {

    private static final Random RANDOM = new Random();

    private MathUtil() {}

    public static boolean chance(double percent) {
        return RANDOM.nextDouble() * 100 < percent;
    }

    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /** Normalize a vector, returning zero-vector if length is 0. */
    public static Vector normalize(Vector v) {
        double len = v.length();
        return len == 0 ? new Vector(0, 0, 0) : v.clone().multiply(1.0 / len);
    }

    /** Returns random double in [min, max]. */
    public static double randomRange(double min, double max) {
        return min + RANDOM.nextDouble() * (max - min);
    }

    public static int randomRange(int min, int max) {
        return min + RANDOM.nextInt(max - min + 1);
    }

    /** Calculate the position fraction from the bottom of an entity's hitbox (0=feet, 1=top). */
    public static double getHitVerticalFraction(LivingEntity entity, org.bukkit.Location hitLocation) {
        BoundingBox box = entity.getBoundingBox();
        double relY = hitLocation.getY() - box.getMinY();
        double height = box.getHeight();
        if (height <= 0) return 0.5;
        return clamp(relY / height, 0, 1);
    }

    /** Returns true if the hit location is in the top fraction of the hitbox (headshot zone). */
    public static boolean isHeadshot(LivingEntity entity, org.bukkit.Location hitLocation, double topFraction) {
        return getHitVerticalFraction(entity, hitLocation) >= (1.0 - topFraction);
    }

    /** Get all living entities within radius (excluding one). */
    public static List<LivingEntity> getNearbyLiving(org.bukkit.Location center, double radius, Entity exclude) {
        List<LivingEntity> result = new ArrayList<>();
        for (Entity e : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (e instanceof LivingEntity le && (exclude == null || !e.equals(exclude))) {
                result.add(le);
            }
        }
        return result;
    }

    /** Get all living entities within radius. */
    public static List<LivingEntity> getNearbyLiving(org.bukkit.Location center, double radius) {
        return getNearbyLiving(center, radius, (Entity) null);
    }

    /** Get nearest living entity to a location within radius (excluding one). */
    public static LivingEntity getNearestLiving(org.bukkit.Location center, double radius, Entity exclude) {
        LivingEntity nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (LivingEntity e : getNearbyLiving(center, radius, exclude)) {
            double dist = e.getLocation().distanceSquared(center);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = e;
            }
        }
        return nearest;
    }

    /** Get nearest living entity matching a predicate. */
    public static LivingEntity getNearestLiving(org.bukkit.Location center, double radius, Predicate<Entity> filter) {
        LivingEntity nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (Entity e : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (e instanceof LivingEntity le && filter.test(e)) {
                double dist = e.getLocation().distanceSquared(center);
                if (dist < nearestDist) {
                    nearestDist = dist;
                    nearest = le;
                }
            }
        }
        return nearest;
    }

    /** Get nearest living entity within radius (no exclusion). */
    public static LivingEntity getNearestLiving(org.bukkit.Location center, double radius) {
        return getNearestLiving(center, radius, (Entity) null);
    }

    /** Encode a block location to a unique long key. */
    public static long blockKey(org.bukkit.block.Block block) {
        return blockKey(block.getX(), block.getY(), block.getZ());
    }

    public static long blockKey(int x, int y, int z) {
        return ((long) (x & 0x3FFFFFF) << 38) | ((long) (z & 0x3FFFFFF) << 12) | (y & 0xFFF);
    }

    /** Convert ticks to seconds (double). */
    public static double ticksToSeconds(int ticks) {
        return ticks / 20.0;
    }

    /** Convert seconds to ticks (int). */
    public static long secondsToTicks(double seconds) {
        return Math.round(seconds * 20);
    }
}
