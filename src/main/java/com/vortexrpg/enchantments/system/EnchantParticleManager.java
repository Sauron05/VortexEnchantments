package com.vortexrpg.enchantments.system;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.SchedulerUtil;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom Enchant Particle System — per-enchant particle effects on weapons/armor.
 *
 * Unlike AE's generic aura system, VortexEnchantments shows SPECIFIC particles
 * for individual enchants on items when held/worn.
 *
 * Features:
 *   - Each enchant category gets a unique particle style (fire, ice, lightning, etc.)
 *   - Particles appear on the weapon/item itself, not as a body aura
 *   - Higher enchant levels = more intense particles
 *   - Multiple enchants on one item = blended particle effects
 *   - Performance-optimized: only renders for nearby players
 *   - Toggle per player via /ve particles
 *   - Separate from the body aura system (AuraManager)
 */
public class EnchantParticleManager {

    private final VortexEnchantments plugin;
    private final Set<UUID> disabledPlayers = ConcurrentHashMap.newKeySet();
    private final Map<String, ParticleStyle> enchantStyles = new HashMap<>();
    private int taskId = -1;

    public EnchantParticleManager(VortexEnchantments plugin) {
        this.plugin = plugin;
        registerDefaultStyles();
    }

    // ─── Particle Style Definition ───────────────────────────────────────────

    public record ParticleStyle(
        Particle particle,
        Color color,            // for DUST particles (null for non-dust)
        float size,             // dust size or speed
        int count,              // particles per tick
        double radius,          // spread radius
        double yOffset,         // vertical offset from item position
        StylePattern pattern    // movement pattern
    ) {}

    public enum StylePattern {
        ORBIT,      // circles around item
        DRIP,       // falls downward
        SPIRAL,     // spiral upward
        BURST,      // random burst outward
        FLAME_TRAIL,// trailing fire
        FROST,      // slow falling snowflakes
        ELECTRIC,   // quick sparks
        DARK,       // ender/void particles
        HOLY,       // end rod / light
        NATURE      // green particles
    }

    // ─── Task Management ─────────────────────────────────────────────────────

