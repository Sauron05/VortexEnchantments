package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.Random;

/**
 * Bycatch — Fishing Rod (Uncommon, Max 3)
 * Each catch has a 15/25/35% chance to also bring up a random junk or treasure item (flotsam).
 */
public class BycatchEnchant extends VortexEnchant {

    private static final Random RNG = new Random();
    private static final Material[] JUNK = {
        Material.LILY_PAD, Material.BOWL, Material.LEATHER, Material.ROTTEN_FLESH,
        Material.BONE, Material.INK_SAC, Material.TRIPWIRE_HOOK, Material.WATER_BUCKET
    };

    public BycatchEnchant() {
        super("bycatch", "Bycatch", "fishingrod");
    }

    @Override
    public String getTier() { return "UNCOMMON"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] pct = {15, 25, 35};
        return "§e" + pct[level - 1] + "%§7 chance to reel in extra flotsam alongside your catch.";
    }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        double[] chances = {0.15, 0.25, 0.35};
        if (RNG.nextDouble() < cfgd("chance", chances[level - 1])) {
            Material mat = JUNK[RNG.nextInt(JUNK.length)];
            player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(mat));
        }
    }
}
