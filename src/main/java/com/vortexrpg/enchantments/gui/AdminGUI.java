package com.vortexrpg.enchantments.gui;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Admin management GUI for VortexEnchantments.
 * Features: browse all 300 enchants, toggle enable/disable, view proc stats, quick-give.
 */
@SuppressWarnings("deprecation")
public class AdminGUI implements Listener {

    private static final String GUI_TITLE_PREFIX = "§4§lVortex Admin §8» ";
    private static final int SIZE = 54; // 6 rows
    private static final int ENCHANTS_PER_PAGE = 36; // rows 1-4
    private static final int ENCHANT_START = 9;

    // Navigation and filter slots (row 5: 45-53)
    private static final int PREV_SLOT = 45;
    private static final int NEXT_SLOT = 53;
    private static final int FILTER_RARITY_SLOT = 47;
    private static final int FILTER_TARGET_SLOT = 48;
    private static final int STATS_SLOT = 49;
    private static final int RELOAD_SLOT = 50;
    private static final int ENABLE_ALL_SLOT = 51;

    private final VortexEnchantments plugin;
    private final Map<UUID, AdminState> playerStates = new ConcurrentHashMap<>();

    // Global proc stats counter (enchant ID → count)
    private final Map<String, AtomicInteger> procStats = new ConcurrentHashMap<>();

    public AdminGUI(VortexEnchantments plugin) {
        this.plugin = plugin;
    }

    /**
     * Increment proc counter for an enchant. Called from enchant listeners.
     */
    public void recordProc(String enchantId) {
        procStats.computeIfAbsent(enchantId, k -> new AtomicInteger(0)).incrementAndGet();
    }

    public int getProcCount(String enchantId) {
        AtomicInteger count = procStats.get(enchantId);
        return count != null ? count.get() : 0;
    }

    public void open(Player player) {
        open(player, 0, null, null);
    }

    public void open(Player player, int page, EnchantRarity filterRarity, ItemTarget filterTarget) {
        List<VortexEnchant> enchants = getFilteredEnchants(filterRarity, filterTarget);
        int totalPages = Math.max(1, (int) Math.ceil((double) enchants.size() / ENCHANTS_PER_PAGE));
        page = Math.max(0, Math.min(page, totalPages - 1));

        String rarityLabel = filterRarity != null ? filterRarity.getDisplayName() : "All";
        String targetLabel = filterTarget != null ? filterTarget.name() : "All";

        String title = GUI_TITLE_PREFIX + "§7" + (page + 1) + "/" + totalPages;
        Inventory inv = Bukkit.createInventory(null, SIZE, title);

        // Row 0 — stats bar
        long total = plugin.getEnchantManager().getAll().size();
        long enabled = plugin.getEnchantManager().getAll().stream().filter(VortexEnchant::isEnabled).count();
        long disabled = total - enabled;
        inv.setItem(0, makeItem(Material.EMERALD_BLOCK, "§a" + enabled + " Enabled"));
        inv.setItem(1, makeItem(Material.REDSTONE_BLOCK, "§c" + disabled + " Disabled"));
        inv.setItem(2, makeItem(Material.DIAMOND, "§b" + total + " Total"));
        inv.setItem(4, makeItem(Material.COMMAND_BLOCK, "§4§lAdmin Panel",
            "§7Manage all VortexEnchantments",
            "§7Filter: Rarity=" + rarityLabel + " Target=" + targetLabel));

        // Enchant entries rows 1-4
        int start = page * ENCHANTS_PER_PAGE;
        int end = Math.min(start + ENCHANTS_PER_PAGE, enchants.size());
        for (int i = start; i < end; i++) {
            VortexEnchant e = enchants.get(i);
            inv.setItem(ENCHANT_START + (i - start), makeAdminEnchantItem(e));
        }

        // Fill empty enchant slots
        ItemStack filler = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§8");
        for (int i = ENCHANT_START; i < ENCHANT_START + ENCHANTS_PER_PAGE; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, filler);
        }

