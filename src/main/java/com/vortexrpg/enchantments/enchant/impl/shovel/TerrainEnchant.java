package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/** Terrain: Right-click creates 3-wide path strip as you walk. */
public class TerrainEnchant extends VortexEnchant {
    public TerrainEnchant() { super("terrain", "Terrain", EnchantRarity.COMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getClickedBlock() == null) return;
        if (isOnCooldown(player)) return;
        int width = cfgi("path_width_" + level, 1 + level);
        int length = cfgi("max_length", 8);
        setCooldownSeconds(player, 1);
        org.bukkit.util.Vector forward = player.getLocation().getDirection().setY(0).normalize();
        org.bukkit.Location loc = player.getLocation();
        for (int i = 0; i < length; i++) {
            for (int w = -width/2; w <= width/2; w++) {
                org.bukkit.util.Vector side = rotateY(forward, 90).multiply(w);
                Block b = loc.clone().add(forward.clone().multiply(i)).add(side).getBlock().getRelative(BlockFace.DOWN);
                if (b.getType() == Material.GRASS_BLOCK || b.getType() == Material.DIRT) {
                    b.setType(Material.DIRT_PATH);
                }
            }
        }
    }

    private org.bukkit.util.Vector rotateY(org.bukkit.util.Vector v, double degrees) {
        double rad = Math.toRadians(degrees);
        double cos = Math.cos(rad), sin = Math.sin(rad);
        return new org.bukkit.util.Vector(v.getX() * cos - v.getZ() * sin, v.getY(), v.getX() * sin + v.getZ() * cos);
    }

    @Override public String getDescription() { return "Right-click to paint a dirt path strip ahead."; }
    @Override public String getDescription(int level) {
        return "§7Right-click: paint §e" + (1+level) + "-wide§7 path strip ahead."; }
}
