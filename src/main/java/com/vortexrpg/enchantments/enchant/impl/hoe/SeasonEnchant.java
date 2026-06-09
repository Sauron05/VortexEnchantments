package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;

import java.util.List;

/** Season: Crops within 6/8/10 blocks grow faster while hoe is in hotbar. */
public class SeasonEnchant extends VortexEnchant {
    private static final double[] RADIUS = {6, 8, 10};

    public SeasonEnchant() { super("season", "Season", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        double radius = cfg("radius", RADIUS[level-1]);
        double bonus = cfg("growth_bonus_" + level, 0.15 + level * 0.05);
        if (Math.random() > bonus) return; // probabilistic growth boost each 1s tick
        Location origin = player.getLocation();
        int r = (int) radius;
        for (int x = -r; x <= r; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -r; z <= r; z++) {
                    Block b = origin.clone().add(x, y, z).getBlock();
                    if (b.getBlockData() instanceof Ageable ageable) {
                        if (ageable.getAge() < ageable.getMaximumAge()) {
                            ageable.setAge(ageable.getAge() + 1);
                            b.setBlockData(ageable);
                        }
                    }
                }
            }
        }
    }

    @Override public String getDescription() { return "Crops near you grow faster with hoe in hotbar."; }
    @Override public String getDescription(int level) {
        return "§7Crops within §a" + (int)RADIUS[level-1] + " blocks§7 grow §e" + (int)((0.15+level*0.05)*100) + "%§7 faster."; }
}
