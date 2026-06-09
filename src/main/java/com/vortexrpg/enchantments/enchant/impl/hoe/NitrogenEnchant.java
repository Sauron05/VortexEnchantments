package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/** Nitrogen: Tilling adjacent to existing farmland enriches it for 1.5×/1.75×/2× crop drops. */
public class NitrogenEnchant extends VortexEnchant {
    private static final double[] MULT = {1.5, 1.75, 2.0};

    public NitrogenEnchant() { super("nitrogen", "Nitrogen", EnchantRarity.RARE, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Block clicked = event.getClickedBlock();
        if (clicked == null) return;
        if (clicked.getType() != Material.DIRT && clicked.getType() != Material.GRASS_BLOCK) return;
        // Check for adjacent farmland
        int[] dx = {1,-1,0,0};
        int[] dz = {0,0,1,-1};
        boolean adjacent = false;
        for (int i = 0; i < 4; i++) {
            if (clicked.getRelative(dx[i], 0, dz[i]).getType() == Material.FARMLAND) {
                adjacent = true; break;
            }
        }
        if (!adjacent) return;
        double mult = cfg("bonus_multiplier", MULT[level-1]);
        // Tag block location via PDC on a temporary entity or using block's tile entity
        // We tag the player's data associated with block coords for simplicity
        plugin.getPlayerDataManager().setDouble(player.getUniqueId(), "nitrogen_" + clicked.getX() + "_" + clicked.getZ(), mult);
    }

    @Override public String getDescription() { return "Enriches farmland for bonus crop drops."; }
    @Override public String getDescription(int level) {
        return "§7Adjacent farmland gives §a" + MULT[level-1] + "x§7 crop drops."; }
}
