package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.entity.Player;

import java.util.List;

/** DroughtGuard: Nearby farmland never dries out (restores moisture each passive tick). */
public class DroughtGuardEnchant extends VortexEnchant {
    private static final int RADIUS = 6;

    public DroughtGuardEnchant() { super("drought_guard", "Drought Guard", EnchantRarity.UNCOMMON, 1, List.of(ItemTarget.HOE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        int radius = cfgi("radius", RADIUS);
        // Inline scan
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block below = player.getLocation().clone().add(x, -1, z).getBlock();
                if (below.getBlockData() instanceof Farmland fl) {
                    if (fl.getMoisture() < fl.getMaximumMoisture()) {
                        fl.setMoisture(fl.getMaximumMoisture());
                        below.setBlockData(fl);
                    }
                }
            }
        }
    }

    @Override public String getDescription() { return "Nearby farmland never dries out."; }
    @Override public String getDescription(int level) { return "§7Farmland within §a" + RADIUS + " §7blocks stays permanently hydrated."; }
}
