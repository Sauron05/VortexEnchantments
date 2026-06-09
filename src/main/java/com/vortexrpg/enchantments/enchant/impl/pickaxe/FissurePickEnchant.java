package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Fissure Pick: Break a line of blocks in facing direction. */
public class FissurePickEnchant extends VortexEnchant {
    private static final int[] LENGTH = {3, 4, 5};

    public FissurePickEnchant() { super("fissure_pick", "Fissure", EnchantRarity.EPIC, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        BlockFace face = getCardinalFace(player);
        int length = cfgi("length", LENGTH[level - 1]);
        Block base = event.getBlock();
        for (int i = 1; i <= length; i++) {
            Block ahead = base.getRelative(face, i);
            if (ahead.getType() == Material.BEDROCK || ahead.getType().isAir()) continue;
            ahead.breakNaturally(player.getInventory().getItemInMainHand());
        }
    }

    private BlockFace getCardinalFace(Player player) {
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();
        if (pitch < -45) return BlockFace.UP;
        if (pitch > 45) return BlockFace.DOWN;
        yaw = ((yaw % 360) + 360) % 360;
        if (yaw >= 315 || yaw < 45) return BlockFace.SOUTH;
        if (yaw >= 45 && yaw < 135) return BlockFace.WEST;
        if (yaw >= 135 && yaw < 225) return BlockFace.NORTH;
        return BlockFace.EAST;
    }

    @Override public String getDescription() { return "Break a line of blocks in facing direction."; }
    @Override public String getDescription(int level) {
        return "§7Fissure: §a" + LENGTH[level - 1] + " blocks§7 deep in facing direction."; }
}
