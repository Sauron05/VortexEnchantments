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

/** Burrow: Digging straight down auto-places ladders on adjacent wall. */
public class BurrowEnchant extends VortexEnchant {
    public BurrowEnchant() { super("burrow", "Burrow", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Block broke = event.getBlock();
        // Only trigger when player is digging downward
        if (!player.getLocation().getBlock().equals(broke) && !player.getLocation().getBlock().equals(broke.getRelative(BlockFace.UP))) return;
        // Place ladder on one of the horizontal faces if air
        BlockFace[] faces = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
        for (BlockFace face : faces) {
            Block adj = broke.getRelative(face);
            if (adj.getType() != Material.AIR) {
                Block ladderBlock = broke;
                if (ladderBlock.getType() == Material.AIR) {
                    ladderBlock.setType(Material.LADDER);
                    org.bukkit.block.data.type.Ladder ladderData = (org.bukkit.block.data.type.Ladder) ladderBlock.getBlockData();
                    ladderData.setFacing(face.getOppositeFace());
                    ladderBlock.setBlockData(ladderData);
                    break;
                }
            }
        }
    }

    @Override public String getDescription() { return "Auto-places ladders when digging down."; }
    @Override public String getDescription(int level) { return "§7Digging down auto-places §6ladders§7 on adjacent wall."; }
}
