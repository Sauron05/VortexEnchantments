package com.vortexrpg.enchantments.gui;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.item.DustItem;
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
 * Alchemist — combine two identical enchant books to get a higher level,
 * or combine two dust items to get a higher tier.
 * Input: 2 slots, Output: 1 slot.
 */
@SuppressWarnings("deprecation")
public class AlchemistGUI implements Listener {

    private static final String GUI_TITLE = "§5§l✦ Alchemist ✦";
    private static final int SIZE = 27; // 3 rows

    private static final int INPUT_1 = 10;
    private static final int INPUT_2 = 12;
    private static final int OUTPUT_SLOT = 16;
    private static final int COMBINE_SLOT = 14;

    private final VortexEnchantments plugin;
    private final Set<UUID> activePlayers = ConcurrentHashMap.newKeySet();

    public AlchemistGUI(VortexEnchantments plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, SIZE, GUI_TITLE);

        // Border
        ItemStack border = makeItem(Material.BLACK_STAINED_GLASS_PANE, "§8");
        for (int i = 0; i < SIZE; i++) inv.setItem(i, border);

        // Open slots
        inv.setItem(INPUT_1, null);
        inv.setItem(INPUT_2, null);

        // Info
        inv.setItem(4, makeItem(Material.BREWING_STAND, "§5§lAlchemist",
            "§7Combine two identical enchant",
            "§7books to get a higher level.",
            "",
            "§7Or combine two dust items",
            "§7of the same tier for a upgrade."));

        // Combine button
        inv.setItem(COMBINE_SLOT, makeItem(Material.ANVIL, "§a§lCombine",
            "§7Place identical items in the",
            "§7input slots, then click here."));

        // Output preview (locked)
        inv.setItem(OUTPUT_SLOT, makeItem(Material.GRAY_STAINED_GLASS_PANE, "§7Result", "§8Output will appear here"));

        player.openInventory(inv);
        activePlayers.add(player.getUniqueId());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;

        int slot = event.getRawSlot();

        // Allow input slots
        if (slot == INPUT_1 || slot == INPUT_2) return;

        // Allow player inventory
        if (slot >= SIZE) return;

        event.setCancelled(true);

        // Combine button
        if (slot == COMBINE_SLOT) {
            SchedulerUtil.runEntityTask(plugin, player, () -> processCombine(player));
            return;
        }

        // Output slot — take result
        if (slot == OUTPUT_SLOT) {
            Inventory inv = event.getView().getTopInventory();
            ItemStack output = inv.getItem(OUTPUT_SLOT);
            if (output != null && output.getType() != Material.GRAY_STAINED_GLASS_PANE
                && output.getType() != Material.AIR) {
                event.setCancelled(false); // Let them take it
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;
        Player player = (Player) event.getPlayer();
        activePlayers.remove(player.getUniqueId());

        Inventory inv = event.getInventory();
        // Return items in input slots
        for (int slot : new int[]{INPUT_1, INPUT_2}) {
            ItemStack item = inv.getItem(slot);
            if (item != null && item.getType() != Material.AIR) {
                HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(item);
                overflow.values().forEach(i -> player.getWorld().dropItemNaturally(player.getLocation(), i));
            }
        }
        // Return output if not taken
        ItemStack output = inv.getItem(OUTPUT_SLOT);
        if (output != null && output.getType() != Material.GRAY_STAINED_GLASS_PANE
            && output.getType() != Material.AIR) {
            HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(output);
            overflow.values().forEach(i -> player.getWorld().dropItemNaturally(player.getLocation(), i));
        }
    }

    private void processCombine(Player player) {
        Inventory inv = player.getOpenInventory().getTopInventory();
        ItemStack item1 = inv.getItem(INPUT_1);
        ItemStack item2 = inv.getItem(INPUT_2);

        if (item1 == null || item2 == null || item1.getType() == Material.AIR || item2.getType() == Material.AIR) {
            player.sendMessage("§cPlace two items in the input slots!");
            return;
        }

        DustItem dustItem = plugin.getDustItem();

        // Dust combining
        if (dustItem.isDust(item1) && dustItem.isDust(item2)) {
            EnchantRarity tier1 = dustItem.getTier(item1);
            EnchantRarity tier2 = dustItem.getTier(item2);
            if (tier1 != tier2) {
                player.sendMessage("§cBoth dust items must be the same tier!");
                return;
            }
            int nextOrdinal = tier1.ordinal() + 1;
            if (nextOrdinal >= EnchantRarity.values().length) {
                player.sendMessage("§cDust is already at the highest tier!");
                return;
            }
            EnchantRarity nextTier = EnchantRarity.values()[nextOrdinal];
            inv.setItem(INPUT_1, null);
            inv.setItem(INPUT_2, null);
            inv.setItem(OUTPUT_SLOT, dustItem.create(nextTier));
            player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1f, 1.2f);
            player.sendMessage("§5§l✦ Alchemist §8» §7Combined into " + nextTier.getColor() + nextTier.getDisplayName() + " Dust§7!");
            return;
        }

        // Book combining
        if (item1.getType() == Material.ENCHANTED_BOOK && item2.getType() == Material.ENCHANTED_BOOK) {
            Map<VortexEnchant, Integer> enchants1 = plugin.getEnchantManager().getEnchants(item1);
            Map<VortexEnchant, Integer> enchants2 = plugin.getEnchantManager().getEnchants(item2);

            if (enchants1.size() != 1 || enchants2.size() != 1) {
                player.sendMessage("§cBooks must each contain exactly one enchantment!");
                return;
            }

            Map.Entry<VortexEnchant, Integer> e1 = enchants1.entrySet().iterator().next();
            Map.Entry<VortexEnchant, Integer> e2 = enchants2.entrySet().iterator().next();

            if (!e1.getKey().getId().equals(e2.getKey().getId())) {
                player.sendMessage("§cBoth books must have the same enchantment!");
                return;
            }

            if (!e1.getValue().equals(e2.getValue())) {
                player.sendMessage("§cBoth books must be the same level!");
                return;
            }

            VortexEnchant enchant = e1.getKey();
            int currentLevel = e1.getValue();
            if (currentLevel >= enchant.getMaxLevel()) {
                player.sendMessage("§cEnchantment is already at max level!");
                return;
            }

            int newLevel = currentLevel + 1;
            ItemStack result = plugin.getEnchantManager().createEnchantedBook(enchant, newLevel);

            inv.setItem(INPUT_1, null);
            inv.setItem(INPUT_2, null);
            inv.setItem(OUTPUT_SLOT, result);
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1.5f);
            player.sendMessage("§5§l✦ Alchemist §8» §7Combined into " + enchant.getLoreLine(newLevel) + "§7!");
            return;
        }

        player.sendMessage("§cUnsupported combination! Use two identical enchant books or two same-tier dust items.");
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
