package com.vortexrpg.enchantments.system;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Armor Set Bonus System — when a player wears a full set of armor (all 4 pieces)
 * with specific enchants, they receive bonus effects.
 */
public class ArmorSetManager implements Listener {

    private final VortexEnchantments plugin;
    private final List<ArmorSet> registeredSets = new ArrayList<>();
    private final Map<UUID, String> activeSetBonuses = new ConcurrentHashMap<>();

    public ArmorSetManager(VortexEnchantments plugin) {
        this.plugin = plugin;
        registerDefaultSets();
    }

    private void registerDefaultSets() {
        // Guardian Set: All 4 armor pieces with any "guard/shield/fortify" type enchants
        registeredSets.add(new ArmorSet("guardian", "§9§lGuardian Set",
            List.of(
                new PotionEffect(PotionEffectType.RESISTANCE, 100, 0, true, false),
                new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100, 0, true, false)
            ),
            "§7Full Set Bonus: §9Resistance I + Fire Resistance"));

        // Berserker Set: All 4 armor pieces with any "damage/strike/rage" type enchants
        registeredSets.add(new ArmorSet("berserker", "§c§lBerserker Set",
            List.of(
                new PotionEffect(PotionEffectType.STRENGTH, 100, 0, true, false),
                new PotionEffect(PotionEffectType.SPEED, 100, 0, true, false)
            ),
            "§7Full Set Bonus: §cStrength I + Speed I"));

        // Phantom Set: All 4 armor pieces with stealth-related enchants
        registeredSets.add(new ArmorSet("phantom", "§7§lPhantom Set",
            List.of(
                new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0, true, false),
                new PotionEffect(PotionEffectType.SPEED, 100, 1, true, false)
            ),
            "§7Full Set Bonus: §7Invisibility + Speed II"));

        // Vortex Set: Full netherite with at least 2 mythic enchants on each piece
        registeredSets.add(new ArmorSet("vortex", "§6§lVortex Set",
            List.of(
                new PotionEffect(PotionEffectType.RESISTANCE, 100, 1, true, false),
                new PotionEffect(PotionEffectType.REGENERATION, 100, 0, true, false),
                new PotionEffect(PotionEffectType.STRENGTH, 100, 0, true, false)
            ),
            "§7Full Set Bonus: §6Resistance II + Regen I + Strength I"));
    }

    /** Called periodically from the tick scheduler to check and apply set bonuses. */
    public void tickPlayer(Player player) {
        if (!plugin.getConfig().getBoolean("armor-sets.enabled", true)) return;

        EntityEquipment eq = player.getEquipment();
        if (eq == null) return;

        ItemStack helmet = eq.getHelmet();
        ItemStack chest = eq.getChestplate();
        ItemStack legs = eq.getLeggings();
        ItemStack boots = eq.getBoots();

        // Must have all 4 pieces
        if (isEmpty(helmet) || isEmpty(chest) || isEmpty(legs) || isEmpty(boots)) {
            removeActiveSet(player);
            return;
        }

        // Count total vortex enchants across all armor pieces
        var allEnchants = new ArrayList<Map.Entry<VortexEnchant, Integer>>();
        allEnchants.addAll(plugin.getEnchantManager().getEnchants(helmet).entrySet());
        allEnchants.addAll(plugin.getEnchantManager().getEnchants(chest).entrySet());
        allEnchants.addAll(plugin.getEnchantManager().getEnchants(legs).entrySet());
        allEnchants.addAll(plugin.getEnchantManager().getEnchants(boots).entrySet());

        int totalEnchants = allEnchants.size();
        long mythicCount = allEnchants.stream()
            .filter(e -> e.getKey().getRarity() == com.vortexrpg.enchantments.enchant.EnchantRarity.MYTHIC)
            .count();
        long legendaryCount = allEnchants.stream()
            .filter(e -> e.getKey().getRarity() == com.vortexrpg.enchantments.enchant.EnchantRarity.LEGENDARY
                || e.getKey().getRarity() == com.vortexrpg.enchantments.enchant.EnchantRarity.MYTHIC)
            .count();

        boolean isNetherite = isNetherite(helmet) && isNetherite(chest) && isNetherite(legs) && isNetherite(boots);

        // Check sets in priority order (highest tier first)
        String activeSet = null;

        // Vortex Set: Full netherite with 8+ mythic enchants
        if (isNetherite && mythicCount >= 8) {
            activeSet = "vortex";
        }
        // Berserker Set: 8+ total enchants
        else if (totalEnchants >= 8 && legendaryCount >= 2) {
            activeSet = "berserker";
        }
        // Phantom Set: 6+ total enchants
        else if (totalEnchants >= 6) {
            activeSet = "phantom";
        }
        // Guardian Set: 4+ total enchants
        else if (totalEnchants >= 4) {
            activeSet = "guardian";
        }

        if (activeSet == null) {
            removeActiveSet(player);
            return;
        }

        // Apply set effects
        String previousSet = activeSetBonuses.get(player.getUniqueId());
        activeSetBonuses.put(player.getUniqueId(), activeSet);

        ArmorSet set = getSet(activeSet);
        if (set != null) {
            for (PotionEffect effect : set.effects()) {
                player.addPotionEffect(effect);
            }
            // Notify on set activation
            if (!activeSet.equals(previousSet)) {
                player.sendMessage("§6§l✦ Set Bonus Activated: " + set.displayName());
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_NETHERITE, 0.5f, 1.5f);
            }
        }
    }

    private void removeActiveSet(Player player) {
        String previous = activeSetBonuses.remove(player.getUniqueId());
        if (previous != null) {
            player.sendMessage("§7§l✦ Set Bonus Deactivated");
        }
    }

    private ArmorSet getSet(String id) {
        for (ArmorSet set : registeredSets) {
            if (set.id().equals(id)) return set;
        }
        return null;
    }

    private boolean isEmpty(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    private boolean isNetherite(ItemStack item) {
        return item != null && item.getType().name().startsWith("NETHERITE_");
    }

    public List<ArmorSet> getRegisteredSets() { return Collections.unmodifiableList(registeredSets); }

    public record ArmorSet(String id, String displayName, List<PotionEffect> effects, String description) {}
}
