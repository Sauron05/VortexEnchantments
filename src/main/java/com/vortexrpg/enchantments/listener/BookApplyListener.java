package com.vortexrpg.enchantments.listener;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.SuccessRateManager;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.item.DustItem;
import com.vortexrpg.enchantments.item.HolyWhiteScroll;
import com.vortexrpg.enchantments.item.ProtectionScrolls;
import com.vortexrpg.enchantments.item.RandomizationScroll;
import com.vortexrpg.enchantments.item.SlotIncreaser;
import com.vortexrpg.enchantments.item.TransmogScroll;
import com.vortexrpg.enchantments.system.EnchantLimitManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

/**
 * Handles drag-and-drop interactions in player inventory:
 * - Enchanted book onto item → apply with success/destroy rates
 * - Dust onto enchanted book → add success bonus
 * - White Scroll onto item → add protection
 * - Black Scroll onto item → remove random enchant, return as book
 */
@SuppressWarnings("deprecation")
public class BookApplyListener implements Listener {

    private final VortexEnchantments plugin;
    private final NamespacedKey dustBonusKey;

    public BookApplyListener(VortexEnchantments plugin) {
        this.plugin = plugin;
        this.dustBonusKey = new NamespacedKey(plugin, "dust_bonus");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        // Only handle cursor-on-slot clicks (drag-and-drop)
        ItemStack cursor = event.getCursor();
        ItemStack target = event.getCurrentItem();
        if (cursor == null || cursor.getType() == Material.AIR) return;
        if (target == null || target.getType() == Material.AIR) return;

        // Must be in player inventory, not in a named GUI
        if (event.getView().getType() != InventoryType.CRAFTING) return;

        DustItem dustItem = plugin.getDustItem();
        ProtectionScrolls scrolls = plugin.getProtectionScrolls();
        SlotIncreaser slotIncreaser = plugin.getSlotIncreaser();
        TransmogScroll transmogScroll = plugin.getTransmogScroll();
        RandomizationScroll randomizationScroll = plugin.getRandomizationScroll();
        HolyWhiteScroll holyWhiteScroll = plugin.getHolyWhiteScroll();
        EnchantLimitManager limitManager = plugin.getEnchantLimitManager();

        // ─── Dust onto Enchanted Book ────────────────────────────────────────
        if (dustItem.isDust(cursor) && target.getType() == Material.ENCHANTED_BOOK) {
            Map<VortexEnchant, Integer> bookEnchants = plugin.getEnchantManager().getEnchants(target);
            if (bookEnchants.isEmpty()) return;

            event.setCancelled(true);

            EnchantRarity dustTier = dustItem.getTier(cursor);
            int bonus = dustItem.getBonus(dustTier);

            // Get existing bonus
            var meta = target.getItemMeta();
            if (meta == null) return;
            int existing = 0;
            if (meta.getPersistentDataContainer().has(dustBonusKey, PersistentDataType.INTEGER)) {
                existing = meta.getPersistentDataContainer().get(dustBonusKey, PersistentDataType.INTEGER);
            }
            int newBonus = Math.min(existing + bonus, 100);
            meta.getPersistentDataContainer().set(dustBonusKey, PersistentDataType.INTEGER, newBonus);

            // Update lore to show bonus
            List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
            lore.removeIf(line -> line.contains("Success Boost"));
            lore.add("§a✦ Success Boost: +" + newBonus + "%");
            meta.setLore(lore);
            target.setItemMeta(meta);

            // Consume one dust
            if (cursor.getAmount() > 1) {
                cursor.setAmount(cursor.getAmount() - 1);
            } else {
                event.getView().setCursor(null);
            }

            player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.7f, 1.5f);
            player.sendMessage("§a✦ Applied " + dustTier.getColor() + dustTier.getDisplayName() + " Dust§a! Success boost: §e+" + newBonus + "%");
            return;
        }

