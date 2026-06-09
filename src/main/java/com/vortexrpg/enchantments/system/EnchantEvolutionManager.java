package com.vortexrpg.enchantments.system;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Enchant Evolution / Prestige System — unique to VortexEnchantments.
 *
 * When an enchant reaches max level, players can "evolve" it into a superior
 * version with a new name, higher rarity tier, and bonus effect.
 *
 * Features:
 *   - Evolution paths: each enchant can define an evolved form (different name + rarity)
 *   - Cost: requires souls + money (configurable) to evolve
 *   - Success chance: not guaranteed (configurable per rarity)
 *   - Evolved enchants keep original effects + gain a bonus modifier
 *   - Visual prestige indicator: star prefix on evolved enchants (★, ★★, ★★★)
 *   - Up to 3 evolution stages per enchant
 *   - Evolution history tracked on item via PDC
 *   - /ve evolve command — evolves held item's enchant
 */
@SuppressWarnings("deprecation")
public class EnchantEvolutionManager {

    private final VortexEnchantments plugin;
    private final Map<String, EvolutionPath> evolutionPaths = new LinkedHashMap<>();

    public EnchantEvolutionManager(VortexEnchantments plugin) {
        this.plugin = plugin;
        registerDefaultPaths();
    }

    // ─── Evolution Path ──────────────────────────────────────────────────────

    public record EvolutionPath(
        String sourceEnchantId,
        List<EvolutionStage> stages
    ) {}

    public record EvolutionStage(
        int stage,           // 1, 2, 3
        String evolvedName,  // New display name
        EnchantRarity evolvedRarity,
        int soulsCost,
        double moneyCost,
        int successChance,   // percentage 0-100
        String bonusDesc     // description of the bonus effect
    ) {}

    public record EvolutionResult(
        boolean success,
        boolean destroyed,       // on failure, enchant may be destroyed
        String message,
        EvolutionStage stage,
        int currentStage         // stage after attempt
    ) {}

    // ─── Evolution Logic ─────────────────────────────────────────────────────

