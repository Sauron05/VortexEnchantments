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

/** Drill Charge: Every N blocks, next break mines 3-deep tunnel. */
public class DrillChargeEnchant extends VortexEnchant {
    private static final int[] THRESHOLD = {40, 30, 20};

    public DrillChargeEnchant() { super("drill_charge", "Drill Charge", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        int threshold = cfgi("threshold", THRESHOLD[level - 1]);
        int count = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "drill_charge_count") + 1;
        if (count >= threshold) {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "drill_charge_count", 0);
            BlockFace face = getCardinalFace(player);
            Block base = event.getBlock();
            for (int i = 1; i <= 3; i++) {
                Block ahead = base.getRelative(face, i);
                if (ahead.getType() != Material.BEDROCK && !ahead.getType().isAir()) {
                    ahead.breakNaturally(player.getInventory().getItemInMainHand());
                }
            }
        } else {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "drill_charge_count", count);
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

    @Override public String getDescription() { return "Periodic tunneling charge."; }
    @Override public String getDescription(int level) {
        return "§7Every §e" + THRESHOLD[level - 1] + " blocks§7: 3-deep tunnel charge."; }
}
