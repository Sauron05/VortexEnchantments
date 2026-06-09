package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** FathomReel: Chance to catch rare deep-sea loot. */
public class FathomReelEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.03, 0.05, 0.07};
    private static final Material[] DEEP_LOOT = {
        Material.DIAMOND, Material.HEART_OF_THE_SEA, Material.NAUTILUS_SHELL,
        Material.EMERALD, Material.TRIDENT
    };

    public FathomReelEnchant() { super("fathom_reel", "Fathom Reel", EnchantRarity.EPIC, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (Math.random() < cfgd("chance", CHANCE[level - 1])) {
            Material mat = DEEP_LOOT[(int) (Math.random() * DEEP_LOOT.length)];
            player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(mat));
        }
    }

    @Override public String getDescription() { return "Chance to catch rare deep-sea loot."; }
    @Override public String getDescription(int level) {
        return "§a" + (int)(CHANCE[level - 1] * 100) + "%§7 chance for §ddeep-sea treasures§7 (diamond, heart of the sea, etc)."; }
}
