package com.vortexrpg.enchantments.gui;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
 * Enchantment browser GUI organized by item type tabs with paginated tab rows.
 */
@SuppressWarnings("deprecation")
public class EnchantBrowserGUI implements Listener {

    private static final String GUI_TITLE_PREFIX = "§6§lVortex Enchants §8» ";
    private static final int ROWS = 6;
    private static final int SIZE = ROWS * 9;

    private static final int ENCHANT_START = 9;
    private static final int ENCHANT_END = 44;
    private static final int ENCHANTS_PER_PAGE = 36;

    private static final int PREV_SLOT = 45;
    private static final int NEXT_SLOT = 53;
    private static final int INFO_SLOT = 49;

    // All browsable categories — split across tab pages (8 per page + 1 nav slot)
    private static final ItemTarget[][] TAB_PAGES = {
        { ItemTarget.SWORD, ItemTarget.AXE, ItemTarget.BOW, ItemTarget.CROSSBOW,
          ItemTarget.TRIDENT, ItemTarget.SPEAR, ItemTarget.HAMMER, ItemTarget.PICKAXE },
        { ItemTarget.SHOVEL, ItemTarget.HOE, ItemTarget.HELMET, ItemTarget.CHESTPLATE,
          ItemTarget.LEGGINGS, ItemTarget.BOOTS, ItemTarget.SHIELD, ItemTarget.ELYTRA },
        { ItemTarget.FISHING_ROD, null /*ALL*/ }
    };
    private static final int TABS_PER_PAGE = 8;

    private final VortexEnchantments plugin;
    private final Map<UUID, GUIState> playerStates = new ConcurrentHashMap<>();

    public EnchantBrowserGUI(VortexEnchantments plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        open(player, ItemTarget.SWORD, 0, 0);
    }

