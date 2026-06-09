package com.vortexrpg.enchantments.system;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Souls — virtual PvP currency earned from player kills, spent on enchant books.
 * Souls are stored per-player in memory (and optionally in PlayerDataManager for persistence).
 */
@SuppressWarnings("deprecation")
public class SoulsManager implements Listener {

    private final VortexEnchantments plugin;
    private final Map<UUID, Integer> soulsBalance = new ConcurrentHashMap<>();

    // Configurable soul rewards
    private static final int BASE_SOULS_PER_KILL = 5;
    private static final int STREAK_BONUS = 2;

    public SoulsManager(VortexEnchantments plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!plugin.getConfig().getBoolean("souls.enabled", true)) return;
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null) return;
        if (killer.equals(victim)) return; // No self-kill souls

        int baseSouls = plugin.getConfig().getInt("souls.per-kill", BASE_SOULS_PER_KILL);
        int bonus = plugin.getConfig().getInt("souls.streak-bonus", STREAK_BONUS);

        // Simple kill streak — more souls for consecutive kills
        int streak = getKillStreak(killer);
        int totalSouls = baseSouls + (streak * bonus);

        addSouls(killer, totalSouls);
        killer.sendMessage("§5✦ §d+" + totalSouls + " Souls §7(Kill: " + victim.getName() + ")");
        killer.playSound(killer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.5f);

        // Victim loses some souls
        int lostSouls = Math.min(getSouls(victim), totalSouls / 2);
        if (lostSouls > 0) {
            removeSouls(victim, lostSouls);
            victim.sendMessage("§c✦ -" + lostSouls + " Souls §7(Killed by: " + killer.getName() + ")");
        }
    }

    // ─── Balance operations ──────────────────────────────────────────────────

    public int getSouls(Player player) { return soulsBalance.getOrDefault(player.getUniqueId(), 0); }
    public void setSouls(Player player, int amount) { soulsBalance.put(player.getUniqueId(), Math.max(0, amount)); }
    public void addSouls(Player player, int amount) { setSouls(player, getSouls(player) + amount); }
    public boolean removeSouls(Player player, int amount) {
        if (getSouls(player) < amount) return false;
        setSouls(player, getSouls(player) - amount);
        return true;
    }
    public boolean hasSouls(Player player, int amount) { return getSouls(player) >= amount; }

    // ─── Souls Shop GUI ─────────────────────────────────────────────────────

    public void openShop(Player player) {
        int size = 54;
        String title = "§5§lSouls Shop §8(Balance: §d" + getSouls(player) + "§8)";
        var inv = Bukkit.createInventory(null, size, title);

        // Border
        ItemStack border = makeItem(Material.PURPLE_STAINED_GLASS_PANE, "§8");
        for (int i = 0; i < size; i++) inv.setItem(i, border);

        // Header
        inv.setItem(4, makeItem(Material.NETHER_STAR, "§5§lSouls Shop",
            "§7Spend your souls on enchant books.",
            "§7Balance: §d" + getSouls(player) + " Souls"));

        // Tier items — each tier has a slot for buying a random book of that rarity
        int slot = 19;
        for (EnchantRarity rarity : EnchantRarity.values()) {
            int cost = getSoulsCost(rarity);
            Material mat = switch (rarity) {
                case COMMON -> Material.WHITE_DYE;
                case UNCOMMON -> Material.LIME_DYE;
                case RARE -> Material.BLUE_DYE;
                case EPIC -> Material.PURPLE_DYE;
                case LEGENDARY -> Material.ORANGE_DYE;
                case MYTHIC -> Material.RED_DYE;
            };

            inv.setItem(slot, makeItem(mat, rarity.getColor() + "§l" + rarity.getDisplayName() + " Book",
                "§7Buy a random " + rarity.getColor() + rarity.getDisplayName() + " §7enchant book.",
                "",
                "§7Cost: §d" + cost + " Souls",
                "",
                hasSouls(player, cost) ? "§aClick to purchase!" : "§cInsufficient souls!"));
            slot++;
        }

        player.openInventory(inv);
    }

    /** Handle shop clicks (called from a GUI listener). */
    public boolean handleShopClick(Player player, int slot) {
        if (slot < 19 || slot > 24) return false;
        int rarityIndex = slot - 19;
        if (rarityIndex >= EnchantRarity.values().length) return false;

        EnchantRarity rarity = EnchantRarity.values()[rarityIndex];
        int cost = getSoulsCost(rarity);

        if (!hasSouls(player, cost)) {
            player.sendMessage("§cYou need §d" + cost + " Souls §cbut only have §d" + getSouls(player) + "§c.");
            return true;
        }

        // Random enchant of this rarity
        List<VortexEnchant> eligible = plugin.getEnchantManager().getAll().stream()
            .filter(VortexEnchant::isEnabled)
            .filter(e -> e.getRarity() == rarity)
            .toList();

        if (eligible.isEmpty()) {
            player.sendMessage("§cNo enchantments available for this rarity.");
            return true;
        }

        VortexEnchant chosen = eligible.get(ThreadLocalRandom.current().nextInt(eligible.size()));
        int level = Math.max(1, ThreadLocalRandom.current().nextInt(1, chosen.getMaxLevel() + 1));
        ItemStack book = plugin.getEnchantManager().createEnchantedBook(chosen, level);

        removeSouls(player, cost);
        HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(book);
        overflow.values().forEach(i -> player.getWorld().dropItemNaturally(player.getLocation(), i));

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.2f);
        player.sendMessage("§5§l✦ Souls Shop §8» §7Purchased: " + chosen.getLoreLine(level) + " §7for §d" + cost + " Souls§7.");

        // Refresh shop
        SchedulerUtil.runEntityTask(plugin, player, () -> openShop(player));
        return true;
    }

    private int getSoulsCost(EnchantRarity rarity) {
        return plugin.getConfig().getInt("souls.cost." + rarity.name().toLowerCase(),
            switch (rarity) {
                case COMMON -> 10;
                case UNCOMMON -> 25;
                case RARE -> 50;
                case EPIC -> 100;
                case LEGENDARY -> 250;
                case MYTHIC -> 500;
            });
    }

    private int getKillStreak(Player player) {
        // Simple implementation — could be expanded with time-based streak tracking
        return 0;
    }

    public Map<UUID, Integer> getAllBalances() { return Collections.unmodifiableMap(soulsBalance); }

    public void loadBalance(UUID uuid, int amount) { soulsBalance.put(uuid, amount); }

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
