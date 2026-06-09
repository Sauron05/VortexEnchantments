package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** TreasureHunter: Higher chance for enchanted books, name tags, etc. */
public class TreasureHunterEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.05, 0.10, 0.15};
    private static final Material[] LOOT = {
        Material.ENCHANTED_BOOK, Material.NAME_TAG, Material.SADDLE,
        Material.NAUTILUS_SHELL, Material.HEART_OF_THE_SEA
    };

    public TreasureHunterEnchant() { super("treasure_hunter", "Treasure Hunter", EnchantRarity.RARE, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (Math.random() < cfgd("chance", CHANCE[level - 1])) {
            Material mat = LOOT[(int) (Math.random() * LOOT.length)];
            player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(mat));
        }
    }

    @Override public String getDescription() { return "Better chance for rare treasure."; }
    @Override public String getDescription(int level) {
        return "§a" + (int)(CHANCE[level - 1] * 100) + "%§7 extra chance for rare treasures on each catch."; }
}
