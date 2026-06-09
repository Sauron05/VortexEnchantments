package com.vortexrpg.enchantments.gui;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.hook.VaultHook;
import com.vortexrpg.enchantments.util.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Economy Enchant Shop — premium buying experience with advanced features.
 * 
 * Superior to AE implementation:
 *   - Flash Sale system: one random rarity is on sale with configurable discount (rotates hourly)
 *   - Buy Preview: RIGHT-click to see 5 possible enchants before committing (LEFT-click to buy)
 *   - Bulk Buy: SHIFT-click to buy 3 at once with a 10% bulk discount
 *   - Daily purchase limit per player per rarity (configurable, 0 = unlimited)
 *   - Dynamic pricing: shows savings on flash sales
 *   - Player purchase history tracking
 *   - Info row showing balance, daily limit status
 */
@SuppressWarnings("deprecation")
public class EnchantShopGUI implements Listener {

    private static final String GUI_TITLE = "§6§l✦ Enchant Shop ✦";
    private static final String PREVIEW_TITLE = "§8§l✦ Preview: ";
    private final VortexEnchantments plugin;
    private final Map<UUID, Long> clickCooldown = new ConcurrentHashMap<>();
    private final Map<UUID, Map<EnchantRarity, Integer>> dailyPurchases = new ConcurrentHashMap<>();
    private long lastDailyReset = System.currentTimeMillis();

    // Flash sale state
    private EnchantRarity flashSaleRarity;
    private long flashSaleExpiry;

    public EnchantShopGUI(VortexEnchantments plugin) {
        this.plugin = plugin;
        rotateFlashSale();
    }

    public void open(Player player) {
        VaultHook vault = plugin.getVaultHook();
        if (!vault.isEnabled()) {
            player.sendMessage("§cEconomy is not available. Vault is not installed.");
            return;
        }

        checkDailyReset();
        checkFlashSaleRotation();

        Inventory inv = Bukkit.createInventory(null, 45, GUI_TITLE);

        // Fill background
        ItemStack filler = makeItem(Material.BLACK_STAINED_GLASS_PANE, "§8");
        for (int i = 0; i < 45; i++) inv.setItem(i, filler);

        // Top info bar
        ItemStack info = new ItemStack(Material.GOLD_INGOT);
        ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName("§e§lYour Balance");
            double balance = vault.getBalance(player);
            List<String> infoLore = new ArrayList<>();
            infoLore.add("§7Balance: §a$" + vault.format(balance));
            infoLore.add("");
            infoLore.add("§e§lLeft-Click §7— Buy 1 random book");
            infoLore.add("§b§lRight-Click §7— Preview 5 possible enchants");
            infoLore.add("§6§lShift-Click §7— Buy 3 at 10% discount");
            infoMeta.setLore(infoLore);
            info.setItemMeta(infoMeta);
        }
        inv.setItem(4, info);

        // Flash sale indicator
        if (flashSaleRarity != null) {
            ItemStack saleIcon = new ItemStack(Material.BELL);
            ItemMeta saleMeta = saleIcon.getItemMeta();
            if (saleMeta != null) {
                saleMeta.setDisplayName("§c§l⚡ FLASH SALE ⚡");
                int discount = getFlashSaleDiscount();
                long remaining = Math.max(0, (flashSaleExpiry - System.currentTimeMillis()) / 1000);
                List<String> saleLore = new ArrayList<>();
                saleLore.add("§7" + flashSaleRarity.getColor() + flashSaleRarity.getDisplayName()
                    + " §7books are §a" + discount + "% OFF§7!");
                saleLore.add("§7Expires in: §e" + formatSeconds(remaining));
                saleMeta.setLore(saleLore);
                saleIcon.setItemMeta(saleMeta);
            }
            inv.setItem(0, saleIcon);
        }

        // Place rarity items in a centered row: slots 19-24
        EnchantRarity[] rarities = EnchantRarity.values();
        int[] slots = {19, 20, 21, 22, 23, 24};
        double balance = vault.getBalance(player);

