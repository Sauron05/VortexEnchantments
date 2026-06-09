package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** DeepSeaKing: All fishing loot is doubled. */
public class DeepSeaKingEnchant extends VortexEnchant {

    public DeepSeaKingEnchant() { super("deep_sea_king", "Deep Sea King", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof Item caught)) return;
        // Double the loot
        ItemStack extra = caught.getItemStack().clone();
        player.getWorld().dropItemNaturally(player.getLocation(), extra);
        // Bonus XP too
        event.setExpToDrop((int) (event.getExpToDrop() * cfgd("xp_multiplier", 1.5 + level * 0.5)));
    }

    @Override public String getDescription() { return "All fishing loot is doubled."; }
    @Override public String getDescription(int level) {
        return "§dEvery catch is doubled§7 + §a" + (int)((1.5 + level * 0.5) * 100) + "%§7 XP."; }
}
