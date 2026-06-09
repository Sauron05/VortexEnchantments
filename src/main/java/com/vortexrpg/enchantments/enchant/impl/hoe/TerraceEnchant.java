package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/** Terrace: Right-click a hillside to create stepped farmland. */
public class TerraceEnchant extends VortexEnchant {

    public TerraceEnchant() { super("terrace", "Terrace", EnchantRarity.EPIC, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (block.getType() != Material.DIRT && block.getType() != Material.GRASS_BLOCK) return;
        if (isOnCooldown(player)) return;
        int steps = cfgi("steps", 1 + level);
        var dir = player.getFacing().getDirection();
        for (int i = 0; i < steps; i++) {
            Block step = block.getRelative((int)(dir.getX() * i), -i, (int)(dir.getZ() * i));
            if (step.getType() == Material.DIRT || step.getType() == Material.GRASS_BLOCK) {
                step.setType(Material.FARMLAND);
                Block above = step.getRelative(0, 1, 0);
                if (!above.getType().isAir() && above.getType() != Material.FARMLAND) {
                    above.setType(Material.AIR);
                }
            }
        }
        setCooldownFromConfig(player, "cooldown", 5);
    }

    @Override public String getDescription() { return "Right-click to create terraced farmland."; }
    @Override public String getDescription(int level) {
        return "§7Create §a" + (1 + level) + "§7 stepped farmland tiers."; }
}
