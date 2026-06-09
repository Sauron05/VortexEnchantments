package com.vortexrpg.enchantments.listener;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Handles enchanted book drops from mobs.
 * Config-driven: each mob type has a base drop chance + rarity weights.
 */
public class MobDropListener implements Listener {

    private final VortexEnchantments plugin;
    private FileConfiguration dropConfig;

    // Cached per-mob-type drop settings
    private final Map<EntityType, MobDropEntry> dropTable = new EnumMap<>(EntityType.class);
    private double globalDropMultiplier = 1.0;
    private boolean enabled = true;

    public MobDropListener(VortexEnchantments plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        File file = new File(plugin.getDataFolder(), "mob_drops.yml");
        if (!file.exists()) {
            plugin.saveResource("mob_drops.yml", false);
        }
        dropConfig = YamlConfiguration.loadConfiguration(file);

        enabled = dropConfig.getBoolean("enabled", true);
        globalDropMultiplier = dropConfig.getDouble("global-drop-multiplier", 1.0);
        dropTable.clear();

        ConfigurationSection mobsSection = dropConfig.getConfigurationSection("mobs");
        if (mobsSection == null) return;

        for (String key : mobsSection.getKeys(false)) {
            ConfigurationSection mobSection = mobsSection.getConfigurationSection(key);
            if (mobSection == null) continue;

            EntityType entityType;
            try {
                entityType = EntityType.valueOf(key.toUpperCase());
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Unknown mob type in mob_drops.yml: " + key);
                continue;
            }

            double dropChance = mobSection.getDouble("drop-chance", 0.01);
            Map<EnchantRarity, Integer> rarityWeights = new EnumMap<>(EnchantRarity.class);

            ConfigurationSection weightsSection = mobSection.getConfigurationSection("rarity-weights");
            if (weightsSection != null) {
                for (String rarityKey : weightsSection.getKeys(false)) {
                    try {
                        EnchantRarity rarity = EnchantRarity.valueOf(rarityKey.toUpperCase());
                        rarityWeights.put(rarity, weightsSection.getInt(rarityKey));
                    } catch (IllegalArgumentException ignored) {}
                }
            }

            // If no weights specified, use defaults
            if (rarityWeights.isEmpty()) {
                for (EnchantRarity r : EnchantRarity.values()) {
                    rarityWeights.put(r, r.getDefaultWeight());
                }
            }

            dropTable.put(entityType, new MobDropEntry(dropChance, rarityWeights));
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!enabled) return;
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer == null) return;

        MobDropEntry entry = dropTable.get(entity.getType());
        if (entry == null) {
            // Use default entry if configured
            entry = dropTable.get(null);
            if (entry == null) return;
        }

        double chance = entry.dropChance * globalDropMultiplier;
        if (ThreadLocalRandom.current().nextDouble() > chance) return;

        // Pick a rarity using weighted random
        EnchantRarity rarity = pickWeightedRarity(entry.rarityWeights);
        if (rarity == null) return;

        // Pick a random enchant of that rarity
        List<VortexEnchant> candidates = new ArrayList<>();
        for (VortexEnchant enchant : plugin.getEnchantManager().getAll()) {
            if (enchant.isEnabled() && enchant.getRarity() == rarity) {
                candidates.add(enchant);
            }
        }
        if (candidates.isEmpty()) return;

        VortexEnchant chosen = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
        int level = 1 + ThreadLocalRandom.current().nextInt(chosen.getMaxLevel());

        ItemStack book = plugin.getEnchantManager().createEnchantedBook(chosen, level);
        event.getDrops().add(book);

        killer.sendMessage("§5✦ §dA " + rarity.getColor() + rarity.getDisplayName()
            + " §denchanted book dropped: " + chosen.getLoreLine(level) + " §5✦");
    }

    private EnchantRarity pickWeightedRarity(Map<EnchantRarity, Integer> weights) {
        int totalWeight = 0;
        for (int w : weights.values()) totalWeight += w;
        if (totalWeight <= 0) return null;

        int roll = ThreadLocalRandom.current().nextInt(totalWeight);
        int cumulative = 0;
        for (Map.Entry<EnchantRarity, Integer> entry : weights.entrySet()) {
            cumulative += entry.getValue();
            if (roll < cumulative) return entry.getKey();
        }
        return EnchantRarity.COMMON;
    }

    public void reload() {
        loadConfig();
    }

    private record MobDropEntry(double dropChance, Map<EnchantRarity, Integer> rarityWeights) {}
}
