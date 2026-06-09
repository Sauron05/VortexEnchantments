package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Cave Sense: Periodic sound alert when air pockets are nearby. */
public class CaveSenseEnchant extends VortexEnchant {
    private static final int[] INTERVAL = {10, 8, 5};

    public CaveSenseEnchant() { super("cave_sense", "Cave Sense", EnchantRarity.RARE, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        int count = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "cave_sense_count") + 1;
        int interval = cfgi("interval", INTERVAL[level - 1]);
        if (count >= interval) {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "cave_sense_count", 0);
            int radius = cfgi("radius", 5);
            Location center = event.getBlock().getLocation();
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        Block b = center.getBlock().getRelative(x, y, z);
                        if (b.getType().isAir() && !b.equals(event.getBlock())) {
                            SoundUtil.play(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.8f, 1.2f);
                            return;
                        }
                    }
                }
            }
        } else {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "cave_sense_count", count);
        }
    }

    @Override public String getDescription() { return "Sound alert when caves are nearby."; }
    @Override public String getDescription(int level) {
        return "§7Every §a" + INTERVAL[level - 1] + " blocks§7: chime if cave nearby."; }
}
