package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Archive (helmet): Tracks unique biomes visited; every 5 new biomes, give 1 exp book. */
@SuppressWarnings("removal")
public class ArchiveHelmetEnchant extends VortexEnchant {
    public ArchiveHelmetEnchant() { super("archive_helmet", "Archive", EnchantRarity.EPIC, 1, List.of(ItemTarget.HELMET)); }

    @Override
    public void onMove(PlayerMoveEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.hasChangedBlock()) return;
        String biome = player.getLocation().getBlock().getBiome().name();
        if (plugin.getPlayerDataManager().hasVisitedBiome(player.getUniqueId(), biome)) return;
        plugin.getPlayerDataManager().markBiomeVisited(player.getUniqueId(), biome);
        int total = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "archive_helmet_count") + 1;
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "archive_helmet_count", total);
        if (total % 5 == 0) {
            player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.EXPERIENCE_BOTTLE));
            player.sendMessage("§b[Archive] §7Discovered " + total + " biomes! Here's a reward.");
        }
    }

    @Override public String getDescription() { return "Tracks biomes; rewards on milestones."; }
    @Override public String getDescription(int level) { return "§7Every 5 new biomes yields an exp bottle."; }
}
