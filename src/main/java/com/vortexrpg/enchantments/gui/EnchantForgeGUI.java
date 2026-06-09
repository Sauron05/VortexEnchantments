package com.vortexrpg.enchantments.gui;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Upgrade GUI — sacrifice materials + XP to upgrade an enchant on your held item.
 * Player opens with /ve forge while holding an enchanted item.
 */
@SuppressWarnings("deprecation")
public class EnchantForgeGUI implements Listener {

    private static final String GUI_TITLE = "§6§l✦ Enchant Forge ✦";
    private static final int SIZE = 45; // 5 rows

    private final VortexEnchantments plugin;
    private final Map<UUID, ForgeState> activeStates = new ConcurrentHashMap<>();

    public EnchantForgeGUI(VortexEnchantments plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        ItemStack held = player.getInventory().getItemInMainHand();
        if (held.getType() == Material.AIR) {
            player.sendMessage("§cYou must be holding an enchanted item!");
            return;
        }

        Map<VortexEnchant, Integer> enchants = plugin.getEnchantManager().getEnchants(held);
        if (enchants.isEmpty()) {
            player.sendMessage("§cThat item has no VortexEnchantments to upgrade.");
            return;
        }

        // Only show enchants that aren't already max level
        List<Map.Entry<VortexEnchant, Integer>> upgradeable = new ArrayList<>();
        for (Map.Entry<VortexEnchant, Integer> entry : enchants.entrySet()) {
            if (entry.getValue() < entry.getKey().getMaxLevel()) {
                upgradeable.add(entry);
            }
        }

        if (upgradeable.isEmpty()) {
            player.sendMessage("§eAll enchantments on this item are already at max level!");
            return;
        }

        Inventory inv = Bukkit.createInventory(null, SIZE, GUI_TITLE);

        // Border
        ItemStack border = makeItem(Material.BLACK_STAINED_GLASS_PANE, "§8");
        for (int i = 0; i < SIZE; i++) inv.setItem(i, border);

        // Header
        inv.setItem(4, makeItem(Material.ANVIL, "§6§lEnchant Forge",
            "§7Select an enchantment to upgrade.",
            "§7Cost: Materials + XP Levels"));

        // Show held item preview
        inv.setItem(22, held.clone());

        // Show upgradeable enchants in row 1 (slots 9-17)
        for (int i = 0; i < upgradeable.size() && i < 9; i++) {
            VortexEnchant enchant = upgradeable.get(i).getKey();
            int currentLevel = upgradeable.get(i).getValue();
            int nextLevel = currentLevel + 1;
            int xpCost = getXPCost(enchant, nextLevel);
            Material matCost = getMaterialType(enchant.getRarity());
            int matAmount = getMaterialAmount(enchant.getRarity(), nextLevel);

            Material displayMat = switch (enchant.getRarity()) {
                case COMMON -> Material.WHITE_STAINED_GLASS_PANE;
                case UNCOMMON -> Material.LIME_STAINED_GLASS_PANE;
                case RARE -> Material.BLUE_STAINED_GLASS_PANE;
                case EPIC -> Material.PURPLE_STAINED_GLASS_PANE;
                case LEGENDARY -> Material.ORANGE_STAINED_GLASS_PANE;
                case MYTHIC -> Material.RED_STAINED_GLASS_PANE;
            };

            inv.setItem(9 + i, makeItem(displayMat, enchant.getLoreLine(currentLevel) + " §7→ " + enchant.getLoreLine(nextLevel),
                enchant.getRarity().getColor() + enchant.getRarity().getDisplayName(),
                "",
                "§7Cost:",
                "§e  " + xpCost + " XP Levels",
                "§e  " + matAmount + "x " + formatMaterial(matCost),
                "",
                "§aClick to upgrade!"));
        }

        // Cost legend in row 3 (slots 27-35)
        inv.setItem(27, makeItem(Material.EXPERIENCE_BOTTLE, "§eXP Cost",
            "§7XP scales with rarity + level."));
        inv.setItem(28, makeItem(getMaterialType(EnchantRarity.COMMON), "§fCommon: §7Iron Ingot"));
        inv.setItem(29, makeItem(getMaterialType(EnchantRarity.UNCOMMON), "§aUncommon: §7Gold Ingot"));
        inv.setItem(30, makeItem(getMaterialType(EnchantRarity.RARE), "§9Rare: §7Diamond"));
        inv.setItem(31, makeItem(getMaterialType(EnchantRarity.EPIC), "§5Epic: §7Emerald"));
        inv.setItem(32, makeItem(getMaterialType(EnchantRarity.LEGENDARY), "§6Legendary: §7Netherite Ingot"));
        inv.setItem(33, makeItem(getMaterialType(EnchantRarity.MYTHIC), "§cMythic: §7Nether Star"));

        player.openInventory(inv);
        activeStates.put(player.getUniqueId(), new ForgeState(upgradeable));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;
        event.setCancelled(true);

        ForgeState state = activeStates.get(player.getUniqueId());
        if (state == null) return;

        int slot = event.getRawSlot();
        if (slot < 9 || slot > 17) return;

        int index = slot - 9;
        if (index >= state.upgradeable.size()) return;

        VortexEnchant enchant = state.upgradeable.get(index).getKey();
        int currentLevel = state.upgradeable.get(index).getValue();
        int nextLevel = currentLevel + 1;
        int xpCost = getXPCost(enchant, nextLevel);
        Material matCost = getMaterialType(enchant.getRarity());
        int matAmount = getMaterialAmount(enchant.getRarity(), nextLevel);

        // Check XP
        if (player.getLevel() < xpCost) {
            player.sendMessage("§cYou need " + xpCost + " XP levels! You have " + player.getLevel() + ".");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        // Check materials
        if (!hasItems(player, matCost, matAmount)) {
            player.sendMessage("§cYou need " + matAmount + "x " + formatMaterial(matCost) + "!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        // Deduct costs
        player.setLevel(player.getLevel() - xpCost);
        removeItems(player, matCost, matAmount);

        // Apply upgrade
        ItemStack held = player.getInventory().getItemInMainHand();
        plugin.getEnchantManager().applyEnchant(held, enchant, nextLevel);

        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.2f);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.5f);
        player.sendMessage("§a✦ Upgraded " + enchant.getLoreLine(currentLevel) + " §a→ " + enchant.getLoreLine(nextLevel) + " §a✦");

        SchedulerUtil.runEntityTask(plugin, player, () -> player.closeInventory());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals(GUI_TITLE)) {
            activeStates.remove(event.getPlayer().getUniqueId());
        }
    }

