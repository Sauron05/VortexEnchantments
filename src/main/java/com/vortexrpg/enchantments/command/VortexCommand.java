package com.vortexrpg.enchantments.command;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles all /ve subcommands.
 */
public class VortexCommand implements CommandExecutor, TabCompleter {

    private static final int PAGE_SIZE = 10;
    private final VortexEnchantments plugin;

    public VortexCommand(VortexEnchantments plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        return switch (args[0].toLowerCase()) {
            case "give" -> cmdGive(sender, args);
            case "list" -> cmdList(sender, args);
            case "info" -> cmdInfo(sender, args);
            case "reload" -> cmdReload(sender);
            case "search" -> cmdSearch(sender, args);
            case "aura" -> cmdAura(sender);
            case "scroll" -> cmdScroll(sender, args);
            case "extractor" -> cmdExtractor(sender, args);
            case "forge" -> cmdForge(sender);
            case "admin" -> cmdAdmin(sender);
            case "browse" -> cmdBrowse(sender);
            case "tinkerer" -> cmdTinkerer(sender);
            case "alchemist" -> cmdAlchemist(sender);
            case "souls" -> cmdSouls(sender, args);
            case "dust" -> cmdDust(sender, args);
            case "whitescroll" -> cmdWhiteScroll(sender, args);
            case "blackscroll" -> cmdBlackScroll(sender, args);
            case "shop" -> cmdShop(sender);
            case "slotincreaser" -> cmdSlotIncreaser(sender, args);
            case "slots" -> cmdSlots(sender);
            case "transmog" -> cmdTransmog(sender, args);
            case "randomscroll" -> cmdRandomScroll(sender, args);
            case "holyscroll" -> cmdHolyScroll(sender, args);
            case "combos" -> cmdCombos(sender);
            case "evolve" -> cmdEvolve(sender, args);
            case "particles" -> cmdParticles(sender);
            default -> { sendHelp(sender); yield true; }
        };
    }

    // /ve give <player> <enchantment> <level>
    private boolean cmdGive(CommandSender sender, String[] args) {
        if (!sender.hasPermission("vortex.give")) {
            sender.sendMessage("§cNo permission.");
            return true;
        }
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /ve give <player> <enchantment> <level>");
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) { sender.sendMessage("§cPlayer not found."); return true; }

        VortexEnchant enchant = plugin.getEnchantManager().getById(args[2]);
        if (enchant == null) { sender.sendMessage("§cEnchantment not found: " + args[2]); return true; }

