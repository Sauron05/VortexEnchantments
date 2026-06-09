package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** MidasReel: Every Nth catch is transmuted into gold. */
public class MidasReelEnchant extends VortexEnchant {

    public MidasReelEnchant() { super("midas_reel", "Midas Reel", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        int count = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "midas_count", 0) + 1;
        int interval = cfgi("interval", Math.max(2, 6 - level));
        if (count >= interval) {
            count = 0;
            Material gold = level >= 3 ? Material.GOLD_BLOCK : Material.GOLD_INGOT;
            int amount = cfgi("gold_amount", level);
            player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(gold, amount));
        }
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "midas_count", count);
    }

    @Override public String getDescription() { return "Periodic catches turn to gold."; }
    @Override public String getDescription(int level) {
        return "§7Every §e" + Math.max(2, 6 - level) + "§7th catch becomes §6gold§7."; }
}
