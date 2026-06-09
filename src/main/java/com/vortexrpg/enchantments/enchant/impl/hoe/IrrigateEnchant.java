package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/** Irrigate: Tilling near water extends water range for farmland hydration. */
public class IrrigateEnchant extends VortexEnchant {

    public IrrigateEnchant() { super("irrigate", "Irrigate", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (block.getType() != Material.DIRT && block.getType() != Material.GRASS_BLOCK) return;
        int radius = cfgi("radius", 2 + level);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block b = block.getRelative(x, 0, z);
                if (b.getType() == Material.FARMLAND) {
                    org.bukkit.block.data.type.Farmland data = (org.bukkit.block.data.type.Farmland) b.getBlockData();
                    data.setMoisture(data.getMaximumMoisture());
                    b.setBlockData(data);
                }
            }
        }
    }

    @Override public String getDescription() { return "Tilling hydrates nearby farmland."; }
    @Override public String getDescription(int level) {
        return "§7Till: hydrate farmland in §b" + (2 + level) + "§7 block radius."; }
}
