package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Loam: Digging farmland fully hydrates it and boosts nearest crop growth stage. */
public class LoamEnchant extends VortexEnchant {
    public LoamEnchant() { super("loam", "Loam", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getBlock().getType() != Material.FARMLAND) return;
        event.setCancelled(true);
        Block farmland = event.getBlock();
        org.bukkit.block.data.type.Farmland data = (org.bukkit.block.data.type.Farmland) farmland.getBlockData();
        data.setMoisture(data.getMaximumMoisture());
        farmland.setBlockData(data);
        // Boost nearest crop above
        Block above = farmland.getRelative(BlockFace.UP);
        if (above.getBlockData() instanceof Ageable ageable) {
            if (ageable.getAge() < ageable.getMaximumAge()) {
                ageable.setAge(ageable.getAge() + 1);
                above.setBlockData(ageable);
            }
        }
    }

    @Override public String getDescription() { return "Digging farmland fully hydrates it and boosts crops."; }
    @Override public String getDescription(int level) { return "§7Farmland: fully §bhydrates§7 + boosts adjacent crop §e1 growth stage§7."; }
}