        // ─── White Scroll onto item ──────────────────────────────────────────
        if (scrolls.isWhiteScroll(cursor) && target.getType() != Material.ENCHANTED_BOOK) {
            if (scrolls.isProtected(target)) {
                player.sendMessage("§cItem is already protected by a White Scroll!");
                return;
            }
            event.setCancelled(true);
            scrolls.applyProtection(target);

            if (cursor.getAmount() > 1) {
                cursor.setAmount(cursor.getAmount() - 1);
            } else {
                event.getView().setCursor(null);
            }

            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.7f, 1.5f);
            player.sendMessage("§f§l✦ White Scroll§f applied! Your item is now protected.");
            return;
        }

        // ─── Black Scroll onto item ──────────────────────────────────────────
        if (scrolls.isBlackScroll(cursor) && target.getType() != Material.ENCHANTED_BOOK
            && target.getType() != Material.PAPER) {
            Map<VortexEnchant, Integer> enchants = plugin.getEnchantManager().getEnchants(target);
            if (enchants.isEmpty()) {
                player.sendMessage("§cThat item has no VortexEnchantments.");
                return;
            }

            event.setCancelled(true);

            // Remove a random enchant
            List<Map.Entry<VortexEnchant, Integer>> enchantList = new ArrayList<>(enchants.entrySet());
            Map.Entry<VortexEnchant, Integer> removed = enchantList.get(
                java.util.concurrent.ThreadLocalRandom.current().nextInt(enchantList.size()));

            plugin.getEnchantManager().removeEnchant(target, removed.getKey());

            // Give the enchant as a book
            ItemStack book = plugin.getEnchantManager().createEnchantedBook(removed.getKey(), removed.getValue());
            HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(book);
            overflow.values().forEach(i -> player.getWorld().dropItemNaturally(player.getLocation(), i));

            // Consume black scroll
            if (cursor.getAmount() > 1) {
                cursor.setAmount(cursor.getAmount() - 1);
            } else {
                event.getView().setCursor(null);
            }

            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 0.5f, 1.2f);
            player.sendMessage("§0§l✦ Black Scroll§7 removed: " + removed.getKey().getLoreLine(removed.getValue()));
            return;
        }

        // ─── Slot Increaser onto item ────────────────────────────────────────
        if (slotIncreaser.isSlotIncreaser(cursor) && target.getType() != Material.ENCHANTED_BOOK
            && target.getType() != Material.PAPER) {
            event.setCancelled(true);
            boolean applied = slotIncreaser.applySlotIncrease(target);
            if (!applied) {
                player.sendMessage("§cThis item has reached the maximum slot bonus!");
                return;
            }

            if (cursor.getAmount() > 1) {
                cursor.setAmount(cursor.getAmount() - 1);
            } else {
                event.getView().setCursor(null);
            }

            int bonus = slotIncreaser.getSlotBonus(target);
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 0.8f, 1.5f);
            player.sendMessage("§b§l✦ Slot Increaser§b applied! Enchant slots: §e+" + bonus);
            return;
        }

        // ─── Transmog Scroll onto item ───────────────────────────────────────
        if (transmogScroll.isTransmogScroll(cursor) && target.getType() != Material.ENCHANTED_BOOK
            && target.getType() != Material.PAPER) {
            Map<VortexEnchant, Integer> enchants = plugin.getEnchantManager().getEnchants(target);
            if (enchants.isEmpty()) {
                player.sendMessage("§cThat item has no VortexEnchantments to organize.");
                return;
            }

            event.setCancelled(true);
            transmogScroll.applyTransmog(target, player.getName());

            if (cursor.getAmount() > 1) {
                cursor.setAmount(cursor.getAmount() - 1);
            } else {
                event.getView().setCursor(null);
            }

            player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.8f, 1.8f);
            player.sendMessage("§d§l✦ Transmog Scroll§d applied! Enchantments sorted and organized.");
            return;
        }

        // ─── Randomization Scroll onto item ──────────────────────────────────
        if (randomizationScroll.isRandomizationScroll(cursor) && target.getType() != Material.ENCHANTED_BOOK
            && target.getType() != Material.PAPER) {
            Map<VortexEnchant, Integer> enchants = plugin.getEnchantManager().getEnchants(target);
            if (enchants.isEmpty()) {
                player.sendMessage("§cThat item has no VortexEnchantments to re-roll.");
                return;
            }

            event.setCancelled(true);
            RandomizationScroll.RandomResult randResult = randomizationScroll.applyRandomization(target);

            if (randResult == null) {
                player.sendMessage("§cRandomization failed!");
                return;
            }

            if (cursor.getAmount() > 1) {
                cursor.setAmount(cursor.getAmount() - 1);
            } else {
                event.getView().setCursor(null);
            }

            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 1.5f);
            player.sendMessage("§6§l✦ Randomization Scroll§6 applied! §7(Re-roll #" + randResult.totalRerolls() + ")");
            player.sendMessage("§7Before → After:");
            int count = Math.max(randResult.before().size(), randResult.after().size());
            for (int idx = 0; idx < count; idx++) {
                String b = idx < randResult.before().size() ? randResult.before().get(idx) : "§8(none)";
                String a = idx < randResult.after().size() ? randResult.after().get(idx) : "§8(none)";
                player.sendMessage("  " + b + " §8→ " + a);
            }
            if (randResult.upgrades() > 0) {
                player.sendMessage("  §a▲ " + randResult.upgrades() + " upgraded!");
            }
            if (randResult.downgrades() > 0) {
                player.sendMessage("  §c▼ " + randResult.downgrades() + " downgraded");
            }
            return;
        }

        // ─── Holy White Scroll onto item ─────────────────────────────────────
        if (holyWhiteScroll.isHolyWhiteScroll(cursor) && target.getType() != Material.ENCHANTED_BOOK
            && target.getType() != Material.PAPER) {
            if (holyWhiteScroll.isProtected(target)) {
                player.sendMessage("§cItem is already protected by a Holy White Scroll!");
                return;
            }
            event.setCancelled(true);
            holyWhiteScroll.applyProtection(target);

            if (cursor.getAmount() > 1) {
                cursor.setAmount(cursor.getAmount() - 1);
            } else {
                event.getView().setCursor(null);
            }

            player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.8f, 1.5f);
            player.sendMessage("§e§l✦ Holy White Scroll§e applied! Permanent protection granted.");
            return;
        }

        // ─── Enchanted Book onto item (apply with success/destroy rates) ─────
        if (cursor.getType() == Material.ENCHANTED_BOOK && target.getType() != Material.ENCHANTED_BOOK
            && target.getType() != Material.PAPER) {
            Map<VortexEnchant, Integer> bookEnchants = plugin.getEnchantManager().getEnchants(cursor);
            if (bookEnchants.isEmpty()) return;

            // Check if any enchant is compatible
            boolean anyCompatible = false;
            for (VortexEnchant enchant : bookEnchants.keySet()) {
                if (enchant.getTargets().stream().anyMatch(t -> t.matches(target.getType()))
                    && !plugin.getEnchantManager().wouldConflict(target, enchant)) {
                    anyCompatible = true;
                    break;
                }
            }
            if (!anyCompatible) return;

            // Check enchant slot limits
            if (!limitManager.canApplyMore(player, target)) {
                player.sendMessage("§c✦ This item has reached its maximum enchantment slots!");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.7f, 0.5f);
                return;
            }

            event.setCancelled(true);

            // Get dust bonus from book
            int dustBonus = 0;
            var cursorMeta = cursor.getItemMeta();
            if (cursorMeta != null && cursorMeta.getPersistentDataContainer().has(dustBonusKey, PersistentDataType.INTEGER)) {
                dustBonus = cursorMeta.getPersistentDataContainer().get(dustBonusKey, PersistentDataType.INTEGER);
            }

            // Process each enchant on the book
            boolean anySuccess = false;
            boolean destroyed = false;
            SuccessRateManager srm = plugin.getSuccessRateManager();

            for (Map.Entry<VortexEnchant, Integer> entry : bookEnchants.entrySet()) {
                VortexEnchant enchant = entry.getKey();
                int level = entry.getValue();

                if (!enchant.getTargets().stream().anyMatch(t -> t.matches(target.getType()))) continue;
                if (plugin.getEnchantManager().wouldConflict(target, enchant)) continue;
                if (!limitManager.canUseRarity(player, enchant.getRarity())) {
                    player.sendMessage("§c✦ You don't have permission to use " + enchant.getRarity().getColor() + enchant.getRarity().getDisplayName() + " §cenchantments.");
                    continue;
                }

                SuccessRateManager.ApplyResult result = srm.rollApplication(enchant.getRarity(), dustBonus);

                switch (result) {
                    case SUCCESS -> {
                        int currentLevel = plugin.getEnchantManager().getLevel(target, enchant);
                        int newLevel;
                        if (currentLevel == level) {
                            newLevel = Math.min(currentLevel + 1, enchant.getMaxLevel());
                        } else {
                            newLevel = Math.max(currentLevel, level);
                        }
                        plugin.getEnchantManager().applyEnchant(target, enchant, newLevel);
                        anySuccess = true;
                        player.sendMessage("§a✦ Success! §7Applied " + enchant.getLoreLine(newLevel));
                    }
                    case FAIL -> {
                        player.sendMessage("§e✦ Failed! §7" + enchant.getDisplayName() + " was not applied.");
                    }
                    case DESTROY -> {
                        destroyed = true;
                    }
                }
            }

            // Consume book
            event.getView().setCursor(null);

            if (destroyed) {
                // Check holy white scroll protection (permanent, not consumed)
                if (holyWhiteScroll.isProtected(target)) {
                    holyWhiteScroll.recordSave(target);
                    player.sendMessage("§e§l✦ Holy White Scroll §7saved your item from destruction! (Permanent)");
                    player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.7f, 1.2f);
                // Check white scroll protection (consumed on use)
                } else if (plugin.getProtectionScrolls().consumeProtection(target)) {
                    player.sendMessage("§f§l✦ White Scroll §7saved your item from destruction!");
                    player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.7f, 1.2f);
                } else {
                    event.setCurrentItem(null);
                    player.sendMessage("§c§l✦ ITEM DESTROYED! §7The enchantment overwhelmed your item.");
                    player.playSound(player.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 1f, 0.5f);
                }
            } else if (anySuccess) {
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1.2f);
            } else {
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.7f, 0.5f);
            }
        }
    }
}
