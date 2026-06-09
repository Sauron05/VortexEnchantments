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
import java.util.Set;

/** Levee: Right-click dirt/sand near water to raise 2 blocks instantly. */
public class LeveeEnchant extends VortexEnchant {
    private static final Set<Material> VALID = Set.of(Material.DIRT, Material.SAND, Material.GRAVEL,
        Material.GRASS_BLOCK, Material.COARSE_DIRT);

    public LeveeEnchant() { super("levee", "Levee", EnchantRarity.RARE, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getClickedBlock() == null) return;
        if (!VALID.contains(event.getClickedBlock().getType())) return;
        if (isOnCooldown(player)) return;
        // Check adjacent water
        Block clicked = event.getClickedBlock();
        boolean nearWater = false;
        for (BlockFace face : new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST}) {
            if (clicked.getRelative(face).getType() == Material.WATER) { nearWater = true; break; }
        }
        if (!nearWater) return;
        setCooldownSeconds(player, cfgi("cooldown", 5));
        int height = cfgi("raise_height", 2);
        Block top = clicked;
        for (int h = 0; h < height; h++) {
            top = top.getRelative(BlockFace.UP);
            if (top.getType() == Material.AIR) top.setType(Material.DIRT);
        }
    }

    @Override public String getDescription() { return "Right-click near water to raise a levee."; }
    @Override public String getDescription(int level) { return "§7Right-click dirt/sand near water: raise §e2 blocks§7 instantly."; }
}
