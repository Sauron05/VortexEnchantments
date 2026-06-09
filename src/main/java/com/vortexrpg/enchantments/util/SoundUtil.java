package com.vortexrpg.enchantments.util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

/**
 * Utility for playing sounds with default fallbacks and volume/pitch control.
 */
public final class SoundUtil {

    private SoundUtil() {}

    public static void play(Location loc, Sound sound, float volume, float pitch) {
        if (loc.getWorld() == null) return;
        loc.getWorld().playSound(loc, sound, SoundCategory.PLAYERS, volume, pitch);
    }

    public static void play(Location loc, Sound sound) {
        play(loc, sound, 1.0f, 1.0f);
    }

    public static void playToPlayer(Player player, Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, SoundCategory.PLAYERS, volume, pitch);
    }

    public static void playToPlayer(Player player, Sound sound) {
        playToPlayer(player, sound, 1.0f, 1.0f);
    }

    public static void playHit(Location loc) {
        play(loc, Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.8f, 1.2f);
    }

    public static void playMagic(Location loc) {
        play(loc, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 0.7f, 1.5f);
    }

    public static void playExplosion(Location loc) {
        play(loc, Sound.ENTITY_GENERIC_EXPLODE, 0.6f, 1.5f);
    }

    public static void playLevitate(Location loc) {
        play(loc, Sound.ENTITY_SHULKER_HURT, 0.5f, 2.0f);
    }

    public static void playEnchant(Location loc) {
        play(loc, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.8f, 1.0f);
    }

    public static void playAlert(Player player) {
        playToPlayer(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
    }
}
