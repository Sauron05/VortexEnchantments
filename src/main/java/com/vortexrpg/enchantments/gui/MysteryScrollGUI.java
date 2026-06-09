package com.vortexrpg.enchantments.gui;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Animated spinning GUI for Mystery Scrolls.
 * Shows a "slot machine" style animation then reveals the won enchant.
 */
@SuppressWarnings("deprecation")
public class MysteryScrollGUI implements Listener {

    private static final String GUI_TITLE = "§5§l✦ Mystery Scroll ✦";
    private static final int SIZE = 27; // 3 rows
    // Spin row: slots 9-17 (middle row)
    private static final int SPIN_START = 9;

    private final VortexEnchantments plugin;
    private final Set<UUID> spinningPlayers = ConcurrentHashMap.newKeySet();

    public MysteryScrollGUI(VortexEnchantments plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, EnchantRarity fixedTier) {
        if (spinningPlayers.contains(player.getUniqueId())) return;

        Inventory inv = Bukkit.createInventory(null, SIZE, GUI_TITLE);

        // Fill border with glass
        ItemStack border = makeItem(Material.BLACK_STAINED_GLASS_PANE, "§8");
        for (int i = 0; i < SIZE; i++) {
            inv.setItem(i, border);
        }

        // Arrow pointers above and below center
        inv.setItem(4, makeItem(Material.YELLOW_STAINED_GLASS_PANE, "§e▼"));
        inv.setItem(22, makeItem(Material.YELLOW_STAINED_GLASS_PANE, "§e▲"));

        player.openInventory(inv);
        spinningPlayers.add(player.getUniqueId());

        startSpinAnimation(player, inv, fixedTier);
    }

    private void startSpinAnimation(Player player, Inventory inv, EnchantRarity fixedTier) {
        // Pre-generate the result
        VortexEnchant result = pickRandomEnchant(fixedTier);
        if (result == null) {
            player.sendMessage("§cNo enchantments available!");
            player.closeInventory();
            spinningPlayers.remove(player.getUniqueId());
            return;
        }
        int resultLevel = 1 + ThreadLocalRandom.current().nextInt(result.getMaxLevel());

        // Generate a long strip of random enchant items with the result placed at the end
        List<ItemStack> strip = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            VortexEnchant random = pickRandomEnchant(null);
            if (random != null) {
                strip.add(makeEnchantDisplay(random, 1 + ThreadLocalRandom.current().nextInt(random.getMaxLevel())));
            } else {
                strip.add(makeItem(Material.ENCHANTED_BOOK, "§7???"));
            }
        }
        // Place the real result at position 35 (it will land on center)
        strip.set(35, makeEnchantDisplay(result, resultLevel));

        new BukkitRunnable() {
            int offset = 0;
            int tickDelay = 1; // start fast
            int tickCounter = 0;

            @Override
            public void run() {
                if (!player.isOnline() || !spinningPlayers.contains(player.getUniqueId())) {
                    cancel();
                    spinningPlayers.remove(player.getUniqueId());
                    return;
                }

                tickCounter++;
                if (tickCounter < tickDelay) return;
                tickCounter = 0;

                // Fill the spin row from the strip
                for (int i = 0; i < 9; i++) {
                    int stripIdx = (offset + i) % strip.size();
                    inv.setItem(SPIN_START + i, strip.get(stripIdx));
                }
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 0.5f, 1.0f + offset * 0.02f);

                offset++;

                // Slow down as we approach the end
                if (offset > 25) tickDelay = 2;
                if (offset > 30) tickDelay = 3;
                if (offset > 33) tickDelay = 5;
                if (offset > 34) tickDelay = 8;

                // Stop when result is centered (offset = 31 means strip[35] is at slot 13)
                if (offset >= 32) {
                    // Final arrangement: result at center
                    for (int i = 0; i < 9; i++) {
                        int stripIdx = (31 + i) % strip.size();
                        inv.setItem(SPIN_START + i, strip.get(stripIdx));
                    }

                    // Celebration
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
                    if (result.getRarity().ordinal() >= EnchantRarity.EPIC.ordinal()) {
                        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8f, 1.0f);
                    }

                    // Give the book after a short delay
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        ItemStack book = plugin.getEnchantManager().createEnchantedBook(result, resultLevel);
                        player.getInventory().addItem(book);
                        player.sendMessage("§5✦ §dYou received: " + result.getLoreLine(resultLevel)
                            + " §8[" + result.getRarity().getColor() + result.getRarity().getDisplayName() + "§8] §5✦");
                        spinningPlayers.remove(player.getUniqueId());
                    }, 30L);

                    cancel();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    private VortexEnchant pickRandomEnchant(EnchantRarity fixedTier) {
        List<VortexEnchant> pool = new ArrayList<>();

        if (fixedTier != null) {
            for (VortexEnchant e : plugin.getEnchantManager().getAll()) {
                if (e.isEnabled() && e.getRarity() == fixedTier) pool.add(e);
            }
        } else {
            // Weighted random by rarity
            for (VortexEnchant e : plugin.getEnchantManager().getAll()) {
                if (!e.isEnabled()) continue;
                // Add multiple times based on weight for weighted selection
                pool.add(e);
            }
        }

        if (pool.isEmpty()) return null;

        if (fixedTier != null) {
            return pool.get(ThreadLocalRandom.current().nextInt(pool.size()));
        }

        // Weighted selection by rarity
        int totalWeight = 0;
        for (VortexEnchant e : pool) totalWeight += e.getRarity().getDefaultWeight();
        if (totalWeight <= 0) return pool.get(0);

        int roll = ThreadLocalRandom.current().nextInt(totalWeight);
        int cumulative = 0;
        for (VortexEnchant e : pool) {
            cumulative += e.getRarity().getDefaultWeight();
            if (roll < cumulative) return e;
        }
        return pool.get(pool.size() - 1);
    }

    private ItemStack makeEnchantDisplay(VortexEnchant enchant, int level) {
        Material mat = switch (enchant.getRarity()) {
            case COMMON -> Material.WHITE_STAINED_GLASS_PANE;
            case UNCOMMON -> Material.LIME_STAINED_GLASS_PANE;
            case RARE -> Material.BLUE_STAINED_GLASS_PANE;
            case EPIC -> Material.PURPLE_STAINED_GLASS_PANE;
            case LEGENDARY -> Material.ORANGE_STAINED_GLASS_PANE;
            case MYTHIC -> Material.RED_STAINED_GLASS_PANE;
        };

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(enchant.getLoreLine(level));
        meta.setLore(List.of(
            enchant.getRarity().getColor() + "§l" + enchant.getRarity().getDisplayName(),
            "§7" + enchant.getDescription(level)
        ));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;
        event.setCancelled(true); // No clicking during spin
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;
        // If they close mid-spin, we'll let the task handle cleanup
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
