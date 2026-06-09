package com.vortexrpg.enchantments.fabric.util;

import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
// SoundEvents.* are raw SoundEvent in 1.21.11; wrap with RegistryEntry.of when playing.

/** Helpers for spawning particles and playing sounds on the server. */
public final class Fx {

    private Fx() {}

    public static void particles(ServerWorld world, double x, double y, double z,
                                 ParticleEffect effect, int count, double spread) {
        world.spawnParticles(effect, x, y, z, count, spread, spread, spread, 0.01);
    }

    public static void particlesAt(ServerWorld world, Entity entity,
                                   ParticleEffect effect, int count, double spread) {
        particles(world, entity.getX(), entity.getY() + 1.0, entity.getZ(), effect, count, spread);
    }

    public static void sound(ServerWorld world, double x, double y, double z,
                             SoundEvent sound, float volume, float pitch) {
        world.playSound(null, x, y, z, RegistryEntry.of(sound), SoundCategory.PLAYERS, volume, pitch, 0L);
    }

    public static void soundAt(ServerWorld world, Entity entity,
                               SoundEvent sound, float volume, float pitch) {
        sound(world, entity.getX(), entity.getY(), entity.getZ(), sound, volume, pitch);
    }
}