    public void open(Player player, ItemTarget tab, int page, int tabPage) {
        List<VortexEnchant> enchants = getEnchantsForTab(tab);
        int totalPages = Math.max(1, (int) Math.ceil((double) enchants.size() / ENCHANTS_PER_PAGE));
        page = Math.max(0, Math.min(page, totalPages - 1));
        tabPage = Math.max(0, Math.min(tabPage, TAB_PAGES.length - 1));

        String title = GUI_TITLE_PREFIX + (tab != null ? tab.name() : "ALL") + " §8[" + (page + 1) + "/" + totalPages + "]";
        Inventory inv = Bukkit.createInventory(null, SIZE, title);

        // Tab buttons (row 0) — show current tab page
        ItemTarget[] currentTabs = TAB_PAGES[tabPage];
        for (int i = 0; i < TABS_PER_PAGE; i++) {
            if (i < currentTabs.length) {
                ItemTarget t = currentTabs[i];
                Material mat = getTabMaterial(t);
                String label = t != null ? t.name() : "ALL";
                boolean selected = (t == tab) || (t == null && tab == null);
                String prefix = selected ? "§a§l▶ " : "§e";
                ItemStack btn = makeItem(mat, prefix + label, "§7Click to browse " + label + " enchants");
                inv.setItem(i, btn);
            } else {
                inv.setItem(i, makeItem(Material.GRAY_STAINED_GLASS_PANE, "§8", ""));
            }
        }
        // Slot 8 = tab page navigation arrow
        if (tabPage < TAB_PAGES.length - 1) {
            inv.setItem(8, makeItem(Material.SPECTRAL_ARROW, "§e▶ More Categories", "§7Click for more item types"));
        } else if (tabPage > 0) {
            inv.setItem(8, makeItem(Material.SPECTRAL_ARROW, "§e◀ Back", "§7Click for previous categories"));
        } else {
            inv.setItem(8, makeItem(Material.GRAY_STAINED_GLASS_PANE, "§8", ""));
        }

        // Enchant entries
        int start = page * ENCHANTS_PER_PAGE;
        int end = Math.min(start + ENCHANTS_PER_PAGE, enchants.size());
        for (int i = start; i < end; i++) {
            VortexEnchant e = enchants.get(i);
            inv.setItem(ENCHANT_START + (i - start), makeEnchantItem(e));
        }

        // Navigation
        if (page > 0) inv.setItem(PREV_SLOT, makeItem(Material.ARROW, "§aPrevious Page", "§7Page " + page));
        if (page < totalPages - 1) inv.setItem(NEXT_SLOT, makeItem(Material.ARROW, "§aNext Page", "§7Page " + (page + 2)));
        inv.setItem(INFO_SLOT, makeItem(Material.BOOK,
            "§6VortexEnchantments",
            "§7Total: §f" + plugin.getEnchantManager().getEnchantCount() + " §7enchantments",
            "§7Showing: §f" + (tab != null ? tab.name() : "All")));

        // Fill empty slots
        ItemStack filler = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§8", "");
        for (int i = ENCHANT_START; i <= ENCHANT_END; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, filler);
        }
        for (int i = PREV_SLOT; i <= NEXT_SLOT; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, filler);
        }

        player.openInventory(inv);
        playerStates.put(player.getUniqueId(), new GUIState(tab, page, tabPage, totalPages));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();
        if (!title.startsWith(GUI_TITLE_PREFIX)) return;
        event.setCancelled(true);

        GUIState state = playerStates.get(player.getUniqueId());
        if (state == null) return;

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= SIZE) return;

        // Slot 8 = tab page navigation
        if (slot == 8) {
            int nextTabPage = state.tabPage < TAB_PAGES.length - 1 ? state.tabPage + 1 : 0;
            SchedulerUtil.runEntityTask(plugin, player, () -> open(player, state.tab, state.page, nextTabPage));
            return;
        }

        // Tab click (slots 0-7)
        if (slot >= 0 && slot < TABS_PER_PAGE) {
            ItemTarget[] currentTabs = TAB_PAGES[state.tabPage];
            if (slot < currentTabs.length) {
                ItemTarget clickedTab = currentTabs[slot];
                SchedulerUtil.runEntityTask(plugin, player, () -> open(player, clickedTab, 0, state.tabPage));
            }
            return;
        }

        // Navigation
        if (slot == PREV_SLOT && state.page > 0) {
            SchedulerUtil.runEntityTask(plugin, player, () -> open(player, state.tab, state.page - 1, state.tabPage));
            return;
        }
        if (slot == NEXT_SLOT && state.page < state.totalPages - 1) {
            SchedulerUtil.runEntityTask(plugin, player, () -> open(player, state.tab, state.page + 1, state.tabPage));
            return;
        }

        // Enchant slot click → show info
        if (slot >= ENCHANT_START && slot <= ENCHANT_END) {
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() == Material.GRAY_STAINED_GLASS_PANE) return;
            if (item.getItemMeta() != null && item.getItemMeta().getLore() != null) {
                player.sendMessage("§6§lEnchantment Info:");
                item.getItemMeta().getLore().forEach(line -> player.sendMessage("  " + line));
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().startsWith(GUI_TITLE_PREFIX)) {
            playerStates.remove(event.getPlayer().getUniqueId());
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private List<VortexEnchant> getEnchantsForTab(ItemTarget tab) {
        if (tab == null) return plugin.getEnchantManager().getAll();
        return plugin.getEnchantManager().getForTarget(tab);
    }

    private ItemStack makeEnchantItem(VortexEnchant enchant) {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(enchant.getLoreLine(enchant.getMaxLevel()));
        List<String> lore = new ArrayList<>();
        lore.add(enchant.getRarity().getColor() + "§l" + enchant.getRarity().getDisplayName());
        lore.add("§7" + enchant.getDescription());
        lore.add("§8Max Level: §f" + enchant.getMaxLevel());
        lore.add("§8Targets: §f" + enchant.getTargets().stream().map(ItemTarget::name).reduce((a, b) -> a + ", " + b).orElse(""));
        if (!enchant.getConflicts().isEmpty()) {
            lore.add("§cConflicts: §f" + String.join(", ", enchant.getConflicts()));
        }
        lore.add("§8Status: " + (enchant.isEnabled() ? "§aEnabled" : "§cDisabled"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack makeItem(Material mat, String name, String... lorelines) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(name);
        List<String> lore = new ArrayList<>();
        for (String line : lorelines) if (!line.isEmpty()) lore.add(line);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private Material getTabMaterial(ItemTarget tab) {
        if (tab == null) return Material.BOOK;
        return switch (tab) {
            case SWORD -> Material.DIAMOND_SWORD;
            case AXE -> Material.DIAMOND_AXE;
            case BOW -> Material.BOW;
            case CROSSBOW -> Material.CROSSBOW;
            case TRIDENT -> Material.TRIDENT;
            case SPEAR -> Material.TRIDENT;
            case HAMMER -> Material.MACE;
            case PICKAXE -> Material.DIAMOND_PICKAXE;
            case SHOVEL -> Material.DIAMOND_SHOVEL;
            case HOE -> Material.DIAMOND_HOE;
            case HELMET -> Material.DIAMOND_HELMET;
            case CHESTPLATE -> Material.DIAMOND_CHESTPLATE;
            case LEGGINGS -> Material.DIAMOND_LEGGINGS;
            case BOOTS -> Material.DIAMOND_BOOTS;
            case SHIELD -> Material.SHIELD;
            case ELYTRA -> Material.ELYTRA;
            case FISHING_ROD -> Material.FISHING_ROD;
            default -> Material.ENCHANTED_BOOK;
        };
    }

    private record GUIState(ItemTarget tab, int page, int tabPage, int totalPages) {}
}