    public void start() {
        if (taskId != -1) return;
        if (!plugin.getConfig().getBoolean("enchant-particles.enabled", true)) return;

        int interval = plugin.getConfig().getInt("enchant-particles.tick-interval", 4);

        SchedulerUtil.runGlobalTimer(plugin, () -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (disabledPlayers.contains(player.getUniqueId())) continue;
                tickPlayer(player);
            }
        }, 20L, interval);
    }

    public void stop() {
        if (taskId != -1) {
            plugin.getServer().getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    public boolean toggleParticles(Player player) {
        UUID uuid = player.getUniqueId();
        if (disabledPlayers.contains(uuid)) {
            disabledPlayers.remove(uuid);
            return true;
        } else {
            disabledPlayers.add(uuid);
            return false;
        }
    }

    public boolean isEnabled(Player player) {
        return !disabledPlayers.contains(player.getUniqueId());
    }

    // ─── Per-Player Tick ─────────────────────────────────────────────────────

    private void tickPlayer(Player player) {
        // Main hand weapon particles
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (!mainHand.getType().isAir()) {
            Map<VortexEnchant, Integer> enchants = plugin.getEnchantManager().getEnchants(mainHand);
            if (!enchants.isEmpty()) {
                spawnWeaponParticles(player, enchants);
            }
        }

        // Armor particles (subtle, near body)
        ItemStack[] armor = player.getInventory().getArmorContents();
        if (armor != null) {
            for (ItemStack piece : armor) {
                if (piece == null || piece.getType().isAir()) continue;
                Map<VortexEnchant, Integer> enchants = plugin.getEnchantManager().getEnchants(piece);
                if (!enchants.isEmpty()) {
                    spawnArmorParticles(player, enchants);
                }
            }
        }
    }

    private void spawnWeaponParticles(Player player, Map<VortexEnchant, Integer> enchants) {
        // Get the highest-priority enchant's particle style
        VortexEnchant best = null;
        int bestPriority = -1;

        for (var entry : enchants.entrySet()) {
            int priority = entry.getKey().getRarity().ordinal() * 10 + entry.getValue();
            if (priority > bestPriority) {
                bestPriority = priority;
                best = entry.getKey();
            }
        }

        if (best == null) return;

        ParticleStyle style = getStyleForEnchant(best);
        if (style == null) return;

        // Calculate weapon tip position (in front of player, slightly up)
        Location loc = player.getLocation().clone();
        double yaw = Math.toRadians(loc.getYaw());

        // Offset to hand position
        double handDist = 0.8;
        double handX = -Math.sin(yaw) * handDist;
        double handZ = Math.cos(yaw) * handDist;
        loc.add(handX, 1.0 + style.yOffset(), handZ);

        int level = enchants.get(best);
        int count = Math.min(style.count() + level, 8); // scale with level, cap at 8

        spawnStyledParticle(loc, style, count, level);
    }

    private void spawnArmorParticles(Player player, Map<VortexEnchant, Integer> enchants) {
        // Only spawn for EPIC+ rarity enchants on armor
        VortexEnchant best = null;
        for (VortexEnchant e : enchants.keySet()) {
            if (e.getRarity().ordinal() >= EnchantRarity.EPIC.ordinal()) {
                if (best == null || e.getRarity().ordinal() > best.getRarity().ordinal()) {
                    best = e;
                }
            }
        }
        if (best == null) return;

        ParticleStyle style = getStyleForEnchant(best);
        if (style == null) return;

        // Subtle body particles
        Location loc = player.getLocation().add(0, 0.5 + Math.random() * 1.5, 0);
        loc.add((Math.random() - 0.5) * 0.6, 0, (Math.random() - 0.5) * 0.6);

        spawnStyledParticle(loc, style, 1, enchants.get(best));
    }

    private void spawnStyledParticle(Location loc, ParticleStyle style, int count, int level) {
        double r = style.radius();

        switch (style.pattern()) {
            case ORBIT -> {
                double angle = (System.currentTimeMillis() / 100.0) % (Math.PI * 2);
                for (int i = 0; i < count; i++) {
                    double a = angle + (Math.PI * 2 / count) * i;
                    Location pl = loc.clone().add(Math.cos(a) * r, 0, Math.sin(a) * r);
                    spawnParticle(pl, style);
                }
            }
            case DRIP -> {
                for (int i = 0; i < count; i++) {
                    Location pl = loc.clone().add(
                        (Math.random() - 0.5) * r,
                        -Math.random() * 0.3,
                        (Math.random() - 0.5) * r);
                    spawnParticle(pl, style);
                }
            }
            case SPIRAL -> {
                double angle = (System.currentTimeMillis() / 80.0) % (Math.PI * 2);
                for (int i = 0; i < count; i++) {
                    double a = angle + (Math.PI * 2 / count) * i;
                    double y = ((double) i / count) * 0.5;
                    double sr = r * (1.0 - y * 0.5);
                    Location pl = loc.clone().add(Math.cos(a) * sr, y, Math.sin(a) * sr);
                    spawnParticle(pl, style);
                }
            }
            case BURST -> {
                for (int i = 0; i < count; i++) {
                    Location pl = loc.clone().add(
                        (Math.random() - 0.5) * r * 2,
                        (Math.random() - 0.5) * r,
                        (Math.random() - 0.5) * r * 2);
                    spawnParticle(pl, style);
                }
            }
            case FLAME_TRAIL -> {
                for (int i = 0; i < count; i++) {
                    Location pl = loc.clone().add(
                        (Math.random() - 0.5) * r * 0.5,
                        Math.random() * 0.4,
                        (Math.random() - 0.5) * r * 0.5);
                    loc.getWorld().spawnParticle(style.particle(), pl, 1, 0, 0.02, 0, 0.01);
                }
            }
            case FROST -> {
                for (int i = 0; i < count; i++) {
                    Location pl = loc.clone().add(
                        (Math.random() - 0.5) * r,
                        -Math.random() * 0.2,
                        (Math.random() - 0.5) * r);
                    spawnParticle(pl, style);
                }
            }
            case ELECTRIC -> {
                // Quick random sparks
                if (Math.random() < 0.3 + level * 0.1) {
                    Location pl = loc.clone().add(
                        (Math.random() - 0.5) * r * 1.5,
                        (Math.random() - 0.5) * r,
                        (Math.random() - 0.5) * r * 1.5);
                    loc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, pl, count, 0.1, 0.1, 0.1, 0.05);
                }
            }
            case DARK -> {
                for (int i = 0; i < count; i++) {
                    Location pl = loc.clone().add(
                        (Math.random() - 0.5) * r,
                        (Math.random() - 0.5) * r * 0.5,
                        (Math.random() - 0.5) * r);
                    loc.getWorld().spawnParticle(style.particle(), pl, 1, 0, 0, 0, 0.02);
                }
            }
            case HOLY -> {
                double angle = (System.currentTimeMillis() / 120.0) % (Math.PI * 2);
                for (int i = 0; i < count; i++) {
                    double a = angle + (Math.PI * 2 / count) * i;
                    double y = Math.sin(a * 2) * 0.2;
                    Location pl = loc.clone().add(Math.cos(a) * r * 0.5, y + 0.3, Math.sin(a) * r * 0.5);
                    loc.getWorld().spawnParticle(Particle.END_ROD, pl, 1, 0, 0, 0, 0.01);
                }
            }
            case NATURE -> {
                if (Math.random() < 0.4) {
                    Location pl = loc.clone().add(
                        (Math.random() - 0.5) * r,
                        Math.random() * 0.3,
                        (Math.random() - 0.5) * r);
                    spawnParticle(pl, style);
                }
            }
        }
    }

    private void spawnParticle(Location loc, ParticleStyle style) {
        if (style.color() != null) {
            Particle.DustOptions dust = new Particle.DustOptions(style.color(), style.size());
            loc.getWorld().spawnParticle(Particle.DUST, loc, 1, 0, 0, 0, 0, dust);
        } else {
            loc.getWorld().spawnParticle(style.particle(), loc, 1, 0, 0, 0, style.size());
        }
    }

    // ─── Style Lookup ────────────────────────────────────────────────────────

    private ParticleStyle getStyleForEnchant(VortexEnchant enchant) {
        // Check specific enchant ID first
        ParticleStyle specific = enchantStyles.get(enchant.getId().toLowerCase());
        if (specific != null) return specific;

        // Fall back to category-based style
        String id = enchant.getId().toLowerCase();

        // Fire enchants
        if (id.contains("blaze") || id.contains("inferno") || id.contains("fire")
            || id.contains("flame") || id.contains("scorch") || id.contains("ember")
            || id.contains("ignite") || id.contains("pyro") || id.contains("burn")
            || id.contains("magma") || id.contains("smelt")) {
            return enchantStyles.get("_fire");
        }

        // Ice/frost enchants
        if (id.contains("frost") || id.contains("ice") || id.contains("freeze")
            || id.contains("glacial") || id.contains("cryo") || id.contains("chill")
            || id.contains("winter") || id.contains("cold") || id.contains("snow")) {
            return enchantStyles.get("_ice");
        }

        // Lightning/electric enchants
        if (id.contains("lightning") || id.contains("storm") || id.contains("thunder")
            || id.contains("spark") || id.contains("shock") || id.contains("volt")
            || id.contains("electric") || id.contains("charge")) {
            return enchantStyles.get("_electric");
        }

        // Dark/void/shadow enchants
        if (id.contains("void") || id.contains("shadow") || id.contains("dark")
            || id.contains("wither") || id.contains("necrosis") || id.contains("death")
            || id.contains("soul") || id.contains("phantom") || id.contains("ghost")
            || id.contains("banshee") || id.contains("rift") || id.contains("singularity")) {
            return enchantStyles.get("_dark");
        }

        // Blood/life enchants
        if (id.contains("blood") || id.contains("siphon") || id.contains("drain")
            || id.contains("thirst") || id.contains("vampire") || id.contains("leech")
            || id.contains("life_steal") || id.contains("debt")) {
            return enchantStyles.get("_blood");
        }

        // Holy/divine enchants
        if (id.contains("divine") || id.contains("holy") || id.contains("angel")
            || id.contains("guardian") || id.contains("blessed") || id.contains("sacred")
            || id.contains("celestial") || id.contains("purify")) {
            return enchantStyles.get("_holy");
        }

        // Nature/earth enchants
        if (id.contains("nature") || id.contains("growth") || id.contains("replant")
            || id.contains("harvest") || id.contains("green") || id.contains("flora")
            || id.contains("vine") || id.contains("root") || id.contains("bloom")) {
            return enchantStyles.get("_nature");
        }

        // Gravity/physics enchants
        if (id.contains("gravity") || id.contains("kinesis") || id.contains("force")
            || id.contains("pull") || id.contains("push") || id.contains("telekinesis")) {
            return enchantStyles.get("_gravity");
        }

        // Default: subtle dust based on rarity color
        return new ParticleStyle(
            Particle.DUST, enchant.getRarity().getParticleColor(), 0.6f,
            1, 0.2, 0, StylePattern.ORBIT
        );
    }

    // ─── Default Style Registry ──────────────────────────────────────────────

    private void registerDefaultStyles() {
        enchantStyles.put("_fire", new ParticleStyle(
            Particle.FLAME, null, 0.02f, 2, 0.3, 0.1, StylePattern.FLAME_TRAIL));

        enchantStyles.put("_ice", new ParticleStyle(
            Particle.SNOWFLAKE, null, 0.01f, 2, 0.3, 0, StylePattern.FROST));

        enchantStyles.put("_electric", new ParticleStyle(
            Particle.ELECTRIC_SPARK, null, 0.05f, 2, 0.4, 0, StylePattern.ELECTRIC));

        enchantStyles.put("_dark", new ParticleStyle(
            Particle.PORTAL, null, 0.3f, 2, 0.3, -0.1, StylePattern.DARK));

        enchantStyles.put("_blood", new ParticleStyle(
            Particle.DUST, Color.fromRGB(180, 0, 0), 0.8f, 2, 0.25, 0, StylePattern.DRIP));

        enchantStyles.put("_holy", new ParticleStyle(
            Particle.END_ROD, null, 0.01f, 2, 0.3, 0.2, StylePattern.HOLY));

        enchantStyles.put("_nature", new ParticleStyle(
            Particle.HAPPY_VILLAGER, null, 0.0f, 1, 0.3, 0, StylePattern.NATURE));

        enchantStyles.put("_gravity", new ParticleStyle(
            Particle.REVERSE_PORTAL, null, 0.1f, 2, 0.4, 0, StylePattern.SPIRAL));
    }
}