        for (int i = 0; i < rarities.length && i < slots.length; i++) {
            EnchantRarity rarity = rarities[i];
            double basePrice = getBasePrice(rarity);
            boolean isOnSale = rarity == flashSaleRarity;
            double price = isOnSale ? basePrice * (1 - getFlashSaleDiscount() / 100.0) : basePrice;

            Material mat = switch (rarity) {
                case COMMON -> Material.BOOK;
                case UNCOMMON -> Material.ENCHANTED_BOOK;
                case RARE -> Material.LAPIS_LAZULI;
                case EPIC -> Material.AMETHYST_SHARD;
                case LEGENDARY -> Material.GOLDEN_APPLE;
                case MYTHIC -> Material.NETHER_STAR;
            };

            // Count available enchants for this rarity
            long enchantCount = plugin.getEnchantManager().getAll().stream()
                .filter(VortexEnchant::isEnabled)
                .filter(e -> e.getRarity() == rarity)
                .count();

            int dailyUsed = getDailyPurchases(player, rarity);
            int dailyLimit = getDailyLimit(rarity);

            ItemStack icon = new ItemStack(mat);
            ItemMeta meta = icon.getItemMeta();
            if (meta != null) {
                String saleBadge = isOnSale ? " §c§l⚡SALE" : "";
                meta.setDisplayName(rarity.getColor() + "§l" + rarity.getDisplayName() + " Book" + saleBadge);
                List<String> lore = new ArrayList<>();
                lore.add("§8Random " + rarity.getDisplayName() + " Enchantment");
                lore.add("§8§m                              ");
                lore.add("");
                lore.add("§7Receive a random enchanted book");
                lore.add("§7of " + rarity.getColor() + rarity.getDisplayName() + " §7rarity.");
                lore.add("§7Available enchants: §f" + enchantCount);
                lore.add("");

                if (isOnSale) {
                    lore.add("§7Price: §a$" + vault.format(price) + " §8§m$" + vault.format(basePrice)
                        + " §c(-" + getFlashSaleDiscount() + "%)");
                } else {
                    lore.add("§7Price: §a$" + vault.format(price));
                }
                double bulkPrice = price * 3 * 0.9;
                lore.add("§7Bulk (3x): §a$" + vault.format(bulkPrice) + " §8(-10%)");
                lore.add("§7Balance: §f$" + vault.format(balance));
                if (dailyLimit > 0) {
                    lore.add("§7Daily: §e" + dailyUsed + "§7/" + dailyLimit);
                }
                lore.add("");

                boolean canAfford = balance >= price;
                boolean withinLimit = dailyLimit <= 0 || dailyUsed < dailyLimit;

                if (!canAfford) {
                    lore.add("§c✘ Not enough money");
                } else if (!withinLimit) {
                    lore.add("§c✘ Daily limit reached");
                } else {
                    lore.add("§a✔ Left-Click to buy!");
                    lore.add("§b✔ Right-Click to preview");
                    lore.add("§6✔ Shift-Click for bulk (3x)");
                }
                lore.add("§8§m                              ");
                meta.setLore(lore);

                if (isOnSale) {
                    meta.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true);
                    meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
                }

                icon.setItemMeta(meta);
            }
            inv.setItem(slots[i], icon);

            // Rarity border glass
            Material glassMat = switch (rarity) {
                case COMMON -> Material.WHITE_STAINED_GLASS_PANE;
                case UNCOMMON -> Material.LIME_STAINED_GLASS_PANE;
                case RARE -> Material.BLUE_STAINED_GLASS_PANE;
                case EPIC -> Material.PURPLE_STAINED_GLASS_PANE;
                case LEGENDARY -> Material.ORANGE_STAINED_GLASS_PANE;
                case MYTHIC -> Material.RED_STAINED_GLASS_PANE;
            };
            // Glass above and below
            if (i < slots.length) {
                inv.setItem(slots[i] - 9, makeItem(glassMat, rarity.getColor() + "§m "));
                inv.setItem(slots[i] + 9, makeItem(glassMat, rarity.getColor() + "§m "));
            }
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();

