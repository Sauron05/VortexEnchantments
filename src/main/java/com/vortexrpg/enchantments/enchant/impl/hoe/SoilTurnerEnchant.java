package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/** Soil Turner: Tilling also tills adjacent blocks. */
public class SoilTurnerEnchant extends VortexEnchant {

    public SoilTurnerEnchant() { super("soil_turner", "Soil Turner", EnchantRarity.COMMON, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        Block clicked = event.getClickedBlock();
        if (clicked == null) return;
        if (clicked.getType() != Material.DIRT && clicked.getType() != Material.GRASS_BLOCK) return;
        int radius = cfgi("radius", level);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x == 0 && z == 0) continue;
                Block b = clicked.getRelative(x, 0, z);
                if ((b.getType() == Material.DIRT || b.getType() == Material.GRASS_BLOCK)
                        && b.getRelative(0, 1, 0).getType().isAir()) {
                    b.setType(Material.FARMLAND);
                }
            }
        }
    }

    @Override public String getDescription() { return "Tilling also tills adjacent blocks."; }
    @Override public String getDescription(int level) {
        return "§7Till: converts soil in §e" + level + "§7 block radius."; }
}
