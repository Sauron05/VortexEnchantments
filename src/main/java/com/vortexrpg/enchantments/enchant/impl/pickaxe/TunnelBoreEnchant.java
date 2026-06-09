package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Tunnel Bore: Mines 1×3 tunnel in facing direction. */
public class TunnelBoreEnchant extends VortexEnchant {
    public TunnelBoreEnchant() { super("tunnel_bore", "Tunnel Bore", EnchantRarity.RARE, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        BlockFace face = getCardinalFace(player);
        Block base = event.getBlock();
        int depth = cfgi("depth", level);
        for (int i = 1; i <= depth; i++) {
            Block ahead = base.getRelative(face, i);
            if (ahead.getType().isAir() || ahead.getType() == org.bukkit.Material.BEDROCK) continue;
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

    @Override public String getDescription() { return "Mines tunnel in facing direction."; }
    @Override public String getDescription(int level) {
        return "§7Mine §a" + (1 + level) + " deep§7 tunnel in facing direction."; }
}