        // Bottom row navigation
        if (page > 0) inv.setItem(PREV_SLOT, makeItem(Material.ARROW, "§aPrevious Page"));
        else inv.setItem(PREV_SLOT, filler);

        if (page < totalPages - 1) inv.setItem(NEXT_SLOT, makeItem(Material.ARROW, "§aNext Page"));
        else inv.setItem(NEXT_SLOT, filler);

        // Filter buttons
        inv.setItem(FILTER_RARITY_SLOT, makeItem(Material.AMETHYST_SHARD,
            "§dFilter by Rarity: §e" + rarityLabel,
            "§7Click to cycle rarity filter."));
        inv.setItem(FILTER_TARGET_SLOT, makeItem(Material.DIAMOND_SWORD,
            "§bFilter by Target: §e" + targetLabel,
            "§7Click to cycle target filter."));
        inv.setItem(STATS_SLOT, makeItem(Material.BOOK, "§6Proc Stats",
            "§7Total procs tracked this session:",
            "§f" + procStats.values().stream().mapToInt(AtomicInteger::get).sum()));
        inv.setItem(RELOAD_SLOT, makeItem(Material.COMPARATOR, "§eReload All Configs",
            "§7Reloads config.yml and all enchant configs."));
        inv.setItem(ENABLE_ALL_SLOT, makeItem(Material.LEVER, "§aEnable All / §cDisable All",
            "§7Left-click: Enable all enchants",
            "§7Right-click: Disable all enchants"));