        // Handle preview GUI
        if (title.startsWith(PREVIEW_TITLE)) {
            event.setCancelled(true);
            return;
        }

        if (!title.equals(GUI_TITLE)) return;
        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot < 19 || slot > 24) return;

        // Anti-spam
        long now = System.currentTimeMillis();
        Long last = clickCooldown.get(player.getUniqueId());
        if (last != null && now - last < 500) return;
        clickCooldown.put(player.getUniqueId(), now);

        int rarityIndex = slot - 19;
        EnchantRarity[] rarities = EnchantRarity.values();
        if (rarityIndex >= rarities.length) return;
        EnchantRarity rarity = rarities[rarityIndex];

        if (event.getClick() == ClickType.RIGHT) {
            // Preview mode — show 5 possible enchants
            SchedulerUtil.runEntityTask(plugin, player, () -> openPreview(player, rarity));
            return;
        }

        boolean isBulk = event.getClick().isShiftClick();
        int quantity = isBulk ? 3 : 1;
        double bulkDiscount = isBulk ? 0.9 : 1.0;

        purchaseBooks(player, rarity, quantity, bulkDiscount);
    }

    private void purchaseBooks(Player player, EnchantRarity rarity, int quantity, double bulkDiscount) {
        VaultHook vault = plugin.getVaultHook();
        if (!vault.isEnabled()) return;

        checkDailyReset();
        checkFlashSaleRotation();

        double basePrice = getBasePrice(rarity);
        boolean isOnSale = rarity == flashSaleRarity;
        double unitPrice = isOnSale ? basePrice * (1 - getFlashSaleDiscount() / 100.0) : basePrice;
        double totalPrice = unitPrice * quantity * bulkDiscount;

        if (!vault.has(player, totalPrice)) {
            player.sendMessage("§cYou need §a$" + vault.format(totalPrice) + "§c! You have §e$"
                + vault.format(vault.getBalance(player)));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.7f, 1.0f);
            return;
        }

        int dailyUsed = getDailyPurchases(player, rarity);
        int dailyLimit = getDailyLimit(rarity);
        if (dailyLimit > 0 && dailyUsed + quantity > dailyLimit) {
            player.sendMessage("§cDaily purchase limit reached for " + rarity.getColor()
                + rarity.getDisplayName() + "§c! (" + dailyUsed + "/" + dailyLimit + ")");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.7f, 1.0f);
            return;
        }

        List<VortexEnchant> pool = plugin.getEnchantManager().getAll().stream()
            .filter(VortexEnchant::isEnabled)
            .filter(e -> e.getRarity() == rarity)
            .toList();

        if (pool.isEmpty()) {
            player.sendMessage("§cNo enchantments available for " + rarity.getDisplayName() + " tier.");
            return;
        }

        vault.withdraw(player, totalPrice);
        addDailyPurchases(player, rarity, quantity);

        ThreadLocalRandom rng = ThreadLocalRandom.current();
        List<String> received = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            VortexEnchant chosen = pool.get(rng.nextInt(pool.size()));
            int level = rng.nextInt(1, chosen.getMaxLevel() + 1);
            ItemStack book = plugin.getEnchantManager().createEnchantedBook(chosen, level);
            HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(book);
            overflow.values().forEach(it -> player.getWorld().dropItemNaturally(player.getLocation(), it));
            received.add(chosen.getLoreLine(level));
        }

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.5f);
        if (quantity == 1) {
            player.sendMessage("§a✦ Purchased: " + received.get(0)
                + (isOnSale ? " §c⚡SALE" : "")
                + " §afor §e$" + vault.format(totalPrice));
        } else {
            player.sendMessage("§a✦ Purchased " + quantity + "x " + rarity.getColor()
                + rarity.getDisplayName() + " §abooks for §e$" + vault.format(totalPrice)
                + (isOnSale ? " §c⚡SALE" : "")
                + " §7(-10% bulk)");
            for (String r : received) player.sendMessage("  §8▸ " + r);
        }

        // Refresh
        SchedulerUtil.runEntityTask(plugin, player, () -> open(player));
    }

    private void openPreview(Player player, EnchantRarity rarity) {
        List<VortexEnchant> pool = plugin.getEnchantManager().getAll().stream()
            .filter(VortexEnchant::isEnabled)
            .filter(e -> e.getRarity() == rarity)
            .toList();

        if (pool.isEmpty()) {
            player.sendMessage("§cNo enchantments available for " + rarity.getDisplayName() + ".");
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 9,
            PREVIEW_TITLE + rarity.getColor() + rarity.getDisplayName() + " §8✦");

        ItemStack bg = makeItem(Material.BLACK_STAINED_GLASS_PANE, "§8");
        for (int i = 0; i < 9; i++) inv.setItem(i, bg);

        // Show 5 random possible enchants
        List<VortexEnchant> shuffled = new ArrayList<>(pool);
        Collections.shuffle(shuffled);
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        for (int i = 0; i < Math.min(5, shuffled.size()); i++) {
            VortexEnchant enchant = shuffled.get(i);
            int level = rng.nextInt(1, enchant.getMaxLevel() + 1);
            ItemStack book = plugin.getEnchantManager().createEnchantedBook(enchant, level);
            ItemMeta meta = book.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
                lore.add("");
                lore.add("§8§m                              ");
                lore.add("§7§oThis is a preview — you may");
                lore.add("§7§oreceive any " + rarity.getColor() + rarity.getDisplayName() + "§7§o enchant.");
                meta.setLore(lore);
                book.setItemMeta(meta);
            }
            inv.setItem(i + 2, book);
        }

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.5f, 1.5f);
    }

    // ─── Flash Sale System ───────────────────────────────────────────────────

    private void checkFlashSaleRotation() {
        if (System.currentTimeMillis() > flashSaleExpiry) {
            rotateFlashSale();
        }
    }

    private void rotateFlashSale() {
        EnchantRarity[] rarities = EnchantRarity.values();
        flashSaleRarity = rarities[ThreadLocalRandom.current().nextInt(rarities.length)];
        long duration = plugin.getConfig().getLong("enchant-shop.flash-sale-duration", 3600) * 1000L;
        flashSaleExpiry = System.currentTimeMillis() + duration;
    }

    private int getFlashSaleDiscount() {
        return plugin.getConfig().getInt("enchant-shop.flash-sale-discount", 25);
    }

    // ─── Daily Limits ────────────────────────────────────────────────────────

    private void checkDailyReset() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastDailyReset;
        if (elapsed >= 86400000L) { // 24 hours
            dailyPurchases.clear();
            lastDailyReset = now;
        }
    }

    private int getDailyPurchases(Player player, EnchantRarity rarity) {
        Map<EnchantRarity, Integer> counts = dailyPurchases.get(player.getUniqueId());
        return counts != null ? counts.getOrDefault(rarity, 0) : 0;
    }

    private void addDailyPurchases(Player player, EnchantRarity rarity, int amount) {
        dailyPurchases.computeIfAbsent(player.getUniqueId(), k -> new EnumMap<>(EnchantRarity.class))
            .merge(rarity, amount, (a, b) -> a + b);
    }

    private int getDailyLimit(EnchantRarity rarity) {
        return plugin.getConfig().getInt("enchant-shop.daily-limit." + rarity.name(), 0);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private double getBasePrice(EnchantRarity rarity) {
        return plugin.getConfig().getDouble("enchant-shop.prices." + rarity.name(), switch (rarity) {
            case COMMON -> 500;
            case UNCOMMON -> 1500;
            case RARE -> 5000;
            case EPIC -> 15000;
            case LEGENDARY -> 50000;
            case MYTHIC -> 150000;
        });
    }

    private String formatSeconds(long seconds) {
        if (seconds <= 0) return "Expired";
        long hours = seconds / 3600;
        long mins = (seconds % 3600) / 60;
        if (hours > 0) return hours + "h " + mins + "m";
        return mins + "m " + (seconds % 60) + "s";
    }

    private ItemStack makeItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }
}
