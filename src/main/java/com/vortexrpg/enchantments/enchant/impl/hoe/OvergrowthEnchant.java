package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

/** Overgrowth: Crops within 4 blocks can grow to a super stage for 2×/2.5×/3× drops. */
public class OvergrowthEnchant extends VortexEnchant {
    private static final double[] MULT = {2.0, 2.5, 3.0};
    private static final int RADIUS = 4;
    public static final String META_KEY = "ve_overgrowth_mult";

    public OvergrowthEnchant() { super("overgrowth", "Overgrowth", EnchantRarity.EPIC, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        int radius = cfgi("radius", RADIUS);
        double mult = cfg("super_multiplier", MULT[level-1]);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block b = player.getLocation().clone().add(x, 0, z).getBlock();
                if (b.getBlockData() instanceof Ageable ageable) {
                    if (ageable.getAge() >= ageable.getMaximumAge()) {
                        b.setMetadata(META_KEY, new FixedMetadataValue(plugin, mult));
                    }
                }
            }
        }
    }

    @Override public String getDescription() { return "Crops overgrow for massive bonus drops."; }
    @Override public String getDescription(int level) {
        return "§7Max-stage crops within §a" + RADIUS + "§7 blocks yield §a" + MULT[level-1] + "x§7 drops."; }
}
