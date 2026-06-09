package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.Random;

/**
 * Taxidermy — Fishing Rod (Rare, Max 3)
 * 15/20/25% chance to catch a mob head or decorative skull from the water.
 */
public class TaxidermyEnchant extends VortexEnchant {

    private static final Random RNG = new Random();
    private static final Material[] SKULLS = {
        Material.SKELETON_SKULL, Material.ZOMBIE_HEAD, Material.CREEPER_HEAD,
        Material.PIGLIN_HEAD, Material.PLAYER_HEAD
    };

    public TaxidermyEnchant() {
        super("taxidermy", "Taxidermy", "fishingrod");
    }

    @Override
    public String getTier() { return "RARE"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] pct = {15, 20, 25};
        return "§e" + pct[level - 1] + "%§7 chance to reel up a mob skull from the depths.";
    }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        double[] chances = {0.15, 0.20, 0.25};
        if (RNG.nextDouble() < cfgd("chance", chances[level - 1])) {
            Material skull = SKULLS[RNG.nextInt(SKULLS.length)];
            player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(skull));
            player.sendMessage("§6[Taxidermy] §eYou found something unusual below the surface!");
        }
    }
}
