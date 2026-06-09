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
 * Tinkerer — recycle enchanted items or books for XP + Dust rewards.
 * Player places items in input slots, clicks confirm to salvage.
 */
@SuppressWarnings("deprecation")
public class TinkererGUI implements Listener {

    private static final String GUI_TITLE = "§6§l✦ Tinkerer ✦";
    private static final int SIZE = 54; // 6 rows

    // Input slots: row 1-2 (slots 10-16, 19-25) = 14 slots
    private static final int[] INPUT_SLOTS = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};
    // Confirm button
    private static final int CONFIRM_SLOT = 49;
    // Preview reward slots: row 3-4 (slots 28-34, 37-43) — reserved for future use

    private final VortexEnchantments plugin;
    private final Map<UUID, List<ItemStack>> pendingItems = new ConcurrentHashMap<>();

    public TinkererGUI(VortexEnchantments plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, SIZE, GUI_TITLE);

        // Border
        ItemStack border = makeItem(Material.BLACK_STAINED_GLASS_PANE, "§8");
        for (int i = 0; i < SIZE; i++) inv.setItem(i, border);

        // Open input slots
        for (int slot : INPUT_SLOTS) inv.setItem(slot, null);

        // Header
        inv.setItem(4, makeItem(Material.CRAFTING_TABLE, "§6§lTinkerer",
            "§7Place enchanted items or books",
            "§7in the slots above to recycle.",
            "",
            "§7You'll receive §eXP §7+ §6Dust§7!"));

        // Confirm button
        inv.setItem(CONFIRM_SLOT, makeItem(Material.LIME_STAINED_GLASS_PANE, "§a§lConfirm Salvage",
            "§7Click to salvage all items above.",
            "§cThis cannot be undone!"));

        pendingItems.remove(player.getUniqueId());
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;

        int slot = event.getRawSlot();

        // Allow interaction with input slots (player can place/remove items)
        for (int inputSlot : INPUT_SLOTS) {
            if (slot == inputSlot) return; // Don't cancel — let them place items
        }

        // Allow interaction in player inventory (bottom half)
        if (slot >= SIZE) return;

        event.setCancelled(true);

        // Confirm button click
        if (slot == CONFIRM_SLOT) {
            SchedulerUtil.runEntityTask(plugin, player, () -> processSalvage(player));
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;
        Player player = (Player) event.getPlayer();

        // Return any items left in input slots
        Inventory inv = event.getInventory();
        for (int slot : INPUT_SLOTS) {
            ItemStack item = inv.getItem(slot);
            if (item != null && item.getType() != Material.AIR) {
                HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(item);
                overflow.values().forEach(i -> player.getWorld().dropItemNaturally(player.getLocation(), i));
            }
        }
        pendingItems.remove(player.getUniqueId());
    }

    private void processSalvage(Player player) {
        Inventory inv = player.getOpenInventory().getTopInventory();
        if (inv == null) return;

        int totalXP = 0;
        Map<EnchantRarity, Integer> dustRewards = new EnumMap<>(EnchantRarity.class);
        boolean anyItem = false;

        for (int slot : INPUT_SLOTS) {
            ItemStack item = inv.getItem(slot);
            if (item == null || item.getType() == Material.AIR) continue;

            Map<VortexEnchant, Integer> enchants = plugin.getEnchantManager().getEnchants(item);
            if (enchants.isEmpty()) continue;

            anyItem = true;

            for (Map.Entry<VortexEnchant, Integer> entry : enchants.entrySet()) {
                VortexEnchant enchant = entry.getKey();
                int level = entry.getValue();
                EnchantRarity rarity = enchant.getRarity();

                // XP = (rarity_ordinal + 1) * level * 5
                totalXP += (rarity.ordinal() + 1) * level * 5;

                // Dust: 1 per enchant, tier matches rarity
                dustRewards.merge(rarity, 1, (a, b) -> a + b);
            }

            // Consume the item
            inv.setItem(slot, null);
        }

        if (!anyItem) {
            player.sendMessage("§cPlace enchanted items to salvage!");
            return;
        }

        // Give XP
        player.giveExp(totalXP);

        // Give dust
        for (Map.Entry<EnchantRarity, Integer> entry : dustRewards.entrySet()) {
            ItemStack dust = plugin.getDustItem().create(entry.getKey(), entry.getValue());
            HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(dust);
            overflow.values().forEach(i -> player.getWorld().dropItemNaturally(player.getLocation(), i));
        }

        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1.2f);
        player.sendMessage("§6§l✦ Tinkerer §8» §7Salvaged! Received §e" + totalXP + " XP§7 + §6"
            + dustRewards.values().stream().mapToInt(i -> i).sum() + " Dust§7.");

        // Close
        SchedulerUtil.runEntityTask(plugin, player, () -> player.closeInventory());
    }

    private ItemStack makeItem(Material mat, String name, String... loreLines) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(name);
        List<String> lore = new ArrayList<>();
        for (String line : loreLines) lore.add(line);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
