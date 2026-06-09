package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** AbyssalLine: During night, catches are tripled. */
public class AbyssalLineEnchant extends VortexEnchant {

    public AbyssalLineEnchant() { super("abyssal_line", "Abyssal Line", EnchantRarity.EPIC, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof Item caught)) return;
        long time = player.getWorld().getTime();
        if (time >= 13000 && time <= 23000) { // night time
            int extraCount = cfgi("extra_multiplier", level);
            for (int i = 0; i < extraCount; i++) {
                ItemStack extra = caught.getItemStack().clone();
                player.getWorld().dropItemNaturally(player.getLocation(), extra);
            }
        }
    }

    @Override public String getDescription() { return "Night fishing yields bonus catches."; }
    @Override public String getDescription(int level) {
        return "§7At night, each catch drops §a" + level + "§7 extra copies."; }
}
