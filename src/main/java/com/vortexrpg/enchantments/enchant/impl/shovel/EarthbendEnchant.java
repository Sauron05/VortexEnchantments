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

/** Earthbend: Right-click to raise dirt barrier columns. */
public class EarthbendEnchant extends VortexEnchant {
    public EarthbendEnchant() { super("earthbend", "Earthbend", EnchantRarity.EPIC, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getClickedBlock() == null) return;
        Material mat = event.getClickedBlock().getType();
        if (mat != Material.DIRT && mat != Material.GRASS_BLOCK && mat != Material.MUD) return;
        if (isOnCooldown(player)) return;
        setCooldownFromConfig(player, "cooldown", 15 - level * 2);
        int height = cfgi("height", 1 + level);
        Block base = event.getClickedBlock();
        for (int h = 1; h <= height; h++) {
            Block above = base.getRelative(BlockFace.UP, h);
            if (above.getType().isAir()) {
                above.setType(Material.DIRT);
            }
        }
    }

    @Override public String getDescription() { return "Right-click dirt to raise barrier."; }
    @Override public String getDescription(int level) {
        return "§7Right-click dirt: raise §a" + (1 + level) + " high§7 pillar."; }
}
