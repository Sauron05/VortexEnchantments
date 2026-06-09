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
import java.util.concurrent.ThreadLocalRandom;

/**
 * GUI for the Extractor Scroll — lets a player pick which enchant to extract.
 * Shows all VortexEnchants on held item, click one to attempt extraction.
 */
@SuppressWarnings("deprecation")
public class ExtractorGUI implements Listener {

    private static final String GUI_TITLE = "§c§l✦ Enchant Extractor ✦";
    private static final int SIZE = 27;

    /** Success chance per rarity ordinal (0=COMMON to 5=MYTHIC). */
    private static final double[] SUCCESS_CHANCES = {0.90, 0.80, 0.60, 0.40, 0.25, 0.15};

    private final VortexEnchantments plugin;
    private final Map<UUID, ExtractorState> activeStates = new ConcurrentHashMap<>();

    public ExtractorGUI(VortexEnchantments plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, ItemStack targetItem) {
        Map<VortexEnchant, Integer> enchants = plugin.getEnchantManager().getEnchants(targetItem);
        if (enchants.isEmpty()) {
            player.sendMessage("§cThat item has no VortexEnchantments to extract.");
            return;
        }

        Inventory inv = Bukkit.createInventory(null, SIZE, GUI_TITLE);

        // Fill with border
        ItemStack border = makeItem(Material.BLACK_STAINED_GLASS_PANE, "§8");
        for (int i = 0; i < SIZE; i++) inv.setItem(i, border);

        // Info item
        inv.setItem(4, makeItem(Material.PAPER, "§c§lExtractor Scroll",
            "§7Click an enchantment to extract it.",
            "§7Success depends on rarity."));

        // Show enchants starting at slot 9
        List<Map.Entry<VortexEnchant, Integer>> enchantList = new ArrayList<>(enchants.entrySet());
        for (int i = 0; i < enchantList.size() && i < 9; i++) {
            VortexEnchant enchant = enchantList.get(i).getKey();
            int level = enchantList.get(i).getValue();
            double chance = getSuccessChance(enchant.getRarity());

            Material mat = switch (enchant.getRarity()) {
                case COMMON -> Material.WHITE_STAINED_GLASS_PANE;
                case UNCOMMON -> Material.LIME_STAINED_GLASS_PANE;
                case RARE -> Material.BLUE_STAINED_GLASS_PANE;
                case EPIC -> Material.PURPLE_STAINED_GLASS_PANE;
                case LEGENDARY -> Material.ORANGE_STAINED_GLASS_PANE;
                case MYTHIC -> Material.RED_STAINED_GLASS_PANE;
            };

            inv.setItem(9 + i, makeItem(mat, enchant.getLoreLine(level),
                enchant.getRarity().getColor() + enchant.getRarity().getDisplayName(),
                "§7Level: §f" + level + "/" + enchant.getMaxLevel(),
                "§7Success chance: §e" + (int) (chance * 100) + "%",
                "",
                "§eClick to extract!"));
        }

        player.openInventory(inv);
        activeStates.put(player.getUniqueId(), new ExtractorState(enchantList));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;
        event.setCancelled(true);

        ExtractorState state = activeStates.get(player.getUniqueId());
        if (state == null) return;

        int slot = event.getRawSlot();
        if (slot < 9 || slot > 17) return;

        int index = slot - 9;
        if (index >= state.enchants.size()) return;

        VortexEnchant enchant = state.enchants.get(index).getKey();
        int level = state.enchants.get(index).getValue();
        double chance = getSuccessChance(enchant.getRarity());

        // Get the item from main hand
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem.getType() == Material.AIR) {
            player.sendMessage("§cYou must be holding the enchanted item!");
            SchedulerUtil.runEntityTask(plugin, player, () -> player.closeInventory());
            return;
        }

        // Consume extractor from off hand
        ItemStack offHand = player.getInventory().getItemInOffHand();
        if (plugin.getExtractorScroll().isExtractor(offHand)) {
            if (offHand.getAmount() > 1) {
                offHand.setAmount(offHand.getAmount() - 1);
            } else {
                player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
            }
        }

        // Roll for success
        boolean success = ThreadLocalRandom.current().nextDouble() < chance;

        // Always remove the enchant from the item
        plugin.getEnchantManager().removeEnchant(heldItem, enchant);

        if (success) {
            // Give the enchant as a book
            ItemStack book = plugin.getEnchantManager().createEnchantedBook(enchant, level);
            player.getInventory().addItem(book);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
            player.sendMessage("§a✦ Successfully extracted " + enchant.getLoreLine(level) + " §a✦");
        } else {
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 0.5f);
            player.sendMessage("§c✦ Extraction failed! " + enchant.getLoreLine(level) + " §cwas destroyed. ✦");
        }

        SchedulerUtil.runEntityTask(plugin, player, () -> player.closeInventory());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals(GUI_TITLE)) {
            activeStates.remove(event.getPlayer().getUniqueId());
        }
    }

    private double getSuccessChance(EnchantRarity rarity) {
        int ord = rarity.ordinal();
        return ord < SUCCESS_CHANCES.length ? SUCCESS_CHANCES[ord] : 0.10;
    }

    private ItemStack makeItem(Material mat, String name, String... loreLines) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(name);
        if (loreLines.length > 0) {
            meta.setLore(Arrays.asList(loreLines));
        }
        item.setItemMeta(meta);
        return item;
    }

    private record ExtractorState(List<Map.Entry<VortexEnchant, Integer>> enchants) {}
}
