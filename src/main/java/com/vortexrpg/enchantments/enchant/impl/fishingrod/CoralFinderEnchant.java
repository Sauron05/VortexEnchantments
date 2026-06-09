package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** CoralFinder: Chance to catch coral and sea items alongside fish. */
public class CoralFinderEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.05, 0.10, 0.15};
    private static final Material[] CORALS = {
        Material.TUBE_CORAL, Material.BRAIN_CORAL, Material.BUBBLE_CORAL,
        Material.FIRE_CORAL, Material.HORN_CORAL, Material.SEA_PICKLE
    };

    public CoralFinderEnchant() { super("coral_finder", "Coral Finder", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (Math.random() < cfgd("chance", CHANCE[level - 1])) {
            Material mat = CORALS[(int) (Math.random() * CORALS.length)];
            player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(mat));
        }
    }

    @Override public String getDescription() { return "Chance to catch coral and sea items."; }
    @Override public String getDescription(int level) {
        return "§a" + (int)(CHANCE[level - 1] * 100) + "%§7 chance to catch coral or sea pickles."; }
}
