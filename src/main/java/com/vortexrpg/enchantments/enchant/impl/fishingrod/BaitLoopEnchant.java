package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** BaitLoop: Each consecutive catch without moving boosts next catch quantity. */
public class BaitLoopEnchant extends VortexEnchant {

    public BaitLoopEnchant() { super("bait_loop", "Bait Loop", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof Item caught)) return;
        int stacks = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "baitloop_s", 0);
        int maxStacks = cfgi("max_stacks", 3 + level);
        if (stacks > 0) {
            ItemStack bonus = caught.getItemStack().clone();
            bonus.setAmount(Math.min(stacks, maxStacks));
            player.getWorld().dropItemNaturally(player.getLocation(), bonus);
        }
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "baitloop_s", Math.min(stacks + 1, maxStacks));
    }

    @Override public String getDescription() { return "Consecutive catches stack bonus drops."; }
    @Override public String getDescription(int level) {
        return "§7Each catch stacks §a+1§7 bonus item (max §e" + (3 + level) + "§7 stacks)."; }
}
