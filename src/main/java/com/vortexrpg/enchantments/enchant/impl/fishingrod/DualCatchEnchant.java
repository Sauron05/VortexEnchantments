package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** DualCatch: Chance to roll loot table twice on catch. */
public class DualCatchEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.10, 0.15, 0.20};

    public DualCatchEnchant() { super("dual_catch", "Dual Catch", EnchantRarity.RARE, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof Item caught)) return;
        if (Math.random() < cfgd("chance", CHANCE[level - 1])) {
            ItemStack extra = caught.getItemStack().clone();
            player.getWorld().dropItemNaturally(event.getHook().getLocation(), extra);
        }
    }

    @Override public String getDescription() { return "Chance for double loot on catch."; }
    @Override public String getDescription(int level) {
        return "§a" + (int)(CHANCE[level - 1] * 100) + "%§7 chance for a second loot drop."; }
}
