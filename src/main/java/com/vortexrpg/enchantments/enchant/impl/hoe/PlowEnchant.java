package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/** Plow: Sprint + right-click tills a 1×3/1×4/1×5 strip in the direction the player faces. */
public class PlowEnchant extends VortexEnchant {
    private static final int[] STRIP = {3, 4, 5};

    public PlowEnchant() { super("plow", "Plow", EnchantRarity.RARE, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isSprinting()) return;
        if (isOnCooldown(player)) return;
        setCooldownSeconds(player, 1);
        int length = cfgi("strip_length", STRIP[level-1]);
        Location base = player.getLocation().getBlock().getLocation();
        var dir = player.getFacing().getDirection();
        for (int i = 0; i < length; i++) {
            Block b = base.clone().add(dir.getX() * i, -1, dir.getZ() * i).getBlock();
            if (b.getType() == Material.DIRT || b.getType() == Material.GRASS_BLOCK) {
                b.setType(Material.FARMLAND);
            }
        }
    }

    @Override public String getDescription() { return "Sprint + right-click to till a long strip."; }
    @Override public String getDescription(int level) {
        return "§7Sprint + right-click to till §a" + STRIP[level-1] + "§7 blocks."; }
}
