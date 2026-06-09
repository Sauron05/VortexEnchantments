package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** EchoLocate: Every 25/20/15 blocks mined, sound ping reveals nearest spawner within 32 blocks. */
public class EchoLocateEnchant extends VortexEnchant {
    private static final int[] INTERVAL = {25, 20, 15};

    public EchoLocateEnchant() { super("echolocate", "Echo Locate", EnchantRarity.EPIC, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        int interval = cfgi("interval_" + level, INTERVAL[level-1]);
        int count = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "echolocate_count") + 1;
        if (count < interval) {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "echolocate_count", count);
            return;
        }
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "echolocate_count", 0);
        double radius = cfg("reveal_radius", 32.0);
        // Find nearest spawner
        Location origin = player.getLocation();
        for (int x = -(int)radius; x <= radius; x++) {
            for (int y = -(int)radius; y <= radius; y++) {
                for (int z = -(int)radius; z <= radius; z++) {
                    Block b = origin.clone().add(x, y, z).getBlock();
                    if (b.getType() == Material.SPAWNER) {
                        player.playSound(b.getLocation(), org.bukkit.Sound.BLOCK_BEACON_POWER_SELECT, 0.6f, 1.5f);
                        // Draw a particle beam toward spawner
                        for (double t = 0; t < 1.0; t += 0.05) {
                            Location pt = origin.clone().add(b.getLocation().toVector()
                                .subtract(origin.toVector()).multiply(t));
                            origin.getWorld().spawnParticle(org.bukkit.Particle.SONIC_BOOM, pt, 1, 0, 0, 0, 0);
                        }
                        return;
                    }
                }
            }
        }
    }

    @Override public String getDescription() { return "Periodically pings nearest spawner."; }
    @Override public String getDescription(int level) {
        return "§7Every §e" + INTERVAL[level-1] + " blocks§7: sound ping toward nearest spawner in §a32 blocks§7."; }
}
