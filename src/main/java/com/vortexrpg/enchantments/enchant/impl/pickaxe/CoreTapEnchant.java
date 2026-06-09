package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/** CoreTap: Every 80/60/40 blocks mined triggers guaranteed 1 diamond + 2 lapis drop. */
public class CoreTapEnchant extends VortexEnchant {
    private static final int[] THRESHOLD = {80, 60, 40};

    public CoreTapEnchant() { super("coretap", "Core Tap", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        UUID uuid = player.getUniqueId();
        int threshold = cfgi("blocks_threshold_" + level, THRESHOLD[level-1]);
        int count = plugin.getPlayerDataManager().getInt(uuid, "coretap_count") + 1;
        if (count >= threshold) {
            plugin.getPlayerDataManager().setInt(uuid, "coretap_count", 0);
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.DIAMOND));
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.LAPIS_LAZULI, 2));
        } else {
            plugin.getPlayerDataManager().setInt(uuid, "coretap_count", count);
        }
    }

    @Override public String getDescription() { return "Every N blocks mined yields bonus diamonds and lapis."; }
    @Override public String getDescription(int level) {
        return "§7Every §e" + THRESHOLD[level-1] + " blocks§7 mined: §b1 diamond§7 + §92 lapis§7."; }
}