    /**
     * Attempt to evolve an enchant on the player's held item.
     * Returns the result of the evolution attempt.
     */
    public EvolutionResult evolve(Player player, String enchantId) {
        ItemStack held = player.getInventory().getItemInMainHand();
        if (held.getType().isAir()) {
            return new EvolutionResult(false, false, "§cYou must hold an item!", null, 0);
        }

        // Check enchant exists on item
        Map<VortexEnchant, Integer> enchants = plugin.getEnchantManager().getEnchants(held);
        VortexEnchant target = null;
        int currentLevel = 0;
        for (var entry : enchants.entrySet()) {
            if (entry.getKey().getId().equalsIgnoreCase(enchantId)) {
                target = entry.getKey();
                currentLevel = entry.getValue();
                break;
            }
        }

        if (target == null) {
            return new EvolutionResult(false, false,
                "§cEnchant §f" + enchantId + "§c not found on this item!", null, 0);
        }

        // Must be at max level
        if (currentLevel < target.getMaxLevel()) {
            return new EvolutionResult(false, false,
                "§cEnchant must be at max level (§e" + target.getMaxLevel() + "§c) to evolve!", null, 0);
        }

        // Check evolution path exists
        EvolutionPath path = evolutionPaths.get(target.getId().toLowerCase());
        if (path == null) {
            return new EvolutionResult(false, false,
                "§cThis enchant cannot be evolved.", null, 0);
        }

        // Get current evolution stage
        int currentStage = getEvolutionStage(held, target.getId());
        if (currentStage >= path.stages().size()) {
            return new EvolutionResult(false, false,
                "§cThis enchant is already at max evolution (§e" + getStarPrefix(currentStage) + "§c)!", null, currentStage);
        }

        EvolutionStage nextStage = path.stages().get(currentStage);

        // Check costs
        if (plugin.getSoulsManager().getSouls(player) < nextStage.soulsCost()) {
            return new EvolutionResult(false, false,
                "§cNeed §d" + nextStage.soulsCost() + " Souls§c! (Have: §d"
                    + plugin.getSoulsManager().getSouls(player) + "§c)", null, currentStage);
        }

        if (nextStage.moneyCost() > 0 && plugin.getVaultHook().isEnabled()) {
            if (!plugin.getVaultHook().has(player, nextStage.moneyCost())) {
                return new EvolutionResult(false, false,
                    "§cNeed §a$" + plugin.getVaultHook().format(nextStage.moneyCost())
                        + "§c! (Have: §a$" + plugin.getVaultHook().format(plugin.getVaultHook().getBalance(player)) + "§c)",
                    null, currentStage);
            }
        }

        // Deduct costs
        plugin.getSoulsManager().removeSouls(player, nextStage.soulsCost());
        if (nextStage.moneyCost() > 0 && plugin.getVaultHook().isEnabled()) {
            plugin.getVaultHook().withdraw(player, nextStage.moneyCost());
        }

        // Roll success
        int roll = ThreadLocalRandom.current().nextInt(100);
        if (roll >= nextStage.successChance()) {
            // Failure — small chance to destroy the enchant entirely
            int destroyChance = plugin.getConfig().getInt("evolution.destroy-on-fail-chance", 10);
            if (ThreadLocalRandom.current().nextInt(100) < destroyChance) {
                plugin.getEnchantManager().removeEnchant(held, target);
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 0.8f, 0.5f);
                return new EvolutionResult(false, true,
                    "§c§l✦ EVOLUTION FAILED! §7The enchant was destroyed in the process!",
                    nextStage, currentStage);
            }

            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.7f, 0.5f);
            return new EvolutionResult(false, false,
                "§c✦ Evolution failed! §7The enchant survived, but the materials were consumed.",
                nextStage, currentStage);
        }

        // Success — apply evolution
        int newStage = currentStage + 1;
        setEvolutionStage(held, target.getId(), newStage);

        // Update lore to show evolved name + stars
        updateEvolvedLore(held, target, nextStage, newStage);

        // Effects
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 0.5f);
        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation().add(0, 1, 0), 50, 1, 1, 1, 0.2);
        player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0, 2, 0), 30, 0.5, 0.5, 0.5, 0.1);

        return new EvolutionResult(true, false,
            nextStage.evolvedRarity().getColor() + "§l✦ EVOLVED! §r"
                + nextStage.evolvedRarity().getColor() + getStarPrefix(newStage) + " "
                + nextStage.evolvedName()
                + " §7— " + nextStage.bonusDesc(),
            nextStage, newStage);
    }

    // ─── PDC Evolution Tracking ──────────────────────────────────────────────

    private int getEvolutionStage(ItemStack item, String enchantId) {
        var meta = item.getItemMeta();
        if (meta == null) return 0;
        var pdc = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "evo_" + enchantId.toLowerCase());
        if (pdc.has(key, PersistentDataType.INTEGER)) {
            return pdc.get(key, PersistentDataType.INTEGER);
        }
        return 0;
    }

    private void setEvolutionStage(ItemStack item, String enchantId, int stage) {
        var meta = item.getItemMeta();
        if (meta == null) return;
        NamespacedKey key = new NamespacedKey(plugin, "evo_" + enchantId.toLowerCase());
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, stage);
        item.setItemMeta(meta);
    }

    /**
     * Get the evolution stage for a specific enchant on an item.
     */
    public int getStage(ItemStack item, String enchantId) {
        return getEvolutionStage(item, enchantId);
    }

    // ─── Lore ────────────────────────────────────────────────────────────────

    private void updateEvolvedLore(ItemStack item, VortexEnchant enchant, EvolutionStage stage, int stageNum) {
        var meta = item.getItemMeta();
        if (meta == null) return;

        List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();

        // Remove old lore line for this enchant
        lore.removeIf(line -> line.contains(enchant.getDisplayName()) && !line.contains("★"));

        // Also remove previous evolution line
        lore.removeIf(line -> line.contains(enchant.getDisplayName()) && line.contains("★"));

        // Add new evolved line
        String stars = getStarPrefix(stageNum);
        String evolvedLine = stage.evolvedRarity().getColor() + stars + " "
            + stage.evolvedName() + " " + enchant.getLevelDisplay(enchant.getMaxLevel());
        lore.add(0, evolvedLine);

        // Add bonus description
        lore.add(1, "  §8§o" + stage.bonusDesc());

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public static String getStarPrefix(int stage) {
        return switch (stage) {
            case 1 -> "§e★";
            case 2 -> "§6★★";
            case 3 -> "§c★★★";
            default -> "";
        };
    }

    // ─── Info ────────────────────────────────────────────────────────────────

    /**
     * Send evolution info for an enchant to a player.
     */
    public void sendEvolutionInfo(Player player, String enchantId) {
        VortexEnchant enchant = plugin.getEnchantManager().getById(enchantId);
        if (enchant == null) {
            player.sendMessage("§cEnchant not found: " + enchantId);
            return;
        }

        EvolutionPath path = evolutionPaths.get(enchantId.toLowerCase());
        if (path == null) {
            player.sendMessage("§c" + enchant.getDisplayName() + " has no evolution path.");
            return;
        }

        player.sendMessage("§6§l━━━ Evolution Path: " + enchant.getRarity().getColor() + enchant.getDisplayName() + " §6§l━━━");
        player.sendMessage("§7Base: " + enchant.getRarity().getColor() + enchant.getDisplayName()
            + " §7(Max Lvl " + enchant.getMaxLevel() + ")");
        player.sendMessage("");

        for (EvolutionStage stage : path.stages()) {
            player.sendMessage(stage.evolvedRarity().getColor() + "§l" + getStarPrefix(stage.stage())
                + " Stage " + stage.stage() + ": " + stage.evolvedName());
            player.sendMessage("  §7Rarity: " + stage.evolvedRarity().getColor() + stage.evolvedRarity().getDisplayName());
            player.sendMessage("  §7Cost: §d" + stage.soulsCost() + " Souls"
                + (stage.moneyCost() > 0 ? " §7+ §a$" + (int) stage.moneyCost() : ""));
            player.sendMessage("  §7Success: §e" + stage.successChance() + "%");
            player.sendMessage("  §7Bonus: §f" + stage.bonusDesc());
            player.sendMessage("");
        }
        player.sendMessage("§6§l━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    /**
     * Get all enchant IDs that have evolution paths.
     */
    public Set<String> getEvolvableEnchants() {
        return Collections.unmodifiableSet(evolutionPaths.keySet());
    }

    // ─── Default Evolution Paths ─────────────────────────────────────────────

    private void registerDefaultPaths() {
        // Sword enchants
        registerPath("debt", List.of(
            new EvolutionStage(1, "Debt Collector", EnchantRarity.EPIC, 50, 5000, 80, "+15% damage steal"),
            new EvolutionStage(2, "Soul Tax", EnchantRarity.LEGENDARY, 150, 25000, 50, "+30% damage steal + souls"),
            new EvolutionStage(3, "Absolute Debt", EnchantRarity.MYTHIC, 500, 100000, 25, "+50% steal + AoE drain")
        ));

        registerPath("entropy", List.of(
            new EvolutionStage(1, "Chaos Entropy", EnchantRarity.LEGENDARY, 75, 10000, 70, "Wider AoE entropy"),
            new EvolutionStage(2, "Void Entropy", EnchantRarity.MYTHIC, 300, 50000, 35, "Entropy + wither effect")
        ));

        registerPath("siphon", List.of(
            new EvolutionStage(1, "Blood Siphon", EnchantRarity.EPIC, 40, 3000, 85, "+25% life steal"),
            new EvolutionStage(2, "Soul Siphon", EnchantRarity.LEGENDARY, 120, 20000, 55, "+50% steal + regen"),
            new EvolutionStage(3, "Absolute Drain", EnchantRarity.MYTHIC, 400, 80000, 30, "Full HP steal on crit")
        ));

        registerPath("thirst", List.of(
            new EvolutionStage(1, "Blood Thirst", EnchantRarity.RARE, 30, 2000, 90, "+10% damage per kill streak"),
            new EvolutionStage(2, "Unquenchable", EnchantRarity.EPIC, 80, 12000, 60, "+20% damage + speed boost")
        ));

        registerPath("fracture", List.of(
            new EvolutionStage(1, "Shatter Fracture", EnchantRarity.EPIC, 60, 8000, 75, "Armor break extends"),
            new EvolutionStage(2, "Obliterate", EnchantRarity.LEGENDARY, 200, 40000, 40, "Permanent armor reduction")
        ));

        registerPath("singularity", List.of(
            new EvolutionStage(1, "Black Hole", EnchantRarity.LEGENDARY, 100, 15000, 60, "Larger pull radius"),
            new EvolutionStage(2, "Event Horizon", EnchantRarity.MYTHIC, 400, 100000, 25, "Inescapable void zone")
        ));

        registerPath("gravity_well", List.of(
            new EvolutionStage(1, "Gravity Crush", EnchantRarity.EPIC, 50, 6000, 80, "+damage in gravity field"),
            new EvolutionStage(2, "Supermassive", EnchantRarity.LEGENDARY, 150, 30000, 45, "Airborne targets take 2x")
        ));

        registerPath("blood_price", List.of(
            new EvolutionStage(1, "Crimson Price", EnchantRarity.LEGENDARY, 80, 12000, 65, "Less self-damage cost"),
            new EvolutionStage(2, "Bloodlord", EnchantRarity.MYTHIC, 350, 75000, 30, "Blood magic heals instead")
        ));

        // Bow enchants
        registerPath("power_draw", List.of(
            new EvolutionStage(1, "Titan's Draw", EnchantRarity.EPIC, 60, 8000, 75, "+20% arrow damage"),
            new EvolutionStage(2, "Celestial Shot", EnchantRarity.LEGENDARY, 180, 35000, 45, "+40% + piercing arrows")
        ));

        // Armor enchants
        registerPath("fortify", List.of(
            new EvolutionStage(1, "Iron Fortress", EnchantRarity.EPIC, 50, 5000, 80, "+15% damage reduction"),
            new EvolutionStage(2, "Adamantine Wall", EnchantRarity.LEGENDARY, 150, 25000, 50, "+30% + thorns")
        ));

        registerPath("guardian_angel", List.of(
            new EvolutionStage(1, "Archangel", EnchantRarity.LEGENDARY, 100, 15000, 60, "Chance to negate lethal hit"),
            new EvolutionStage(2, "Divine Guardian", EnchantRarity.MYTHIC, 400, 80000, 25, "Auto-revive on death (2min CD)")
        ));

        // Pickaxe enchants
        registerPath("vein_mine", List.of(
            new EvolutionStage(1, "Ore Eater", EnchantRarity.RARE, 30, 2000, 90, "+4 blocks per vein"),
            new EvolutionStage(2, "Strip Miner", EnchantRarity.EPIC, 80, 10000, 65, "+8 blocks + XP bonus"),
            new EvolutionStage(3, "World Eater", EnchantRarity.LEGENDARY, 250, 50000, 35, "Entire vein + fortune")
        ));

        registerPath("auto_smelt", List.of(
            new EvolutionStage(1, "Forge Master", EnchantRarity.RARE, 25, 1500, 90, "Double smelt output"),
            new EvolutionStage(2, "Inferno Forge", EnchantRarity.EPIC, 70, 8000, 70, "Triple + fire particles")
        ));

        // Hoe enchants
        registerPath("replant", List.of(
            new EvolutionStage(1, "Green Fingers", EnchantRarity.UNCOMMON, 15, 500, 95, "3x3 replant area"),
            new EvolutionStage(2, "Nature's Will", EnchantRarity.RARE, 40, 3000, 80, "5x5 + instant growth")
        ));
    }

    private void registerPath(String enchantId, List<EvolutionStage> stages) {
        evolutionPaths.put(enchantId.toLowerCase(), new EvolutionPath(enchantId, stages));
    }
}