        // Fill remaining bottom slots
        for (int i = 45; i < SIZE; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, filler);
        }

        player.openInventory(inv);
        playerStates.put(player.getUniqueId(), new AdminState(page, filterRarity, filterTarget, totalPages));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();
        if (!title.startsWith(GUI_TITLE_PREFIX)) return;
        event.setCancelled(true);

        AdminState state = playerStates.get(player.getUniqueId());
        if (state == null) return;

        int slot = event.getRawSlot();

        // Navigation
        if (slot == PREV_SLOT && state.page > 0) {
            reopen(player, state.page - 1, state.filterRarity, state.filterTarget);
            return;
        }
        if (slot == NEXT_SLOT && state.page < state.totalPages - 1) {
            reopen(player, state.page + 1, state.filterRarity, state.filterTarget);
            return;
        }

        // Filter rarity (cycle)
        if (slot == FILTER_RARITY_SLOT) {
            EnchantRarity next = cycleRarity(state.filterRarity);
            reopen(player, 0, next, state.filterTarget);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            return;
        }

        // Filter target (cycle)
        if (slot == FILTER_TARGET_SLOT) {
            ItemTarget next = cycleTarget(state.filterTarget);
            reopen(player, 0, state.filterRarity, next);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            return;
        }

        // Reload
        if (slot == RELOAD_SLOT) {
            plugin.getConfigManager().loadAll();
            plugin.getEnchantManager().reloadAll();
            player.sendMessage("§aConfiguration reloaded!");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            reopen(player, state.page, state.filterRarity, state.filterTarget);
            return;
        }

        // Enable/disable all
        if (slot == ENABLE_ALL_SLOT) {
            boolean enable = event.isLeftClick();
            for (VortexEnchant e : plugin.getEnchantManager().getAll()) {
                e.setEnabled(enable);
            }
            player.sendMessage(enable ? "§aAll enchantments enabled!" : "§cAll enchantments disabled!");
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            reopen(player, state.page, state.filterRarity, state.filterTarget);
            return;
        }

        // Enchant slot click
        if (slot >= ENCHANT_START && slot < ENCHANT_START + ENCHANTS_PER_PAGE) {
            List<VortexEnchant> enchants = getFilteredEnchants(state.filterRarity, state.filterTarget);
            int index = state.page * ENCHANTS_PER_PAGE + (slot - ENCHANT_START);
            if (index >= enchants.size()) return;

            VortexEnchant enchant = enchants.get(index);

            if (event.isLeftClick()) {
                // Toggle enable/disable
                enchant.setEnabled(!enchant.isEnabled());
                saveEnchantEnabled(enchant);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, enchant.isEnabled() ? 1.2f : 0.8f);
                player.sendMessage((enchant.isEnabled() ? "§a" : "§c") + enchant.getDisplayName()
                    + (enchant.isEnabled() ? " enabled" : " disabled"));
                reopen(player, state.page, state.filterRarity, state.filterTarget);
            } else if (event.isRightClick()) {
                // Quick give — give the book to yourself
                int level = enchant.getMaxLevel();
                ItemStack book = plugin.getEnchantManager().createEnchantedBook(enchant, level);
                player.getInventory().addItem(book);
                player.sendMessage("§aGave yourself: " + enchant.getLoreLine(level));
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 1.0f);
            }
        }
    }

    private void reopen(Player player, int page, EnchantRarity filterRarity, ItemTarget filterTarget) {
        SchedulerUtil.runEntityTask(plugin, player, () -> open(player, page, filterRarity, filterTarget));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().startsWith(GUI_TITLE_PREFIX)) {
            playerStates.remove(event.getPlayer().getUniqueId());
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private List<VortexEnchant> getFilteredEnchants(EnchantRarity filterRarity, ItemTarget filterTarget) {
        return plugin.getEnchantManager().getAll().stream()
            .filter(e -> filterRarity == null || e.getRarity() == filterRarity)
            .filter(e -> filterTarget == null || e.getTargets().contains(filterTarget))
            .collect(Collectors.toList());
    }

    private ItemStack makeAdminEnchantItem(VortexEnchant enchant) {
        Material mat = enchant.isEnabled() ? Material.ENCHANTED_BOOK : Material.BOOK;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(enchant.getLoreLine(enchant.getMaxLevel()));
        List<String> lore = new ArrayList<>();
        lore.add(enchant.getRarity().getColor() + "§l" + enchant.getRarity().getDisplayName());
        lore.add("§7ID: §f" + enchant.getId());
        lore.add("§7Max Level: §f" + enchant.getMaxLevel());
        lore.add("§7Targets: §f" + enchant.getTargets().stream().map(ItemTarget::name).collect(Collectors.joining(", ")));
        lore.add("§7Procs: §e" + getProcCount(enchant.getId()));
        lore.add("");
        lore.add("§7Status: " + (enchant.isEnabled() ? "§a§lENABLED" : "§c§lDISABLED"));
        lore.add("");
        lore.add("§e Left-click: §7Toggle enable/disable");
        lore.add("§e Right-click: §7Give max-level book");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private void saveEnchantEnabled(VortexEnchant enchant) {
        String category = enchant.getTargets().isEmpty() ? "misc" :
            enchant.getTargets().get(0).name().toLowerCase().replace("_", "");
        String path = "enchants/" + category + "/" + enchant.getId() + ".yml";
        File file = new File(plugin.getDataFolder(), path);
        if (file.exists()) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            yaml.set("enabled", enchant.isEnabled());
            try {
                yaml.save(file);
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Could not save " + path, e);
            }
        }
    }

    private EnchantRarity cycleRarity(EnchantRarity current) {
        if (current == null) return EnchantRarity.COMMON;
        EnchantRarity[] values = EnchantRarity.values();
        int next = current.ordinal() + 1;
        return next >= values.length ? null : values[next]; // null = all
    }

    private ItemTarget cycleTarget(ItemTarget current) {
        if (current == null) return ItemTarget.SWORD;
        ItemTarget[] values = ItemTarget.values();
        int next = current.ordinal() + 1;
        return next >= values.length ? null : values[next]; // null = all
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

    private record AdminState(int page, EnchantRarity filterRarity, ItemTarget filterTarget, int totalPages) {}
}
