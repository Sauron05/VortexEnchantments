package com.vortexrpg.enchantments.listener;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.Material;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Intercepts villager librarian trade acquisition to inject VortexEnchantment books.
 */
public class VillagerListener implements Listener {

    private final VortexEnchantments plugin;

    public VillagerListener(VortexEnchantments plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        if (!plugin.getConfigManager().isVillagerEnabled()) return;
        if (!(event.getEntity() instanceof Villager villager)) return;
        if (villager.getProfession() != Villager.Profession.LIBRARIAN) return;

        // Only replace existing book trades
        MerchantRecipe original = event.getRecipe();
        ItemStack result = original.getResult();
        if (result.getType() != Material.ENCHANTED_BOOK) return;

        double chance = plugin.getConfig().getDouble("villager.replace-chance", 30.0);
        if (ThreadLocalRandom.current().nextDouble(100) > chance) return;

        // Select a tier based on villager level
        int level = villager.getVillagerLevel(); // 1-5
        EnchantRarity tier = selectTier(level);

        // Get eligible enchants
        List<VortexEnchant> eligible = plugin.getEnchantManager().getAll().stream()
            .filter(VortexEnchant::isEnabled)
            .filter(e -> e.getRarity() == tier)
            .toList();

        if (eligible.isEmpty()) return;

        VortexEnchant chosen = eligible.get(ThreadLocalRandom.current().nextInt(eligible.size()));
        int enchantLevel = Math.max(1, ThreadLocalRandom.current().nextInt(1, chosen.getMaxLevel() + 1));
        ItemStack book = plugin.getEnchantManager().createEnchantedBook(chosen, enchantLevel);

        // Create new trade recipe
        int emeraldCost = getEmeraldCost(tier, enchantLevel);
        MerchantRecipe newRecipe = new MerchantRecipe(book, original.getMaxUses());
        newRecipe.addIngredient(new ItemStack(Material.EMERALD, Math.min(64, emeraldCost)));
        if (tier.ordinal() >= EnchantRarity.EPIC.ordinal()) {
            newRecipe.addIngredient(new ItemStack(Material.BOOK));
        }
        newRecipe.setExperienceReward(true);

        event.setRecipe(newRecipe);
    }

    private EnchantRarity selectTier(int villagerLevel) {
        return switch (villagerLevel) {
            case 1 -> EnchantRarity.COMMON;
            case 2 -> ThreadLocalRandom.current().nextBoolean() ? EnchantRarity.COMMON : EnchantRarity.UNCOMMON;
            case 3 -> EnchantRarity.UNCOMMON;
            case 4 -> {
                double r = ThreadLocalRandom.current().nextDouble();
                yield r < 0.3 ? EnchantRarity.RARE : EnchantRarity.UNCOMMON;
            }
            case 5 -> {
                double r = ThreadLocalRandom.current().nextDouble();
                if (r < 0.05) yield EnchantRarity.LEGENDARY;
                if (r < 0.2) yield EnchantRarity.EPIC;
                yield EnchantRarity.RARE;
            }
            default -> EnchantRarity.COMMON;
        };
    }

    private int getEmeraldCost(EnchantRarity tier, int level) {
        int base = switch (tier) {
            case COMMON -> 5;
            case UNCOMMON -> 10;
            case RARE -> 20;
            case EPIC -> 35;
            case LEGENDARY -> 50;
            case MYTHIC -> 64;
        };
        return Math.min(64, base + (level * 3));
    }
}
