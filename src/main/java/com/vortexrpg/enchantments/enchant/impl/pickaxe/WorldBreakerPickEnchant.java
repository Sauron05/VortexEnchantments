package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** World Breaker Pick: Mine all blocks in a 3-block radius sphere. */
public class WorldBreakerPickEnchant extends VortexEnchant {
    public WorldBreakerPickEnchant() { super("world_breaker_pick", "World Breaker", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        int radius = cfgi("radius", 3);
        Block center = event.getBlock();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z > radius * radius) continue;
                    if (x == 0 && y == 0 && z == 0) continue;
                    Block b = center.getRelative(x, y, z);
                    Material m = b.getType();
                    if (m == Material.BEDROCK || m.isAir()) continue;
                    b.breakNaturally(player.getInventory().getItemInMainHand());
                }
            }
        }
    }

    @Override public String getDescription() { return "Mine all blocks in a sphere."; }
    @Override public String getDescription(int level) {
        return "§7Mine all blocks in §a3-block§7 radius sphere."; }
}
