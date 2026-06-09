package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.Set;

/** Canyon Dig: Mine 1×3 line of soft blocks in facing direction. */
public class CanyonDigEnchant extends VortexEnchant {
    private static final Set<Material> SOFT = Set.of(
            Material.DIRT, Material.GRASS_BLOCK, Material.SAND, Material.GRAVEL,
            Material.RED_SAND, Material.COARSE_DIRT, Material.CLAY, Material.SOUL_SAND);

    public CanyonDigEnchant() { super("canyon_dig", "Canyon", EnchantRarity.RARE, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!SOFT.contains(event.getBlock().getType())) return;
        BlockFace face = getCardinalFace(player);
        Block base = event.getBlock();
        for (int i = 1; i <= level; i++) {
            Block ahead = base.getRelative(face, i);
            if (SOFT.contains(ahead.getType())) {
                ahead.breakNaturally(player.getInventory().getItemInMainHand());
            }
        }
    }

    private BlockFace getCardinalFace(Player player) {
        float yaw = player.getLocation().getYaw();
        yaw = ((yaw % 360) + 360) % 360;
        if (yaw >= 315 || yaw < 45) return BlockFace.SOUTH;
        if (yaw >= 45 && yaw < 135) return BlockFace.WEST;
        if (yaw >= 135 && yaw < 225) return BlockFace.NORTH;
        return BlockFace.EAST;
    }

    @Override public String getDescription() { return "Mine soft blocks in a line forward."; }
    @Override public String getDescription(int level) {
        return "§7Mine §a" + (1 + level) + " soft blocks§7 in facing direction."; }
}