        int level;
        try { level = Integer.parseInt(args[3]); } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid level."); return true;
        }
        level = Math.max(1, Math.min(level, enchant.getMaxLevel()));

        ItemStack book = plugin.getEnchantManager().createEnchantedBook(enchant, level);
        target.getInventory().addItem(book);
        target.sendMessage("§aYou received: " + enchant.getLoreLine(level));
        sender.sendMessage("§aGave " + enchant.getLoreLine(level) + " §ato " + target.getName());
        return true;
    }

    // /ve list [page]
    private boolean cmdList(CommandSender sender, String[] args) {
        List<VortexEnchant> all = plugin.getEnchantManager().getAll();
        int page = 1;
        if (args.length >= 2) {
            try { page = Integer.parseInt(args[1]); } catch (NumberFormatException ignored) {}
        }
        int totalPages = (int) Math.ceil((double) all.size() / PAGE_SIZE);
        page = Math.max(1, Math.min(page, totalPages));
        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, all.size());

        sender.sendMessage("§6§l━━━ VortexEnchantments §7[" + page + "/" + totalPages + "] §6━━━");
        for (int i = start; i < end; i++) {
            VortexEnchant e = all.get(i);
            sender.sendMessage("  " + e.getRarity().getColor() + e.getDisplayName()
                + " §8- §7Lvl 1-" + e.getMaxLevel() + " §8| " + e.getTargets().stream()
                    .map(t -> t.name()).collect(Collectors.joining(", "))
                + " §8[" + e.getRarity().getColor() + e.getRarity().getDisplayName() + "§8]");
        }
        sender.sendMessage("§8Use /ve info <name> for details. Use /ve list <page> to navigate.");
        return true;
    }

    // /ve info <enchantment>
    private boolean cmdInfo(CommandSender sender, String[] args) {
        if (args.length < 2) { sender.sendMessage("§cUsage: /ve info <enchantment>"); return true; }
        VortexEnchant enchant = plugin.getEnchantManager().getById(args[1]);
        if (enchant == null) { sender.sendMessage("§cEnchantment not found: " + args[1]); return true; }

        EnchantRarity r = enchant.getRarity();
        sender.sendMessage("§6§l━━━ " + r.getColor() + enchant.getDisplayName() + " §6§l━━━");
        sender.sendMessage("  §7ID: §f" + enchant.getId());
        sender.sendMessage("  §7Rarity: " + r.getColor() + r.getDisplayName());
        sender.sendMessage("  §7Max Level: §f" + enchant.getMaxLevel());
        sender.sendMessage("  §7Applies to: §f" + enchant.getTargets().stream()
            .map(t -> t.name()).collect(Collectors.joining(", ")));
        sender.sendMessage("  §7Description: §f" + enchant.getDescription());
        for (int i = 1; i <= enchant.getMaxLevel(); i++) {
            sender.sendMessage("  §7Level " + i + ": §f" + enchant.getDescription(i));
        }
        if (!enchant.getConflicts().isEmpty()) {
            sender.sendMessage("  §cConflicts: §f" + String.join(", ", enchant.getConflicts()));
        }
        sender.sendMessage("  §7Status: " + (enchant.isEnabled() ? "§aEnabled" : "§cDisabled"));
        return true;
    }

    // /ve reload
    private boolean cmdReload(CommandSender sender) {
        if (!sender.hasPermission("vortex.reload")) { sender.sendMessage("§cNo permission."); return true; }
        plugin.getConfigManager().loadAll();
        plugin.getEnchantManager().reloadAll();
        sender.sendMessage("§aVortexEnchantments configuration reloaded!");
        return true;
    }

    // /ve search <keyword>
    private boolean cmdSearch(CommandSender sender, String[] args) {
        if (args.length < 2) { sender.sendMessage("§cUsage: /ve search <keyword>"); return true; }
        String query = args[1].toLowerCase();
        List<VortexEnchant> results = plugin.getEnchantManager().getAll().stream()
            .filter(e -> e.getId().contains(query)
                || e.getDisplayName().toLowerCase().contains(query)
                || e.getDescription().toLowerCase().contains(query))
            .toList();

        if (results.isEmpty()) {
            sender.sendMessage("§cNo enchantments found matching: " + args[1]);
        } else {
            sender.sendMessage("§6Search results for '" + args[1] + "':");
            for (VortexEnchant e : results) {
                sender.sendMessage("  " + e.getRarity().getColor() + e.getDisplayName()
                    + " §8(" + e.getId() + ")");
            }
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§lVortexEnchantments Commands:");
        sender.sendMessage("  §e/ve give <player> <enchant> <level> §8- Give enchanted book");
        sender.sendMessage("  §e/ve list [page] §8- List all enchantments");
        sender.sendMessage("  §e/ve info <enchant> §8- Detailed enchantment info");
        sender.sendMessage("  §e/ve search <keyword> §8- Search enchantments");
        sender.sendMessage("  §e/ve browse §8- Open enchantment browser GUI");
        sender.sendMessage("  §e/ve aura §8- Toggle particle aura on/off");
        sender.sendMessage("  §e/ve scroll give <player> [tier] §8- Give a Mystery Scroll");
        sender.sendMessage("  §e/ve extractor give <player> §8- Give an Extractor Scroll");
        sender.sendMessage("  §e/ve forge §8- Open the Enchant Forge (upgrade)");
        sender.sendMessage("  §e/ve tinkerer §8- Salvage enchants for XP + Dust");
        sender.sendMessage("  §e/ve alchemist §8- Combine books/dust to upgrade");
        sender.sendMessage("  §e/ve souls [shop] §8- View souls or open Souls Shop");
        sender.sendMessage("  §e/ve dust give <player> <tier> [amount] §8- Give Enchant Dust");
        sender.sendMessage("  §e/ve whitescroll give <player> [amount] §8- Give White Scroll");
        sender.sendMessage("  §e/ve blackscroll give <player> [amount] §8- Give Black Scroll");
        sender.sendMessage("  §e/ve shop §8- Open the enchant shop (buy with $)");
        sender.sendMessage("  §e/ve slotincreaser give <player> [tier] [amount] §8- Give Slot Increaser (tier 1-3)");
        sender.sendMessage("  §e/ve slots §8- View enchant slot info on held item");
        sender.sendMessage("  §e/ve transmog give <player> [amount] §8- Give Transmog Scroll");
        sender.sendMessage("  §e/ve randomscroll give <player> [amount] §8- Give Randomization Scroll");
        sender.sendMessage("  §e/ve holyscroll give <player> [amount] §8- Give Holy White Scroll");
        sender.sendMessage("  §e/ve combos §8- View enchant combos on held item");
        sender.sendMessage("  §e/ve evolve <enchant> §8- Evolve a max-level enchant");
        sender.sendMessage("  §e/ve evolve info <enchant> §8- View evolution path");
        sender.sendMessage("  §e/ve particles §8- Toggle enchant particles on/off");
        sender.sendMessage("  §e/ve admin §8- Open admin management GUI");
        sender.sendMessage("  §e/ve reload §8- Reload configuration");
    }

    // /ve aura — toggle particle aura
    private boolean cmdAura(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cPlayers only.");
            return true;
        }
        if (!player.hasPermission("vortexenchantments.aura")) {
            player.sendMessage("§cNo permission.");
            return true;
        }
        boolean enabled = plugin.getAuraManager().toggleAura(player);
        player.sendMessage(enabled ? "§a✦ Enchant aura enabled." : "§c✦ Enchant aura disabled.");
        return true;
    }

    // /ve scroll give <player> [tier]
    private boolean cmdScroll(CommandSender sender, String[] args) {
        if (!sender.hasPermission("vortexenchantments.scroll")) {
            sender.sendMessage("§cNo permission.");
            return true;
        }
        if (args.length < 3 || !args[1].equalsIgnoreCase("give")) {
            sender.sendMessage("§cUsage: /ve scroll give <player> [tier]");
            return true;
        }
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) { sender.sendMessage("§cPlayer not found."); return true; }

        EnchantRarity tier = null;
        if (args.length >= 4) {
            try {
                tier = EnchantRarity.valueOf(args[3].toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§cInvalid tier. Options: COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, MYTHIC");
                return true;
            }
        }

        ItemStack scroll = plugin.getMysteryScroll().create(tier);
        target.getInventory().addItem(scroll);
        target.sendMessage("§5✦ §dYou received a Mystery Enchant Scroll!");
        sender.sendMessage("§aGave Mystery Scroll to " + target.getName()
            + (tier != null ? " (tier: " + tier.getDisplayName() + ")" : ""));
        return true;
    }

    // /ve extractor give <player>
    private boolean cmdExtractor(CommandSender sender, String[] args) {
        if (!sender.hasPermission("vortexenchantments.extractor")) {
            sender.sendMessage("§cNo permission.");
            return true;
        }
        if (args.length < 3 || !args[1].equalsIgnoreCase("give")) {
            sender.sendMessage("§cUsage: /ve extractor give <player>");
            return true;
        }
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) { sender.sendMessage("§cPlayer not found."); return true; }

        ItemStack extractor = plugin.getExtractorScroll().create();
        target.getInventory().addItem(extractor);
        target.sendMessage("§c✦ §7You received an Extractor Scroll!");
        sender.sendMessage("§aGave Extractor Scroll to " + target.getName());
        return true;
    }

    // /ve forge — open upgrade GUI
    private boolean cmdForge(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cPlayers only.");
            return true;
        }
        if (!player.hasPermission("vortexenchantments.forge")) {
            player.sendMessage("§cNo permission.");
            return true;
        }
        plugin.getEnchantForgeGUI().open(player);
        return true;
    }

    // /ve admin — open admin GUI
    private boolean cmdAdmin(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cPlayers only.");
            return true;
        }
        if (!player.hasPermission("vortexenchantments.admin")) {
            player.sendMessage("§cNo permission.");
            return true;
        }
        plugin.getAdminGUI().open(player);
        return true;
    }

    // /ve browse — open enchant browser GUI
    private boolean cmdBrowse(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cPlayers only.");
            return true;
        }
        if (!player.hasPermission("vortexenchantments.browse")) {
            player.sendMessage("§cNo permission.");
            return true;
        }
        plugin.getEnchantBrowserGUI().open(player);
        return true;
    }

    // /ve tinkerer — open tinkerer GUI
    private boolean cmdTinkerer(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cPlayers only.");
            return true;
        }
        if (!player.hasPermission("vortexenchantments.tinkerer")) {
            player.sendMessage("§cNo permission.");
            return true;
        }
        plugin.getTinkererGUI().open(player);
        return true;
    }

    // /ve alchemist — open alchemist GUI
    private boolean cmdAlchemist(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cPlayers only.");
            return true;
        }
        if (!player.hasPermission("vortexenchantments.alchemist")) {
            player.sendMessage("§cNo permission.");
            return true;
        }
        plugin.getAlchemistGUI().open(player);
        return true;
    }

    // /ve souls [shop] — view souls or open shop
    private boolean cmdSouls(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cPlayers only.");
            return true;
        }
        if (!player.hasPermission("vortexenchantments.souls")) {
            player.sendMessage("§cNo permission.");
            return true;
        }
        if (args.length >= 2 && args[1].equalsIgnoreCase("shop")) {
            plugin.getSoulsManager().openShop(player);
        } else {
            player.sendMessage("§5§lSouls Balance: §d" + plugin.getSoulsManager().getSouls(player));
            player.sendMessage("§7Use §e/ve souls shop §7to spend souls.");
        }
        return true;
    }

    // /ve dust give <player> <tier> [amount]
    private boolean cmdDust(CommandSender sender, String[] args) {
        if (!sender.hasPermission("vortexenchantments.dust")) {
            sender.sendMessage("§cNo permission.");
            return true;
        }
        if (args.length < 4 || !args[1].equalsIgnoreCase("give")) {
            sender.sendMessage("§cUsage: /ve dust give <player> <tier> [amount]");
            return true;
        }
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) { sender.sendMessage("§cPlayer not found."); return true; }

        EnchantRarity tier;
        try { tier = EnchantRarity.valueOf(args[3].toUpperCase()); }
        catch (IllegalArgumentException e) {
            sender.sendMessage("§cInvalid tier. Options: COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, MYTHIC");
            return true;
        }

        int amount = 1;
        if (args.length >= 5) {
            try { amount = Integer.parseInt(args[4]); } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid amount."); return true;
            }
        }

        ItemStack dust = plugin.getDustItem().create(tier, Math.min(amount, 64));
        target.getInventory().addItem(dust);
        sender.sendMessage("§aGave " + amount + "x " + tier.getColor() + tier.getDisplayName() + " Dust §ato " + target.getName());
        target.sendMessage("§6✦ §7You received " + amount + "x " + tier.getColor() + tier.getDisplayName() + " Dust§7!");
        return true;
    }

    // /ve whitescroll give <player> [amount]
    private boolean cmdWhiteScroll(CommandSender sender, String[] args) {
        if (!sender.hasPermission("vortexenchantments.scrolls")) {
            sender.sendMessage("§cNo permission.");
            return true;
        }
        if (args.length < 3 || !args[1].equalsIgnoreCase("give")) {
            sender.sendMessage("§cUsage: /ve whitescroll give <player> [amount]");
            return true;
        }
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) { sender.sendMessage("§cPlayer not found."); return true; }

        int amount = 1;
        if (args.length >= 4) {
            try { amount = Integer.parseInt(args[3]); } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid amount."); return true;
            }
        }

        target.getInventory().addItem(plugin.getProtectionScrolls().createWhiteScroll(Math.min(amount, 64)));
        sender.sendMessage("§aGave " + amount + "x §f§lWhite Scroll §ato " + target.getName());
        target.sendMessage("§f§l✦ §7You received " + amount + "x §f§lWhite Scroll§7!");
        return true;
    }

    // /ve blackscroll give <player> [amount]
    private boolean cmdBlackScroll(CommandSender sender, String[] args) {
        if (!sender.hasPermission("vortexenchantments.scrolls")) {
            sender.sendMessage("§cNo permission.");
            return true;
        }
        if (args.length < 3 || !args[1].equalsIgnoreCase("give")) {
            sender.sendMessage("§cUsage: /ve blackscroll give <player> [amount]");
            return true;
        }
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) { sender.sendMessage("§cPlayer not found."); return true; }

        int amount = 1;
        if (args.length >= 4) {
            try { amount = Integer.parseInt(args[3]); } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid amount."); return true;
            }
        }

        target.getInventory().addItem(plugin.getProtectionScrolls().createBlackScroll(Math.min(amount, 64)));
        sender.sendMessage("§aGave " + amount + "x §0§lBlack Scroll §ato " + target.getName());
        target.sendMessage("§0§l✦ §7You received " + amount + "x §0§lBlack Scroll§7!");
        return true;
    }

    // /ve shop — open enchant shop
    private boolean cmdShop(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cPlayers only.");
            return true;
        }
        if (!player.hasPermission("vortexenchantments.shop")) {
            player.sendMessage("§cNo permission.");
            return true;
        }
        plugin.getEnchantShopGUI().open(player);
        return true;
    }

    // /ve slotincreaser give <player> [tier] [amount]
    private boolean cmdSlotIncreaser(CommandSender sender, String[] args) {
        if (!sender.hasPermission("vortexenchantments.slotincreaser")) {
            sender.sendMessage("§cNo permission.");
            return true;
        }
        if (args.length < 3 || !args[1].equalsIgnoreCase("give")) {
            sender.sendMessage("§cUsage: /ve slotincreaser give <player> [tier] [amount]");
            return true;
        }
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) { sender.sendMessage("§cPlayer not found."); return true; }

        int tier = 1;
        int amount = 1;
        if (args.length >= 4) {
            try { tier = Integer.parseInt(args[3]); } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid tier. Use 1, 2, or 3."); return true;
            }
            if (tier < 1 || tier > 3) {
                sender.sendMessage("§cTier must be 1, 2, or 3."); return true;
            }
        }
        if (args.length >= 5) {
            try { amount = Integer.parseInt(args[4]); } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid amount."); return true;
            }
        }

        target.getInventory().addItem(plugin.getSlotIncreaser().create(Math.min(amount, 64), tier));
        String tierLabel = switch (tier) { case 1 -> "I"; case 2 -> "II"; case 3 -> "III"; default -> "?"; };
        sender.sendMessage("§aGave " + amount + "x §b§lSlot Increaser " + tierLabel + " §ato " + target.getName());
        target.sendMessage("§b§l✦ §7You received " + amount + "x §b§lSlot Increaser " + tierLabel + "§7!");
        return true;
    }

    // /ve slots — show slot info for held item
    private boolean cmdSlots(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cPlayers only.");
            return true;
        }
        plugin.getEnchantLimitManager().sendSlotInfo(player);
        return true;
    }

    // /ve transmog give <player> [amount]
    private boolean cmdTransmog(CommandSender sender, String[] args) {
        if (!sender.hasPermission("vortexenchantments.transmog")) {
            sender.sendMessage("§cNo permission.");
            return true;
        }
        if (args.length < 3 || !args[1].equalsIgnoreCase("give")) {
            sender.sendMessage("§cUsage: /ve transmog give <player> [amount]");
            return true;
        }
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) { sender.sendMessage("§cPlayer not found."); return true; }

        int amount = 1;
        if (args.length >= 4) {
            try { amount = Integer.parseInt(args[3]); } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid amount."); return true;
            }
        }

        target.getInventory().addItem(plugin.getTransmogScroll().create(Math.min(amount, 64)));
        sender.sendMessage("§aGave " + amount + "x §d§lTransmog Scroll §ato " + target.getName());
        target.sendMessage("§d§l✦ §7You received " + amount + "x §d§lTransmog Scroll§7!");
        return true;
    }

    // /ve randomscroll give <player> [amount]
    private boolean cmdRandomScroll(CommandSender sender, String[] args) {
        if (!sender.hasPermission("vortexenchantments.randomscroll")) {
            sender.sendMessage("§cNo permission.");
            return true;
        }
        if (args.length < 3 || !args[1].equalsIgnoreCase("give")) {
            sender.sendMessage("§cUsage: /ve randomscroll give <player> [amount]");
            return true;
        }
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) { sender.sendMessage("§cPlayer not found."); return true; }

        int amount = 1;
        if (args.length >= 4) {
            try { amount = Integer.parseInt(args[3]); } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid amount."); return true;
            }
        }

        target.getInventory().addItem(plugin.getRandomizationScroll().create(Math.min(amount, 64)));
        sender.sendMessage("§aGave " + amount + "x §6§lRandomization Scroll §ato " + target.getName());
        target.sendMessage("§6§l✦ §7You received " + amount + "x §6§lRandomization Scroll§7!");
        return true;
    }

    // /ve holyscroll give <player> [amount]
    private boolean cmdHolyScroll(CommandSender sender, String[] args) {
        if (!sender.hasPermission("vortexenchantments.holyscroll")) {
            sender.sendMessage("§cNo permission.");
            return true;
        }
        if (args.length < 3 || !args[1].equalsIgnoreCase("give")) {
            sender.sendMessage("§cUsage: /ve holyscroll give <player> [amount]");
            return true;
        }
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) { sender.sendMessage("§cPlayer not found."); return true; }

        int amount = 1;
        if (args.length >= 4) {
            try { amount = Integer.parseInt(args[3]); } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid amount."); return true;
            }
        }

        target.getInventory().addItem(plugin.getHolyWhiteScroll().create(Math.min(amount, 64)));
        sender.sendMessage("§aGave " + amount + "x §e§lHoly White Scroll §ato " + target.getName());
        target.sendMessage("§e§l✦ §7You received " + amount + "x §e§lHoly White Scroll§7!");
        return true;
    }

    // /ve combos — view active combos on held item
    private boolean cmdCombos(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cPlayers only.");
            return true;
        }
        if (!player.hasPermission("vortexenchantments.combos")) {
            player.sendMessage("§cNo permission.");
            return true;
        }
        var combo = plugin.getEnchantComboManager();
        ItemStack held = player.getInventory().getItemInMainHand();
        if (held.getType().isAir()) {
            // List all combos
            player.sendMessage("§6§l━━━ Enchant Combos ━━━");
            for (var c : combo.getAllCombos()) {
                player.sendMessage("  " + c.color() + c.displayName() + " §8- §7" + c.description());
                player.sendMessage("    §8Requires: §f" + String.join(" + ", c.requiredEnchantIds()));
            }
            return true;
        }
        var active = combo.getActiveCombos(held);
        if (active.isEmpty()) {
            player.sendMessage("§7No enchant combos active on this item.");
        } else {
            player.sendMessage("§6§l━━━ Active Combos ━━━");
            for (var c : active) {
                player.sendMessage("  " + c.color() + "✦ " + c.displayName() + " §8- §7" + c.description());
            }
        }
        return true;
    }

    // /ve evolve <enchant> | /ve evolve info <enchant>
    private boolean cmdEvolve(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cPlayers only.");
            return true;
        }
        if (!player.hasPermission("vortexenchantments.evolve")) {
            player.sendMessage("§cNo permission.");
            return true;
        }
        if (args.length < 2) {
            player.sendMessage("§cUsage: /ve evolve <enchant> or /ve evolve info <enchant>");
            return true;
        }
        var evo = plugin.getEnchantEvolutionManager();
        if (args[1].equalsIgnoreCase("info")) {
            if (args.length < 3) {
                player.sendMessage("§cUsage: /ve evolve info <enchant>");
                return true;
            }
            evo.sendEvolutionInfo(player, args[2]);
        } else {
            var result = evo.evolve(player, args[1]);
            if (result != null) {
                player.sendMessage(result.message());
            }
        }
        return true;
    }

    // /ve particles — toggle enchant particles
    private boolean cmdParticles(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cPlayers only.");
            return true;
        }
        if (!player.hasPermission("vortexenchantments.particles")) {
            player.sendMessage("§cNo permission.");
            return true;
        }
        boolean enabled = plugin.getEnchantParticleManager().toggleParticles(player);
        player.sendMessage(enabled ? "§a✦ Enchant particles enabled." : "§c✦ Enchant particles disabled.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filterStart(List.of("give", "list", "info", "reload", "search",
                "aura", "scroll", "extractor", "forge", "admin", "browse",
                "tinkerer", "alchemist", "souls", "dust", "whitescroll", "blackscroll",
                "shop", "slotincreaser", "slots", "transmog", "randomscroll", "holyscroll",
                "combos", "evolve", "particles"), args[0]);
        }
        if (args.length == 2) {
            return switch (args[0].toLowerCase()) {
                case "give" -> Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName).filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
                case "info", "search" -> filterStart(
                    plugin.getEnchantManager().getAll().stream().map(VortexEnchant::getId).toList(), args[1]);
                case "scroll", "extractor", "dust", "whitescroll", "blackscroll",
                    "slotincreaser", "transmog", "randomscroll", "holyscroll" -> filterStart(List.of("give"), args[1]);
                case "souls" -> filterStart(List.of("shop"), args[1]);
                case "evolve" -> filterStart(
                    new ArrayList<>() {{ add("info"); addAll(plugin.getEnchantEvolutionManager().getEvolvableEnchants()); }},
                    args[1]);
                default -> Collections.emptyList();
            };
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give")) {
                return filterStart(
                    plugin.getEnchantManager().getAll().stream().map(VortexEnchant::getId).toList(), args[2]);
            }
            if (args[0].equalsIgnoreCase("scroll") || args[0].equalsIgnoreCase("extractor")
                || args[0].equalsIgnoreCase("whitescroll") || args[0].equalsIgnoreCase("blackscroll")
                || args[0].equalsIgnoreCase("slotincreaser") || args[0].equalsIgnoreCase("transmog")
                || args[0].equalsIgnoreCase("randomscroll") || args[0].equalsIgnoreCase("holyscroll")) {
                return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName).filter(n -> n.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
            }
            if (args[0].equalsIgnoreCase("dust")) {
                return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName).filter(n -> n.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
            }
            if (args[0].equalsIgnoreCase("evolve") && args[1].equalsIgnoreCase("info")) {
                return filterStart(new ArrayList<>(plugin.getEnchantEvolutionManager().getEvolvableEnchants()), args[2]);
            }
        }
        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("give")) {
                VortexEnchant e = plugin.getEnchantManager().getById(args[2]);
                if (e != null) {
                    List<String> levels = new ArrayList<>();
                    for (int i = 1; i <= e.getMaxLevel(); i++) levels.add(String.valueOf(i));
                    return filterStart(levels, args[3]);
                }
            }
            if (args[0].equalsIgnoreCase("scroll") || args[0].equalsIgnoreCase("dust")) {
                return filterStart(Arrays.stream(EnchantRarity.values())
                    .map(Enum::name).toList(), args[3]);
            }
            if (args[0].equalsIgnoreCase("slotincreaser")) {
                return filterStart(List.of("1", "2", "3"), args[3]);
            }
        }
        return Collections.emptyList();
    }

    private List<String> filterStart(List<String> list, String prefix) {
        return list.stream()
            .filter(s -> s.toLowerCase().startsWith(prefix.toLowerCase()))
            .collect(Collectors.toList());
    }
}
