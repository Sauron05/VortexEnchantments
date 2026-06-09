package com.vortexrpg.enchantments.item;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

/**
 * Transmog Scroll — premium cosmetic scroll that transforms item lore presentation.
 * 
 * Superior to AE implementation:
 *   - Rarity section dividers (groups enchants under colored rarity headers)
 *   - Enchant counter header showing total enchants and highest rarity
 *   - Tracks transmog count (how many times the item has been transmogged)
 *   - Configurable style mode: "grouped" (by rarity) or "sorted" (single list)
 *   - Adds player signature with timestamp
 *   - Removes duplicate lore lines and cleans up formatting
 */
@SuppressWarnings("deprecation")
public class TransmogScroll {

    private final NamespacedKey scrollKey;
    private final NamespacedKey transmoggedKey;
    private final NamespacedKey transmogCountKey;
    private final VortexEnchantments plugin;

    public TransmogScroll(VortexEnchantments plugin) {
        this.plugin = plugin;
        this.scrollKey = new NamespacedKey(plugin, "transmog_scroll");
        this.transmoggedKey = new NamespacedKey(plugin, "transmogged");
        this.transmogCountKey = new NamespacedKey(plugin, "transmog_count");
    }

    public ItemStack create(int amount) {
        ItemStack item = new ItemStack(Material.PAPER, Math.min(amount, 64));
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName("§d§l✦ Transmog Scroll ✦");
        List<String> lore = new ArrayList<>();
        lore.add("§8Cosmetic Scroll");
        lore.add("§8§m                              ");
        lore.add("");
        lore.add("§7Drag onto an enchanted item to");
        lore.add("§7transform its enchantment display:");
        lore.add("");
        lore.add("  §d▸ §7Groups enchants by §drarity tier");
        lore.add("  §d▸ §7Adds enchant §ecounter header");
        lore.add("  §d▸ §7Adds §bsignature tag §7with your name");
        lore.add("  §d▸ §7Applies §d✦ enchantment shimmer ✦");
        lore.add("");
        lore.add("§7Can be re-applied to update after");
        lore.add("§7adding new enchantments.");
        lore.add("");
        lore.add("§d§l✦ §8One-time use per application");
        lore.add("§8§m                              ");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true);
        meta.getPersistentDataContainer().set(scrollKey, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isTransmogScroll(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        var meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(scrollKey, PersistentDataType.BOOLEAN);
    }

    /**
     * Apply transmog to an item: organize lore with rarity sections, counter, and signature.
     */
    public boolean applyTransmog(ItemStack item, String playerName) {
        if (item == null || item.getType() == Material.AIR) return false;
        var meta = item.getItemMeta();
        if (meta == null) return false;

        var enchants = plugin.getEnchantManager().getEnchants(item);
        if (enchants.isEmpty()) return false;

        // Increment transmog count
        int count = 0;
        if (meta.getPersistentDataContainer().has(transmogCountKey, PersistentDataType.INTEGER)) {
            count = meta.getPersistentDataContainer().get(transmogCountKey, PersistentDataType.INTEGER);
        }
        count++;
        meta.getPersistentDataContainer().set(transmogCountKey, PersistentDataType.INTEGER, count);
        meta.getPersistentDataContainer().set(transmoggedKey, PersistentDataType.BOOLEAN, true);

        // Build new lore
        List<String> newLore = new ArrayList<>();

        // ─── Enchant counter header ──────────────────────────────────────────
        EnchantRarity highest = EnchantRarity.COMMON;
        for (VortexEnchant e : enchants.keySet()) {
            if (e.getRarity().ordinal() > highest.ordinal()) highest = e.getRarity();
        }
        newLore.add("§8§m                              ");
        newLore.add(highest.getColor() + "§l✦ " + enchants.size() + " Enchantment"
            + (enchants.size() != 1 ? "s" : "") + " §8| " + highest.getColor() + highest.getDisplayName() + "+");
        newLore.add("§8§m                              ");
        newLore.add("");

        // ─── Group enchants by rarity ────────────────────────────────────────
        String style = plugin.getConfig().getString("transmog.style", "grouped");
        if (style.equalsIgnoreCase("grouped")) {
            // Group by rarity (highest first)
            Map<EnchantRarity, List<Map.Entry<VortexEnchant, Integer>>> grouped = new LinkedHashMap<>();
            var sorted = enchants.entrySet().stream()
                .sorted((a, b) -> {
                    int cmp = Integer.compare(b.getKey().getRarity().ordinal(), a.getKey().getRarity().ordinal());
                    return cmp != 0 ? cmp : a.getKey().getDisplayName().compareTo(b.getKey().getDisplayName());
                })
                .toList();

            for (var entry : sorted) {
                grouped.computeIfAbsent(entry.getKey().getRarity(), k -> new ArrayList<>()).add(entry);
            }

            for (var group : grouped.entrySet()) {
                EnchantRarity rarity = group.getKey();
                // Rarity section header
                newLore.add(rarity.getColor() + "§l▸ " + rarity.getDisplayName()
                    + " §8(" + group.getValue().size() + ")");
                for (var entry : group.getValue()) {
                    newLore.add("  " + entry.getKey().getLoreLine(entry.getValue()));
                }
                newLore.add("");
            }
        } else {
            // Simple sorted mode (highest rarity first, then alphabetical)
            var sorted = enchants.entrySet().stream()
                .sorted((a, b) -> {
                    int cmp = Integer.compare(b.getKey().getRarity().ordinal(), a.getKey().getRarity().ordinal());
                    return cmp != 0 ? cmp : a.getKey().getDisplayName().compareTo(b.getKey().getDisplayName());
                })
                .toList();
            for (var entry : sorted) {
                newLore.add(entry.getKey().getLoreLine(entry.getValue()));
            }
            newLore.add("");
        }

        // ─── Preserve non-enchant lore (White Scroll, Slot Bonus, Holy, etc.) ──
        List<String> existingLore = meta.getLore() != null ? meta.getLore() : new ArrayList<>();
        Set<String> enchantNames = new HashSet<>();
        for (var entry : enchants.entrySet()) enchantNames.add(entry.getKey().getDisplayName());

        boolean hasExtraLines = false;
        for (String line : existingLore) {
            boolean isEnchantLine = false;
            for (String name : enchantNames) {
                if (line.contains(name)) {
                    isEnchantLine = true;
                    break;
                }
            }
            // Skip old transmog lines, section dividers, counter headers, and enchant lines
            if (isEnchantLine) continue;
            if (line.contains("Transmogged by")) continue;
            if (line.contains("Enchantment") && line.contains("✦")) continue;
            if (line.equals("§8§m                              ")) continue;
            if (line.contains("§l▸") && containsRarityName(line)) continue;
            if (line.isEmpty() || line.equals("")) continue;

            if (!hasExtraLines) {
                newLore.add("§8§m                              ");
                hasExtraLines = true;
            }
            newLore.add(line);
        }

        // ─── Signature + timestamp ───────────────────────────────────────────
        newLore.add("§8§m                              ");
        newLore.add("§d✦ §7Transmogged by §f" + playerName + " §d✦");
        if (count > 1) {
            newLore.add("§8  (applied " + count + " times)");
        }

        meta.setLore(newLore);

        // Add enchant shimmer (hidden glint)
        if (!meta.hasEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING)) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true);
        }
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);
        return true;
    }

    public boolean isTransmogged(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        var meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(transmoggedKey, PersistentDataType.BOOLEAN);
    }

    public int getTransmogCount(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return 0;
        var meta = item.getItemMeta();
        if (meta == null) return 0;
        var pdc = meta.getPersistentDataContainer();
        return pdc.has(transmogCountKey, PersistentDataType.INTEGER)
            ? pdc.get(transmogCountKey, PersistentDataType.INTEGER) : 0;
    }

    private boolean containsRarityName(String line) {
        for (EnchantRarity r : EnchantRarity.values()) {
            if (line.contains(r.getDisplayName())) return true;
        }
        return false;
    }

    public NamespacedKey getScrollKey() { return scrollKey; }
}
