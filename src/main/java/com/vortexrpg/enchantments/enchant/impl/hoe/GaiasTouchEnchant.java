package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Gaia's Touch: Every N harvests triggers a massive growth burst in radius. */
public class GaiasTouchEnchant extends VortexEnchant {

    public GaiasTouchEnchant() { super("gaias_touch", "Gaia's Touch", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!(event.getBlock().getBlockData() instanceof Ageable)) return;
        String key = "gaias_touch_count";
        int count = plugin.getPlayerDataManager().getInt(player.getUniqueId(), key) + 1;
        int threshold = cfgi("threshold", 20 - level * 3);
        if (count < threshold) {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), key, count);
            return;
        }
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), key, 0);
        int radius = cfgi("radius", 5 + level * 2);
        Block center = player.getLocation().getBlock();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block b = center.getRelative(x, 0, z);
                if (b.getBlockData() instanceof Ageable) {
                    for (int i = 0; i < level + 1; i++) {
                        b.applyBoneMeal(BlockFace.UP);
                    }
                }
                if (b.getType().name().endsWith("_SAPLING")) {
                    b.applyBoneMeal(BlockFace.UP);
                }
            }
        }
        SoundUtil.play(player.getLocation(), Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1.0f, 1.5f);
        ParticleUtil.burst(player.getLocation().add(0, 1, 0), Particle.HAPPY_VILLAGER, 50, radius);
    }

    @Override public String getDescription() { return "Periodic massive growth burst."; }
    @Override public String getDescription(int level) {
        return "§7Every §e" + (20 - level * 3) + "§7 harvests: §amassive growth§7 in " + (5 + level * 2) + "b."; }
}
