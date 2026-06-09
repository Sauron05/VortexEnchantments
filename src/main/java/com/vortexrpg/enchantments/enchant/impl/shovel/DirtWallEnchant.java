package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.Set;

/** Dirt Wall: Mining soft blocks while sneaking raises a wall ahead. */
public class DirtWallEnchant extends VortexEnchant {
    private static final Set<Material> SOFT = Set.of(
            Material.DIRT, Material.GRASS_BLOCK, Material.SAND, Material.GRAVEL);

    public DirtWallEnchant() { super("dirt_wall", "Dirt Wall", EnchantRarity.RARE, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isSneaking()) return;
        if (!SOFT.contains(event.getBlock().getType())) return;
        if (isOnCooldown(player)) return;
        setCooldownFromConfig(player, "cooldown", 15);
        org.bukkit.block.BlockFace face = getCardinalFace(player);
        Block base = event.getBlock().getRelative(face, 2);
        int height = cfgi("height", 1 + level);
        for (int h = 0; h < height; h++) {
            for (int w = -1; w <= 1; w++) {
                Block wall;
                if (face == org.bukkit.block.BlockFace.NORTH || face == org.bukkit.block.BlockFace.SOUTH) {
                    wall = base.getRelative(w, h, 0);
                } else {
                    wall = base.getRelative(0, h, w);
                }
                if (wall.getType().isAir()) {
                    wall.setType(Material.DIRT);
                }
            }
        }
    }

    private org.bukkit.block.BlockFace getCardinalFace(Player player) {
        float yaw = player.getLocation().getYaw();
        yaw = ((yaw % 360) + 360) % 360;
        if (yaw >= 315 || yaw < 45) return org.bukkit.block.BlockFace.SOUTH;
        if (yaw >= 45 && yaw < 135) return org.bukkit.block.BlockFace.WEST;
        if (yaw >= 135 && yaw < 225) return org.bukkit.block.BlockFace.NORTH;
        return org.bukkit.block.BlockFace.EAST;
    }

    @Override public String getDescription() { return "Sneak-mining raises a dirt wall ahead."; }
    @Override public String getDescription(int level) {
        return "§7Sneak+dig: raise §a3×" + (1 + level) + "§7 dirt wall ahead."; }
}
