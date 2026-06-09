package com.vortexrpg.enchantments.util;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Utility methods for spawning particles efficiently.
 */
public final class ParticleUtil {

    private ParticleUtil() {}

    public static void spawn(Location loc, Particle particle, int count, double spread) {
        if (loc.getWorld() == null) return;
        loc.getWorld().spawnParticle(particle, loc, count, spread, spread, spread, 0);
    }

    public static void spawn(Location loc, Particle particle, int count, double spreadX, double spreadY, double spreadZ) {
        if (loc.getWorld() == null) return;
        loc.getWorld().spawnParticle(particle, loc, count, spreadX, spreadY, spreadZ, 0);
    }

    public static void spawn(Location loc, Particle particle, int count, double spread, double extra) {
        if (loc.getWorld() == null) return;
        loc.getWorld().spawnParticle(particle, loc, count, spread, spread, spread, extra);
    }

    public static void spawnForPlayer(Player player, Location loc, Particle particle, int count, double spread) {
        player.spawnParticle(particle, loc, count, spread, spread, spread, 0);
    }

    public static <T> void spawn(Location loc, Particle particle, int count, double spread, T data) {
        if (loc.getWorld() == null) return;
        loc.getWorld().spawnParticle(particle, loc, count, spread, spread, spread, 0, data);
    }

    /** Draw a line of particles between two locations. */
    public static void drawLine(Location from, Location to, Particle particle, double spacing) {
        if (from.getWorld() == null || !from.getWorld().equals(to.getWorld())) return;
        double distance = from.distance(to);
        int steps = (int) Math.ceil(distance / spacing);
        if (steps <= 0) return;
        double dx = (to.getX() - from.getX()) / steps;
        double dy = (to.getY() - from.getY()) / steps;
        double dz = (to.getZ() - from.getZ()) / steps;
        World world = from.getWorld();
        double x = from.getX(), y = from.getY(), z = from.getZ();
        for (int i = 0; i <= steps; i++) {
            world.spawnParticle(particle, x, y, z, 1, 0, 0, 0, 0);
            x += dx; y += dy; z += dz;
        }
    }

    /** Draw a circle of particles at a location's level. */
    public static void drawCircle(Location center, double radius, int points, Particle particle) {
        if (center.getWorld() == null) return;
        World world = center.getWorld();
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            world.spawnParticle(particle, x, center.getY(), z, 1, 0, 0, 0, 0);
        }
    }

    /** Send particles only to a specific collection of players (for server-side-only visuals). */
    public static void spawnForPlayers(Collection<Player> players, Location loc, Particle particle, int count, double spread) {
        for (Player p : players) {
            p.spawnParticle(particle, loc, count, spread, spread, spread, 0);
        }
    }

    /** Helix/spiral effect around an entity. */
    public static void spawnHelix(Location center, Particle particle, int rings, double height) {
        if (center.getWorld() == null) return;
        World world = center.getWorld();
        int pointsPerRing = 12;
        for (int i = 0; i < rings * pointsPerRing; i++) {
            double angle = 2 * Math.PI * i / pointsPerRing;
            double y = center.getY() + (height * i) / (rings * pointsPerRing);
            double x = center.getX() + 0.5 * Math.cos(angle);
            double z = center.getZ() + 0.5 * Math.sin(angle);
            world.spawnParticle(particle, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    /** Burst of particles outward from a location. */
    public static void burst(Location loc, Particle particle, int count, double spread) {
        spawn(loc, particle, count, spread);
    }

    /** Ring of particles at a location's level. */
    public static void ring(Location center, Particle particle, int points, double radius) {
        drawCircle(center, radius, points, particle);
    }

    /** Trail of particles along a short walk. */
    public static void trail(Location loc, Particle particle, int count, double spread) {
        spawn(loc, particle, count, spread);
    }
}
