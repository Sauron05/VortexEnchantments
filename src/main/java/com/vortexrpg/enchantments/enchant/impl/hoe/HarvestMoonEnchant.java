package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** HarvestMoon: 6PM–6AM in-game: 2×/2.5×/3× all crop yield. */
public class HarvestMoonEnchant extends VortexEnchant {
    private static final double[] MULT = {2.0, 2.5, 3.0};
    private static final long NIGHT_START = 12000L;
    private static final long NIGHT_END = 24000L;

    public HarvestMoonEnchant() { super("harvest_moon", "Harvest Moon", EnchantRarity.EPIC, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onHarvest(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        long time = player.getWorld().getTime();
        if (time < NIGHT_START || time > NIGHT_END) return;
        double mult = cfg("night_multiplier", MULT[level-1]);
        // Multiply all drops
        event.setDropItems(false);
        for (ItemStack drop : event.getBlock().getDrops()) {
            int extra = (int) Math.floor(drop.getAmount() * (mult - 1.0));
            drop.setAmount(drop.getAmount() + extra);
            player.getWorld().dropItemNaturally(event.getBlock().getLocation(), drop);
        }
    }

    @Override public String getDescription() { return "Night harvests yield far more crops."; }
    @Override public String getDescription(int level) {
        return "§7§a" + MULT[level-1] + "x§7 crop yields during night (6PM–6AM)."; }
}
