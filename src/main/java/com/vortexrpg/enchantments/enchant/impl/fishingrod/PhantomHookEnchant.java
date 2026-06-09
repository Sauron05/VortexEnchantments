package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** PhantomHook: Chance to catch rare loot from any fish catch. */
public class PhantomHookEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.10, 0.15, 0.20};
    private static final Material[] RARE_LOOT = {
        Material.ENDER_PEARL, Material.BLAZE_ROD, Material.GHAST_TEAR,
        Material.PHANTOM_MEMBRANE, Material.EXPERIENCE_BOTTLE
    };

    public PhantomHookEnchant() { super("phantom_hook", "Phantom Hook", EnchantRarity.EPIC, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (Math.random() < cfgd("chance", CHANCE[level - 1])) {
            Material mat = RARE_LOOT[(int) (Math.random() * RARE_LOOT.length)];
            player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(mat));
        }
    }

    @Override public String getDescription() { return "Chance for rare otherworldly loot."; }
    @Override public String getDescription(int level) {
        return "§a" + (int)(CHANCE[level - 1] * 100) + "%§7 chance to fish up §dphantom realm§7 items."; }
}
