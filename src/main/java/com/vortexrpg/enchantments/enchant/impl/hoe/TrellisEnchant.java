package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/** Trellis: Right-click to grow vines on adjacent blocks. */
public class TrellisEnchant extends VortexEnchant {

    public TrellisEnchant() { super("trellis", "Trellis", EnchantRarity.RARE, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (block.getType().isAir() || !block.getType().isSolid()) return;
        if (isOnCooldown(player)) return;
        int count = 0;
        int max = cfgi("max-vines", level * 2);
        BlockFace[] faces = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
        for (BlockFace face : faces) {
            if (count >= max) break;
            Block adj = block.getRelative(face);
            if (adj.getType().isAir()) {
                adj.setType(Material.VINE);
                count++;
            }
        }
        // Grow vines downward
        for (int y = 1; y <= level && count < max; y++) {
            Block below = block.getRelative(0, -y, 0);
            for (BlockFace face : faces) {
                if (count >= max) break;
                Block side = below.getRelative(face);
                if (side.getType().isAir()) {
                    side.setType(Material.VINE);
                    count++;
                }
            }
        }
        if (count > 0) setCooldownFromConfig(player, "cooldown", 5);
    }

    @Override public String getDescription() { return "Right-click to grow vines on blocks."; }
    @Override public String getDescription(int level) {
        return "§7Right-click: grow §a" + level * 2 + "§7 vines on block."; }
}
