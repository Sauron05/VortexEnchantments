package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Allseer: Passive X-ray vision through walls for ores (shows particles at ore locations).
 * Only works for ores within a very small radius.
 */
public class AllseerEnchant extends VortexEnchant {
    public AllseerEnchant() { super("allseer", "Allseer", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        int radius = cfgi("radius", 3 + level * 2);
        org.bukkit.Location loc = player.getLocation();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    org.bukkit.block.Block block = loc.clone().add(x, y, z).getBlock();
                    if (isOre(block.getType())) {
                        ParticleUtil.spawn(block.getLocation().add(0.5, 0.5, 0.5), Particle.SMALL_FLAME, 1, 0.0);
                    }
                }
            }
        }
    }

    private boolean isOre(org.bukkit.Material mat) {
        String name = mat.name();
        return name.endsWith("_ORE") || name.equals("ANCIENT_DEBRIS");
    }

    @Override public String getDescription(int level) {
        return "§7Reveals ores within §a" + (3 + level * 2) + " §7blocks with particles.";
    }
}