    // ─── Cost Calculations ──────────────────────────────────────────────────

    private int getXPCost(VortexEnchant enchant, int targetLevel) {
        int base = (enchant.getRarity().ordinal() + 1) * 3; // 3,6,9,12,15,18
        return base + (targetLevel * 2);
    }

    private Material getMaterialType(EnchantRarity rarity) {
        return switch (rarity) {
            case COMMON -> Material.IRON_INGOT;
            case UNCOMMON -> Material.GOLD_INGOT;
            case RARE -> Material.DIAMOND;
            case EPIC -> Material.EMERALD;
            case LEGENDARY -> Material.NETHERITE_INGOT;
            case MYTHIC -> Material.NETHER_STAR;
        };
    }

    private int getMaterialAmount(EnchantRarity rarity, int targetLevel) {
        int base = switch (rarity) {
            case COMMON -> 2;
            case UNCOMMON -> 3;
            case RARE -> 2;
            case EPIC -> 2;
            case LEGENDARY -> 1;
            case MYTHIC -> 1;
        };
        return base + (targetLevel - 1);
    }

    private boolean hasItems(Player player, Material material, int amount) {
        int count = 0;
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack != null && stack.getType() == material) {
                count += stack.getAmount();
                if (count >= amount) return true;
            }
        }
        return false;
    }

    private void removeItems(Player player, Material material, int amount) {
        int remaining = amount;
        for (int i = 0; i < player.getInventory().getSize() && remaining > 0; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack != null && stack.getType() == material) {
                int take = Math.min(remaining, stack.getAmount());
                stack.setAmount(stack.getAmount() - take);
                remaining -= take;
            }
        }
    }

    private String formatMaterial(Material mat) {
        return switch (mat) {
            case IRON_INGOT -> "Iron Ingot";
            case GOLD_INGOT -> "Gold Ingot";
            case DIAMOND -> "Diamond";
            case EMERALD -> "Emerald";
            case NETHERITE_INGOT -> "Netherite Ingot";
            case NETHER_STAR -> "Nether Star";
            default -> mat.name();
        };
    }

    private ItemStack makeItem(Material mat, String name, String... loreLines) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(name);
        if (loreLines.length > 0) meta.setLore(Arrays.asList(loreLines));
        item.setItemMeta(meta);
        return item;
    }

    private record ForgeState(List<Map.Entry<VortexEnchant, Integer>> upgradeable) {}
}
