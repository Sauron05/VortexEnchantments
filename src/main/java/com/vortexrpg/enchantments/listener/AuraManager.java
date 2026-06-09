package com.vortexrpg.enchantments.listener;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spawns colored particle auras around players based on the highest-rarity
 * VortexEnchant on their held or equipped items.
 */
public class AuraManager {

    private final VortexEnchantments plugin;
    private final Set<UUID> disabledPlayers = ConcurrentHashMap.newKeySet();
    private int taskId = -1;

    public AuraManager(VortexEnchantments plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (taskId != -1) return;
        taskId = new BukkitRunnable() {
            private double angle = 0;

            @Override
            public void run() {
                angle += Math.PI / 8; // rotate each tick
                if (angle > Math.PI * 2) angle -= Math.PI * 2;

                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (disabledPlayers.contains(player.getUniqueId())) continue;

                    EnchantRarity highest = getHighestRarity(player);
                    if (highest == null) continue;

                    spawnAura(player, highest, angle);
                }
            }
        }.runTaskTimer(plugin, 20L, 4L).getTaskId(); // every 4 ticks
    }

    public void stop() {
        if (taskId != -1) {
            plugin.getServer().getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    public boolean toggleAura(Player player) {
        UUID uuid = player.getUniqueId();
        if (disabledPlayers.contains(uuid)) {
            disabledPlayers.remove(uuid);
            return true; // now enabled
        } else {
            disabledPlayers.add(uuid);
            return false; // now disabled
        }
    }

    public boolean isAuraEnabled(Player player) {
        return !disabledPlayers.contains(player.getUniqueId());
    }

    private EnchantRarity getHighestRarity(Player player) {
        EnchantRarity highest = null;

        // Check main hand, off hand, and all armor slots
        ItemStack[] toCheck = {
            player.getInventory().getItemInMainHand(),
            player.getInventory().getItemInOffHand()
        };

        List<ItemStack> allItems = new ArrayList<>(Arrays.asList(toCheck));
        ItemStack[] armor = player.getInventory().getArmorContents();
        if (armor != null) {
            Collections.addAll(allItems, armor);
        }

        for (ItemStack item : allItems) {
            Map<VortexEnchant, Integer> enchants = plugin.getEnchantManager().getEnchants(item);
            for (VortexEnchant enchant : enchants.keySet()) {
                if (highest == null || enchant.getRarity().ordinal() > highest.ordinal()) {
                    highest = enchant.getRarity();
                }
            }
        }
        return highest;
    }

    private void spawnAura(Player player, EnchantRarity rarity, double angle) {
        Location center = player.getLocation().add(0, 0.8, 0);
        Color color = rarity.getParticleColor();
        float size = 0.8f + (rarity.ordinal() * 0.15f);
        Particle.DustOptions dust = new Particle.DustOptions(color, size);

        int points;
        double radius;

        switch (rarity) {
            case COMMON -> {
                // Simple 2 particles floating up
                points = 2;
                radius = 0.4;
                for (int i = 0; i < points; i++) {
                    double a = angle + (Math.PI * 2 / points * i);
                    Location loc = center.clone().add(Math.cos(a) * radius, 0.3 * Math.sin(angle * 2 + i), Math.sin(a) * radius);
                    player.getWorld().spawnParticle(Particle.DUST, loc, 1, 0, 0, 0, 0, dust);
                }
            }
            case UNCOMMON -> {
                // 3 orbiting particles
                points = 3;
                radius = 0.5;
                for (int i = 0; i < points; i++) {
                    double a = angle + (Math.PI * 2 / points * i);
                    Location loc = center.clone().add(Math.cos(a) * radius, 0.4 * Math.sin(angle + i), Math.sin(a) * radius);
                    player.getWorld().spawnParticle(Particle.DUST, loc, 1, 0, 0, 0, 0, dust);
                }
            }
            case RARE -> {
                // Swirling ring
                points = 6;
                radius = 0.6;
                for (int i = 0; i < points; i++) {
                    double a = angle + (Math.PI * 2 / points * i);
                    double y = 0.5 * Math.sin(angle * 1.5 + i * 0.5);
                    Location loc = center.clone().add(Math.cos(a) * radius, y, Math.sin(a) * radius);
                    player.getWorld().spawnParticle(Particle.DUST, loc, 1, 0, 0, 0, 0, dust);
                }
            }
            case EPIC -> {
                // Double helix
                points = 8;
                radius = 0.65;
                for (int i = 0; i < points; i++) {
                    double a = angle + (Math.PI * 2 / points * i);
                    double y = 0.6 * Math.sin(angle * 2 + i * 0.4);
                    Location loc = center.clone().add(Math.cos(a) * radius, y, Math.sin(a) * radius);
                    player.getWorld().spawnParticle(Particle.DUST, loc, 1, 0, 0, 0, 0, dust);
                    // Second helix offset
                    double a2 = a + Math.PI;
                    Location loc2 = center.clone().add(Math.cos(a2) * radius * 0.6, -y, Math.sin(a2) * radius * 0.6);
                    player.getWorld().spawnParticle(Particle.DUST, loc2, 1, 0, 0, 0, 0, dust);
                }
            }
            case LEGENDARY -> {
                // Bright spiral with rising particles
                points = 10;
                radius = 0.7;
                for (int i = 0; i < points; i++) {
                    double a = angle * 1.5 + (Math.PI * 2 / points * i);
                    double y = ((double) i / points) * 1.5 - 0.3;
                    Location loc = center.clone().add(Math.cos(a) * radius, y, Math.sin(a) * radius);
                    player.getWorld().spawnParticle(Particle.DUST, loc, 1, 0, 0, 0, 0, dust);
                }
                // Some extra golden sparkles at top
                Location top = center.clone().add(0, 1.2, 0);
                player.getWorld().spawnParticle(Particle.DUST, top, 2, 0.2, 0.1, 0.2, 0, dust);
            }
            case MYTHIC -> {
                // Intense flame-like spiral with extra flair
                points = 12;
                radius = 0.75;
                for (int i = 0; i < points; i++) {
                    double a = angle * 2 + (Math.PI * 2 / points * i);
                    double y = ((double) i / points) * 1.8 - 0.5;
                    double r = radius * (1.0 - (double) i / points * 0.3);
                    Location loc = center.clone().add(Math.cos(a) * r, y, Math.sin(a) * r);
                    player.getWorld().spawnParticle(Particle.DUST, loc, 1, 0, 0, 0, 0, dust);
                }
                // Flame particles at feet
                player.getWorld().spawnParticle(Particle.FLAME, center.clone().add(0, -0.5, 0), 1, 0.15, 0.05, 0.15, 0.01);
                // Extra red sparkle burst
                Location top = center.clone().add(0, 1.3, 0);
                player.getWorld().spawnParticle(Particle.DUST, top, 3, 0.25, 0.15, 0.25, 0, dust);
            }
        }
    }
}
